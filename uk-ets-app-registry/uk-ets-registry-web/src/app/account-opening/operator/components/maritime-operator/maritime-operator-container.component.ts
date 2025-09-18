import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable, map } from 'rxjs';
import { MaritimeOperator, OperatorType } from '@shared/model/account/operator';
import {
  selectOperator,
  selectOperatorInputBackLink,
} from '@account-opening/operator/operator.selector';
import { ErrorSummary } from '@shared/error-summary/error-summary';
import { MainWizardRoutes } from '@account-opening/main-wizard.routes';
import { OperatorWizardRoutes } from '@account-opening/operator/operator-wizard-properties';
import { canGoBack, errors } from '@shared/shared.action';
import { take } from 'rxjs/operators';
import { AccountOpeningOperatorActions } from '@account-opening/operator/actions';
import { ErrorDetail } from '@shared/error-summary/error-detail';
import { isSeniorOrJuniorAdmin } from '@registry-web/auth/auth.selector';
import { selectRegistryConfigurationProperty } from '@shared/shared.selector';

/*
TODO: app-aircraft-container should be merged to  app-installation-container and renamed to app-operator-container
There only one thing missing  for this code merge is to listen to the setAccountType action and  setOperator so it is known when opening the form

if (accountType=AOHA) - > setOperator(Aircraft)
if( acccountType=OHA)-> if not junior/senior admin -> setOperator(installation)
 */

@Component({
  selector: 'app-maritime-container',
  template: `
    <app-maritime-input
      [maritime]="maritimeOperator$ | async"
      [isSeniorOrJuniorAdmin]="isSeniorOrJuniorAdmin$ | async"
      [title]="'Add the Maritime Operator details'"
      [headerTitle]="'Add the Maritime Operator'"
      [emissionStartYear]="registryConfig$(emissionsYearKey) | async"
      (maritimeOutput)="onContinue($event)"
      (errorDetails)="onError($event)"
    ></app-maritime-input>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MaritimeOperatorContainerComponent implements OnInit {
  maritimeOperator$: Observable<MaritimeOperator> = this.store.select(
    selectOperator
  ) as Observable<MaritimeOperator>;
  isSeniorOrJuniorAdmin$: Observable<boolean> = this.store.select(isSeniorOrJuniorAdmin);
  registryConfig$ = (key: string): Observable<number> =>
    this.store.select(selectRegistryConfigurationProperty, { property: key }).pipe(
      map(value => Number(value))
    );

  readonly mainWizardRoute = MainWizardRoutes.TASK_LIST;
  readonly overviewRoute = OperatorWizardRoutes.OVERVIEW;
  readonly emissionsYearKey = 'emissions-maritime-starting-year';

  constructor(
    private route: ActivatedRoute,
    private _router: Router,
    private store: Store
  ) {}

  ngOnInit() {
    this.store
      .select(selectOperatorInputBackLink)
      .pipe(take(1))
      .subscribe((backLink) => {
        this.store.dispatch(canGoBack({ goBackRoute: backLink }));
      });
    this.store.dispatch(
      AccountOpeningOperatorActions.completeWizard({ complete: false })
    );
  }

  onContinue(value: MaritimeOperator) {
    this.store.dispatch(
      AccountOpeningOperatorActions.fetchExistsImoAndMonitoringPlan({
        operator: value,
      })
    );
  }

  onError(details: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: details,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }

  protected readonly OperatorType = OperatorType;
}
