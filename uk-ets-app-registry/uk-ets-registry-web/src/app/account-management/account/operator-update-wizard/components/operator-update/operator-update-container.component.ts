import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import {
  AircraftOperator,
  Installation,
  Operator,
  OperatorType,
} from '@shared/model/account';
import { canGoBack, errors } from '@shared/shared.action';
import { ErrorSummary } from '@shared/error-summary';
import { selectOperator } from '@operator-update/reducers';
import {
  cancelClicked,
  checkIfExistsInstallationPermitId,
  checkIfExistsAircraftMonitoringPlanId,
} from '@operator-update/actions/operator-update.actions';

@Component({
  selector: 'app-operator-update-container',
  template: `<app-operator-update
    [operatorInfo]="operatorInfo$ | async"
    (cancelEmitter)="onCancel()"
    (errorEmitter)="onErrors($event)"
    (continueEmitter)="onContinue($event)"
  ></app-operator-update> `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OperatorUpdateContainerComponent implements OnInit {
  operatorInfo$: Observable<Operator>;

  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit() {
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.route.snapshot.paramMap.get(
          'accountId'
        )}`,
      })
    );
    this.operatorInfo$ = this.store.select(selectOperator);
  }

  onContinue(value: Operator) {
    if (value.type === OperatorType.INSTALLATION) {
      this.store.dispatch(
        checkIfExistsInstallationPermitId({ operator: value as Installation })
      );
    } else {
      this.store.dispatch(
        checkIfExistsAircraftMonitoringPlanId({
          operator: value as AircraftOperator,
        })
      );
    }
  }

  onErrors(value: ErrorSummary) {
    this.store.dispatch(
      errors({
        errorSummary: value,
      })
    );
  }

  onCancel() {
    this.store.dispatch(
      cancelClicked({ route: this.route.snapshot['_routerState'].url })
    );
  }
}
