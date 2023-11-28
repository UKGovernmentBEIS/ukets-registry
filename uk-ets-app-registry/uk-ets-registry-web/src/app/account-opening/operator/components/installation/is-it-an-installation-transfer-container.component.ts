import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { Operator } from '@shared/model/account/operator';
import {
  selectOperator,
  selectOperatorCompleted,
} from '@account-opening/operator/operator.selector';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { MainWizardRoutes } from '@account-opening/main-wizard.routes';
import { OperatorWizardRoutes } from '@account-opening/operator/operator-wizard-properties';
import { canGoBack } from '@shared/shared.action';
import { take } from 'rxjs/operators';
import { AccountOpeningOperatorActions } from '@account-opening/operator/actions';

@Component({
  selector: 'app-is-it-an-installation-transfer-container',
  template: `
    <app-is-it-an-installation-transfer
      [operator]="operator$ | async"
      (installationTypeEmitter)="onContinue($event)"
    >
    </app-is-it-an-installation-transfer>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class IsItAnInstallationTransferContainerComponent implements OnInit {
  operator$: Observable<Operator>;
  operatorCompleted$: Observable<boolean>;

  readonly mainWizardRoute = MainWizardRoutes.TASK_LIST;
  readonly overviewRoute = OperatorWizardRoutes.OVERVIEW;

  constructor(
    private route: ActivatedRoute,
    private _router: Router,
    private store: Store
  ) {}

  ngOnInit() {
    this.operator$ = this.store.select(selectOperator);
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
            goBackRoute: this.mainWizardRoute,
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
    this._router.navigate([OperatorWizardRoutes.INSTALLATION], {
      skipLocationChange: true,
    });
  }
}
