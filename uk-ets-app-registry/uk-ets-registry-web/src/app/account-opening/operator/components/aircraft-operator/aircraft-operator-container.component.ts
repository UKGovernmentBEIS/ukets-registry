import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { AircraftOperator } from '@shared/model/account/operator';
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

/*
TODO: app-aircraft-container should be merged to  app-installation-container and renamed to app-operator-container
There only one thing missing  for this code merge is to listen to the setAccountType action and  setOperator so it is known when opening the form

if (accountType=AOHA) - > setOperator(Aircraft)
if( acccountType=OHA)-> if not junior/senior admin -> setOperator(installation)
 */

@Component({
  selector: 'app-aircraft-container',
  template: `
    <app-aircraft-input
      [aircraft]="aircraftOperator$ | async"
      [isSeniorOrJuniorAdmin]="isSeniorOrJuniorAdmin$ | async"
      [title]="'Add the Aircraft Operator details'"
      [headerTitle]="'Add the Aircraft Operator'"
      (aircraftOutput)="onContinue($event)"
      (errorDetails)="onError($event)"
    ></app-aircraft-input>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AircraftOperatorContainerComponent implements OnInit {
  isSeniorOrJuniorAdmin$: Observable<boolean>  = this.store.select(isSeniorOrJuniorAdmin);
  aircraftOperator$: Observable<AircraftOperator> = this.store.select(
    selectOperator
  ) as Observable<AircraftOperator>;

  readonly mainWizardRoute = MainWizardRoutes.TASK_LIST;
  readonly overviewRoute = OperatorWizardRoutes.OVERVIEW;

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

  onContinue(value: AircraftOperator) {
    this.store.dispatch(
      AccountOpeningOperatorActions.fetchExistsMonitoringPlan({
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
}
