import { Component, Input } from '@angular/core';
import {
  InstallationActivityType,
  MaritimeOperator,
} from '@shared/model/account';
import { OperatorWizardRoutes } from '@account-opening/operator/operator-wizard-properties';
import { regulatorMap } from '@registry-web/account-management/account-list/account-list.model';
import { SummaryListItem } from '@shared/summary-list/summary-list.info';
import { FormatUkDatePipe } from '@shared/pipes';

@Component({
  selector: 'app-overview-maritime-operator',
  template: ` <dl class="govuk-summary-list govuk-!-margin-bottom-1">
      <div class="govuk-summary-list__row govuk-summary-list--no-border">
        <dt class="govuk-summary-list__key govuk-body-l">
          Maritime Operator Details
        </dt>
      </div>
    </dl>
    <app-summary-list
      [class]="'govuk-summary-list--no-border govuk-!-margin-bottom-9'"
      [summaryListItems]="getMaritimeOperatorSummaryListItems()"
    >
      <ng-content></ng-content>
    </app-summary-list>`,
})
export class OverviewMaritimeOperatorComponent {
  @Input()
  maritimeOperator: MaritimeOperator;

  @Input()
  operatorCompleted: boolean;

  @Input()
  regulatorChanged: boolean;

  @Input()
  isSeniorOrJuniorAdmin: boolean;

  operatorWizardRoutes = OperatorWizardRoutes;
  activityTypes = InstallationActivityType;
  regulatorMap = regulatorMap;

  constructor(private formatUkDatePipe: FormatUkDatePipe) {}

  getMaritimeOperatorSummaryListItems(): SummaryListItem[] {
    const summary = [
      {
        key: { label: 'Emitter ID' },
        value: { label: this.maritimeOperator.emitterId },
      },
      {
        key: { label: 'Monitoring Plan ID' },
        value: { label: this.maritimeOperator.monitoringPlan.id },
        action: {
          label: 'Change',
          visuallyHidden: 'installation and permit details',
          url: this.operatorWizardRoutes.MARITIME_OPERATOR,
          visible: !this.operatorCompleted.valueOf(),
        },
      },
      {
        key: { label: 'Regulator' },
        value: {
          label: regulatorMap[this.maritimeOperator.regulator],
          class: this.regulatorChanged ? 'disabled' : '',
        },
        projection: 'changeRegulatorForm',
      },
      {
        key: { label: 'Company IMO number' },
        value: { label: this.maritimeOperator.imo },
      },
      {
        key: { label: 'First year of verified emission submission' },
        value: { label: this.maritimeOperator.firstYear },
      },
      {
        key: { label: 'Last year of verified emission submission' },
        value: { label: this.maritimeOperator.lastYear },
      },
    ];

    return !this.isSeniorOrJuniorAdmin
      ? summary.filter((next) => next.key.label != 'Emitter ID')
      : summary;
  }
}
