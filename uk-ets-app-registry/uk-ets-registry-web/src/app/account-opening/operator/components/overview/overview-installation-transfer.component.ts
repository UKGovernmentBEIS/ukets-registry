import { Component, Input } from '@angular/core';
import {
  Installation,
  InstallationActivityType,
  InstallationTransfer,
} from '@shared/model/account';
import { OperatorWizardRoutes } from '@account-opening/operator/operator-wizard-properties';
import { regulatorMap } from '@registry-web/account-management/account-list/account-list.model';
import { SummaryListItem } from '@shared/summary-list/summary-list.info';
import { FormatUkDatePipe } from '@shared/pipes';

@Component({
  selector: 'app-overview-installation-transfer',
  template: `
    <app-summary-list
      [class]="'govuk-summary-list--no-border govuk-!-margin-bottom-9'"
      [summaryListItems]="getInstallationToBeTransferredSummaryListInfos()"
      [title]="'Installation to be transferred'"
    >
      <ng-content></ng-content>
    </app-summary-list>
    <app-summary-list
      [class]="'govuk-summary-list--no-border govuk-!-margin-bottom-9'"
      [summaryListItems]="getNewInstallationDetails()"
      [title]="'Installation details'"
    ></app-summary-list>
  `,
})
export class OverviewInstallationTransferComponent {
  @Input()
  installationToBeTransferred: Installation;

  @Input()
  installationTransfer: InstallationTransfer;

  @Input()
  operatorCompleted: boolean;

  @Input()
  isSeniorOrJuniorAdmin: boolean;

  operatorWizardRoutes = OperatorWizardRoutes;
  activityTypes = InstallationActivityType;
  regulatorMap = regulatorMap;

  constructor(private formatUkDatePipe: FormatUkDatePipe) {}

  getInstallationToBeTransferredSummaryListInfos(): SummaryListItem[] {
    const summary = [
      {
        key: { label: 'Installation ID' },
        value: {
          label: this.installationToBeTransferred.identifier.toString(),
        },
        action: {
          label: 'Change',
          visible: !this.operatorCompleted.valueOf(),
          visuallyHidden: 'installation and permit details',
          url: this.operatorWizardRoutes.INSTALLATION,
        },
      },
      {
        key: { label: 'Installation name' },
        value: { label: this.installationToBeTransferred.name },
      },
      {
        key: { label: 'Installation activity type' },
        value: {
          label:
            this.activityTypes[this.installationToBeTransferred.activityType],
        },
      },
      {
        key: { label: 'Emitter ID' },
        value: { label: this.installationToBeTransferred.emitterId },
      },
      {
        key: { label: 'Permit ID' },
        value: { label: this.installationToBeTransferred.permit.id },
      },
      {
        key: { label: 'Regulator' },
        value: {
          label: regulatorMap[this.installationToBeTransferred.regulator],
        },
        projection: 'changeRegulatorForm',
      },
      {
        key: { label: 'First year of verified emission submission' },
        value: { label: this.installationToBeTransferred.firstYear },
      },
      {
        key: { label: 'Last year of verified emission submission' },
        value: { label: this.installationToBeTransferred.lastYear },
      },
    ];

    return !this.isSeniorOrJuniorAdmin ?
      summary.filter( next => next.key.label != 'Emitter ID') :
        summary;
  }

  getNewInstallationDetails(): SummaryListItem[] {
    const summary = [
      {
        key: { label: 'New installation name' },
        value: {
          label: this.installationTransfer.name,
          class: 'summary-list-change-notification',
        },
        action: {
          label: 'Change',
          visible: !this.operatorCompleted.valueOf(),
          visuallyHidden: 'installation and permit details',
          url: this.operatorWizardRoutes.INSTALLATION,
        },
      },
      {
        key: { label: 'New emitter ID' },
        value: {
          label: this.installationTransfer.emitterId,
          class: 'summary-list-change-notification',
        },
        action: {
          label: 'Change',
          visible: !this.operatorCompleted.valueOf(),
          visuallyHidden: 'installation and permit details',
          url: this.operatorWizardRoutes.INSTALLATION,
        },
      },
      {
        key: { label: 'New permit ID' },
        value: {
          label: this.installationTransfer.permit.id,
          class: 'summary-list-change-notification',
        },
        action: {
          label: 'Change',
          visible: !this.operatorCompleted.valueOf(),
          visuallyHidden: 'installation and permit details',
          url: this.operatorWizardRoutes.INSTALLATION,
        },
      }
    ];

    return !this.isSeniorOrJuniorAdmin ?
      summary.filter( next => next.key.label != 'New emitter ID') :
        summary;
  }
}
