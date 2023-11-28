import { Component, Input } from '@angular/core';
import {
  AircraftOperator,
  InstallationActivityType,
} from '@shared/model/account';
import { OperatorWizardRoutes } from '@account-opening/operator/operator-wizard-properties';
import { regulatorMap } from '@registry-web/account-management/account-list/account-list.model';
import { SummaryListItem } from '@shared/summary-list/summary-list.info';
import { FormatUkDatePipe } from '@shared/pipes';

@Component({
  selector: 'app-overview-aircraft-operator',
  template: ` <app-summary-list
    [class]="'govuk-summary-list--no-border govuk-!-margin-bottom-9'"
    [summaryListItems]="getAircraftOperatorSummaryListItems()"
  >
    <ng-content></ng-content>
  </app-summary-list>`,
})
export class OverviewAircraftOperatorComponent {
  @Input()
  aircraftOperator: AircraftOperator;

  @Input()
  operatorCompleted: boolean;

  @Input()
  regulatorChanged: boolean;

  operatorWizardRoutes = OperatorWizardRoutes;
  activityTypes = InstallationActivityType;
  regulatorMap = regulatorMap;

  constructor(private formatUkDatePipe: FormatUkDatePipe) {}

  getAircraftOperatorSummaryListItems(): SummaryListItem[] {
    return [
      {
        key: { label: 'Monitoring Plan ID' },
        value: { label: this.aircraftOperator.monitoringPlan.id },
        action: {
          label: 'Change',
          visuallyHidden: 'installation and permit details',
          url: this.operatorWizardRoutes.AIRCRAFT_OPERATOR,
          visible: !this.operatorCompleted.valueOf(),
        },
      },
      {
        key: { label: 'Regulator' },
        value: {
          label: regulatorMap[this.aircraftOperator.regulator],
          class: this.regulatorChanged ? 'disabled' : '',
        },
        projection: 'changeRegulatorForm',
      },
      {
        key: { label: 'First year of verified emission submission' },
        value: { label: this.aircraftOperator.firstYear },
      },
      {
        key: { label: 'Last year of verified emission submission' },
        value: { label: this.aircraftOperator.lastYear },
      },
    ];
  }
}
