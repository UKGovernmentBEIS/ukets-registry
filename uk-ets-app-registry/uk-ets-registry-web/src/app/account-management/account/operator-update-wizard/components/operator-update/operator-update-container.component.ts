import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { ActivatedRoute } from '@angular/router';
import {map, Observable} from 'rxjs';
import {
  AircraftOperator,
  Installation,
  MaritimeOperator,
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
  checkIfExistsMaritimeImoAndMonitorinfPlan,
} from '@operator-update/actions/operator-update.actions';
import { isSeniorOrJuniorAdmin } from '@registry-web/auth/auth.selector';
import {selectRegistryConfigurationProperty} from "@shared/shared.selector";

@Component({
  selector: 'app-operator-update-container',
  template: `<app-operator-update
    [emissionStartYear]="registryConfig$(emissionsYearKey) | async"
    [operatorInfo]="operatorInfo$ | async"
    [isSeniorOrJuniorAdmin]="isSeniorOrJuniorAdmin$ | async"
    (cancelEmitter)="onCancel()"
    (errorEmitter)="onErrors($event)"
    (continueEmitter)="onContinue($event)"
  ></app-operator-update> `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OperatorUpdateContainerComponent implements OnInit {
  operatorInfo$: Observable<Operator>;
  isSeniorOrJuniorAdmin$: Observable<boolean>;
  registryConfig$ = (key: string): Observable<number> =>
    this.store.select(selectRegistryConfigurationProperty, { property: key }).pipe(
      map(value => Number(value))
    );

  readonly emissionsYearKey = 'emissions-maritime-starting-year';

  constructor(
    private store: Store,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.route.snapshot.paramMap.get(
          'accountId'
        )}`,
      })
    );
    this.operatorInfo$ = this.store.select(selectOperator);
    this.isSeniorOrJuniorAdmin$ = this.store.select(isSeniorOrJuniorAdmin);
  }

  onContinue(value: Operator) {
    if (value.type === OperatorType.INSTALLATION) {
      this.store.dispatch(
        checkIfExistsInstallationPermitId({ operator: value as Installation })
      );
    } else if (value.type === OperatorType.MARITIME_OPERATOR) {
      this.store.dispatch(
        checkIfExistsMaritimeImoAndMonitorinfPlan({
          operator: value as MaritimeOperator,
        })
      );
    } else if (value.type === OperatorType.AIRCRAFT_OPERATOR) {
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
