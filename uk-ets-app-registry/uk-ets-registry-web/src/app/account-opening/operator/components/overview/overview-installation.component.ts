import { Component, Input } from '@angular/core';
import {
  getEntriesValues,
  Installation,
  InstallationActivityType,
} from '@shared/model/account';
import { OperatorWizardRoutes } from '@account-opening/operator/operator-wizard-properties';
import { regulatorMap } from '@registry-web/account-management/account-list/account-list.model';
import { SummaryListItem } from '@shared/summary-list/summary-list.info';
import { FormatUkDatePipe } from '@shared/pipes';

@Component({
  selector: 'app-overview-installation',
  template: ` <app-summary-list
      [class]="'govuk-summary-list--no-border govuk-!-margin-bottom-9'"
      [summaryListItems]="getInstallationSummaryListItems()"
      [title]="'Installation details'"
    >
      <ng-content></ng-content>
    </app-summary-list>
    <app-summary-list
      [class]="'govuk-summary-list--no-border govuk-!-margin-bottom-9'"
      [summaryListItems]="getRegulatedActivity()"
      [title]="'Regulated activity'"
    >
      <ng-content></ng-content>
    </app-summary-list>`,
})
export class OverviewInstallationComponent {
  @Input()
  installation: Installation;

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

  getInstallationSummaryListItems(): SummaryListItem[] {
    const summary = [
      {
        key: { label: 'Installation name' },
        value: { label: this.installation.name },
        action: {
          label: 'Change',
          visible: !this.operatorCompleted.valueOf(),
          visuallyHidden: 'installation and permit details',
          url: this.operatorWizardRoutes.INSTALLATION,
        },
      },
      {
        key: { label: 'Emitter ID' },
        value: { label: this.installation.emitterId },
      },
      {
        key: { label: 'Permit ID' },
        value: { label: this.installation.permit.id },
      },
      {
        key: { label: 'Regulator' },
        value: {
          label: regulatorMap[this.installation.regulator],
          class: this.regulatorChanged ? 'disabled' : '',
        },
        projection: 'changeRegulatorForm',
      },
      {
        key: { label: 'First year of verified emission submission' },
        value: { label: this.installation.firstYear },
      },
      {
        key: { label: 'Last year of verified emission submission' },
        value: { label: this.installation.lastYear },
      },
    ];

    return !this.isSeniorOrJuniorAdmin
      ? summary.filter((next) => next.key.label != 'Emitter ID')
      : summary;
  }

  getRegulatedActivity(): SummaryListItem[] {
    const summary = [
      {
        key: {
          label: 'Regulated activity',
          class: 'govuk-summary-list__key govuk-body-m',
        },
        value: [
          {
            label: getEntriesValues(
              this.installation.activityTypes?.length > 0
                ? this.installation.activityTypes
                : [this.installation['activityType']]
            ),
            class: 'govuk-summary-list__value',
          },
        ],
        action: {
          label: 'Change',
          visible: !this.operatorCompleted.valueOf(),
          visuallyHidden: 'regulated activity',
          url: this.operatorWizardRoutes.SELECT_REGULATED_ACTIVITY,
        },
      },
    ];

    return !this.isSeniorOrJuniorAdmin
      ? summary.filter((next) => next.key.label != 'Emitter ID')
      : summary;
  }
}
