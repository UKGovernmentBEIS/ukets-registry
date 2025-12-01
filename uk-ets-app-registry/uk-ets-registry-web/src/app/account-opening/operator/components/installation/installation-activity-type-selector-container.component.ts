import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { Operator } from '@shared/model/account/operator';
import { Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { MainWizardRoutes } from '@account-opening/main-wizard.routes';
import { OperatorWizardRoutes } from '@account-opening/operator/operator-wizard-properties';
import { AccountOpeningOperatorActions } from '@account-opening/operator/actions';
import {
  selectOperator,
  selectOperatorCompleted,
} from '@account-opening/operator/operator.selector';
import { canGoBack, errors } from '@shared/shared.action';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { take } from 'rxjs/operators';

@Component({
  selector: 'app-installation-activity-type-selector-container',
  template: `
    <app-installation-activity-type
      [title]="'Choose the regulated activity'"
      [headerTitle]="'Add the installation information'"
      [operator]="operator$ | async"
      (installationTypeEmitter)="onContinue($event)"
      (errorDetails)="onError($event)"
    >
    </app-installation-activity-type>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InstallationActivityTypeSelectorContainerComponent
  implements OnInit
{
  operator$: Observable<Operator>;
  operatorCompleted$: Observable<boolean>;

  readonly mainWizardRoute = MainWizardRoutes.TASK_LIST;
  readonly prevRoute = OperatorWizardRoutes.INSTALLATION;
  readonly overviewRoute = OperatorWizardRoutes.OVERVIEW;

  constructor(
    private _router: Router,
    private store: Store
  ) {}

  ngOnInit() {
    this.operator$ = this.store.select(selectOperator);
    this.operator$.subscribe((o) => {
      console.log(o);
    });
    this.operatorCompleted$ = this.store.select(selectOperatorCompleted);
    this.operatorCompleted$.pipe(take(1)).subscribe((completed) => {
      if (completed) {
        this.store.dispatch(
          canGoBack({
            goBackRoute: this.overviewRoute,
            extras: { skipLocationChange: true },
          })
        );
      } else {
        this.store.dispatch(
          canGoBack({
            goBackRoute: this.prevRoute,
            extras: { skipLocationChange: true },
          })
        );
      }
    });
    this.store.dispatch(
      AccountOpeningOperatorActions.completeWizard({ complete: false })
    );
  }

  onContinue(operator: Operator) {
    this.store.dispatch(
      AccountOpeningOperatorActions.setOperator({ operator })
    );
    this._router.navigate([OperatorWizardRoutes.OVERVIEW], {
      skipLocationChange: true,
    });
  }

  onError(value: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: value,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
