import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import {
  selectInstallationToBeTransferred,
  selectOperator,
  selectOperatorCompleted,
  selectOperatorType,
} from '@account-opening/operator/operator.selector';
import {
  InstallationActivityType,
  Operator,
  OperatorType,
} from '@shared/model/account/operator';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { OperatorWizardRoutes } from '@account-opening/operator/operator-wizard-properties';
import { MainWizardRoutes } from '@account-opening/main-wizard.routes';
import { AccountOpeningOperatorActions } from '@account-opening/operator/actions';
import { take } from 'rxjs/operators';
import { canGoBack } from '@shared/shared.action';
import { isSeniorOrJuniorAdmin } from '@registry-web/auth/auth.selector';

@Component({
  selector: 'app-operator-overview-container',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `<app-operator-overview
    [operator]="operator$ | async"
    [operatorCompleted]="operatorCompleted$ | async"
    [installationToBeTransferred]="installationToBeTransferred$ | async"
    [isSeniorOrJuniorAdmin]="isSeniorOrJuniorAdmin$ | async"
    (editEmitter)="onEdit($event)"
    (applyEmitter)="onApply()"
    (deleteEmitter)="onDelete()"
  >
  </app-operator-overview>`,
})
export class OverviewContainerComponent implements OnInit {
  operatorType$: Observable<OperatorType> = this.store.select(
    selectOperatorType
  ) as Observable<OperatorType>;
  operatorCompleted$: Observable<boolean> = this.store.select(
    selectOperatorCompleted
  ) as Observable<boolean>;

  operator$: Observable<Operator> = this.store.select(selectOperator);

  installationToBeTransferred$: Observable<Operator> = this.store.select(
    selectInstallationToBeTransferred
  );

  isSeniorOrJuniorAdmin$: Observable<boolean> = this.store.select(
    isSeniorOrJuniorAdmin
  );

  type = OperatorType;
  operatorWizardRoutes = OperatorWizardRoutes;
  activityTypes = InstallationActivityType;

  readonly mainWizardRoute = MainWizardRoutes.TASK_LIST;
  readonly installationRoute = OperatorWizardRoutes.SELECT_REGULATED_ACTIVITY;
  readonly aircraftOperatorRoute = OperatorWizardRoutes.AIRCRAFT_OPERATOR;
  readonly maritimeOperatorRoute = OperatorWizardRoutes.MARITIME_OPERATOR;

  constructor(
    private route: ActivatedRoute,
    private _router: Router,
    private store: Store
  ) {}

  ngOnInit() {
    this.operatorCompleted$.pipe(take(1)).subscribe((completed) => {
      if (completed) {
        this.store.dispatch(
          canGoBack({
            goBackRoute: this.mainWizardRoute,
            extras: { skipLocationChange: true },
          })
        );
      } else {
        this.operatorType$.pipe(take(1)).subscribe((type) => {
          if (
            type === OperatorType.INSTALLATION ||
            type === OperatorType.INSTALLATION_TRANSFER
          ) {
            this.store.dispatch(
              canGoBack({
                goBackRoute: this.installationRoute,
                extras: { skipLocationChange: true },
              })
            );
          } else if (type === OperatorType.MARITIME_OPERATOR) {
            this.store.dispatch(
              canGoBack({
                goBackRoute: this.maritimeOperatorRoute,
                extras: { skipLocationChange: true },
              })
            );
          } else {
            this.store.dispatch(
              canGoBack({
                goBackRoute: this.aircraftOperatorRoute,
                extras: { skipLocationChange: true },
              })
            );
          }
        });
      }
    });
  }

  onEdit(type: OperatorType) {
    if (type === OperatorType.AIRCRAFT_OPERATOR) {
      this._router.navigate([this.aircraftOperatorRoute], {
        skipLocationChange: true,
      });
    } else if (type === OperatorType.MARITIME_OPERATOR) {
      this._router.navigate([this.maritimeOperatorRoute], {
        skipLocationChange: true,
      });
    } else {
      this._router.navigate([this.installationRoute], {
        skipLocationChange: true,
      });
    }
  }

  onApply() {
    this.store.dispatch(
      AccountOpeningOperatorActions.completeWizard({ complete: true })
    );
    this._router.navigate([this.mainWizardRoute], { skipLocationChange: true });
  }

  onDelete() {
    this.store.dispatch(AccountOpeningOperatorActions.deleteOperator());
    this._router.navigate([this.mainWizardRoute], { skipLocationChange: true });
  }
}
