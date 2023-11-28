import { Component, EventEmitter, Input, Output } from '@angular/core';
import {
  Installation,
  InstallationActivityType,
  Operator,
  OperatorType,
  operatorTypeMap,
  Regulator,
} from '@shared/model/account/operator';
import { OperatorWizardRoutes } from '@account-opening/operator/operator-wizard-properties';
import { MainWizardRoutes } from '@account-opening/main-wizard.routes';
import { regulatorMap } from '@account-management/account-list/account-list.model';

@Component({
  selector: 'app-operator-overview',
  templateUrl: './overview.component.html',
})
export class OverviewComponent {
  @Input()
  operator: Operator;

  @Input()
  installationToBeTransferred: Installation;

  @Output()
  readonly editEmitter = new EventEmitter<OperatorType>();

  @Output()
  readonly applyEmitter = new EventEmitter<void>();

  @Output()
  readonly deleteEmitter = new EventEmitter<void>();

  @Input()
  operatorCompleted: boolean;

  type = OperatorType;
  operatorWizardRoutes = OperatorWizardRoutes;
  activityTypes = InstallationActivityType;
  operatorTypeMap = operatorTypeMap;
  readonly mainWizardRoute = MainWizardRoutes.TASK_LIST;
  readonly installationRoute = OperatorWizardRoutes.INSTALLATION;
  readonly aircraftOperatorRoute = OperatorWizardRoutes.AIRCRAFT_OPERATOR;

  getRegulatorText(regulator: Regulator) {
    return regulatorMap[regulator];
  }
}
