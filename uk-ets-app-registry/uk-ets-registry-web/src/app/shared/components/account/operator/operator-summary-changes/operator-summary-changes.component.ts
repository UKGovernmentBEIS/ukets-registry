import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  InstallationActivityType,
  Operator,
  OperatorType,
} from '@shared/model/account';
import { UkDate } from '@shared/model/uk-date';
import { SummaryListItem } from '@shared/summary-list/summary-list.info';
import { FormatUkDatePipe } from '@shared/pipes';

@Component({
  selector: 'app-operator-summary-changes',
  templateUrl: './operator-summary-changes.component.html',
})
export class OperatorSummaryChangesComponent implements OnInit {
  @Input()
  current: Operator;
  @Input()
  changed: Operator;
  @Input()
  isWizardOrientedFlag: boolean;
  @Input()
  routePathForDetails: string;
  @Output() readonly navigateToEmitter = new EventEmitter<string>();

  activityTypes = InstallationActivityType;
  isInstallation: boolean;
  isAircraft: boolean;
  titleOfIdentifier: string;
  title: string;

  constructor(private formatUkDatePipe: FormatUkDatePipe) {}

  ngOnInit(): void {
    this.isInstallation = this.current.type === OperatorType.INSTALLATION;
    this.isAircraft = this.current.type === OperatorType.AIRCRAFT_OPERATOR;
    if (this.current.type === OperatorType.INSTALLATION) {
      this.titleOfIdentifier = 'Installation ID';
      this.title = 'Installation details';
    }
    if (this.current.type === OperatorType.AIRCRAFT_OPERATOR) {
      this.titleOfIdentifier = 'Aircraft Operator ID';
      this.title = 'Aircraft operator details';
    }
  }

  navigateTo(value: string) {
    this.navigateToEmitter.emit(value);
  }

  isDateNotEqual(currentDate: UkDate, changedDate: UkDate) {
    return (
      currentDate.day !== changedDate.day ||
      currentDate.month !== changedDate.month ||
      currentDate.year !== changedDate.year
    );
  }

  isLastYearNotEqual(current, changed) {
    return !Object.is(
      Number(current),
      Number(changed === undefined ? 0 : changed)
    );
  }

  notFromWizard() {
    return (
      this.isLastYearNotEqual(
        this.current['lastYear'],
        this.changed['lastYear']
      ) &&
      !this.isWizardOrientedFlag &&
      this.changed['lastYearChanged'] == true
    );
  }

  loadFromWizard() {
    return (
      this.isWizardOrientedFlag &&
      !isNaN(Number(this.changed['lastYear'])) &&
      this.isLastYearNotEqual(
        this.current['lastYear'],
        this.changed['lastYear']
      )
    );
  }

  getSummaryListItems(): SummaryListItem[] {
    const summaryListItems: SummaryListItem[] = [
      {
        key: { label: 'Field' },
        value: [
          {
            label: 'Current value',
            class: 'summary-list-change-header-font-weight',
          },
          {
            label: 'Changed value',
            class: 'summary-list-change-header-font-weight',
          },
        ],
        action: {
          label: 'Change',
          visible: this.isWizardOrientedFlag,
          visuallyHidden: '',
          url: '',
          clickEvent: this.routePathForDetails,
        },
      },
    ];
    if (this.current['name']) {
      summaryListItems.push({
        key: { label: 'Installation name' },
        value: [
          {
            label: this.current['name'],
          },
          {
            label: this.changed['name'],
            class: this.changed['name']
              ? 'summary-list-change-notification'
              : '',
          },
        ],
        action: {
          label: '',
          visuallyHidden: '',
          visible: true,
        },
      });
    }
    if (this.current['activityType']) {
      summaryListItems.push({
        key: { label: 'Installation activity type' },
        value: [
          {
            label: this.activityTypes[this.current['activityType']],
          },
          {
            label: this.changed['activityType']
              ? this.activityTypes[this.changed['activityType']]
              : '',
            class: this.changed['activityType']
              ? 'summary-list-change-notification'
              : '',
          },
        ],
        action: {
          label: '',
          visuallyHidden: '',
          visible: true,
        },
      });
    }
    if (this.current['permit']) {
      summaryListItems.push({
        key: { label: 'Permit ID' },
        value: [
          {
            label: this.current['permit']['id'],
          },
          {
            label:
              this.changed['permit'] &&
              this.current['permit']['id'] != this.changed['permit']['id']
                ? this.changed['permit']['id']
                : '',
            class:
              this.changed['permit'] &&
              this.changed['permit']['id'] &&
              this.current['permit']['id'] != this.changed['permit']['id']
                ? 'summary-list-change-notification'
                : '',
          },
        ],
        action: {
          label: '',
          visuallyHidden: '',
          visible: true,
        },
      });
    }
    if (this.current['monitoringPlan']) {
      summaryListItems.push({
        key: { label: 'Monitoring Plan ID' },
        value: [
          {
            label: this.current['monitoringPlan']['id'],
          },
          {
            label:
              this.changed['monitoringPlan'] &&
              this.changed['monitoringPlan']['id']
                ? this.changed['monitoringPlan']['id']
                : '',
            class:
              this.changed['monitoringPlan'] &&
              this.changed['monitoringPlan']['id']
                ? 'summary-list-change-notification'
                : '',
          },
        ],
        action: {
          label: '',
          visuallyHidden: '',
          visible: true,
        },
      });
    }
    if (this.current['regulator']) {
      summaryListItems.push({
        key: { label: 'Regulator' },
        value: [
          {
            label: this.current['regulator'],
          },
          {
            label: this.isWizardOrientedFlag
              ? this.changed['regulator']
              : this.changed['changedRegulator'],
            class: (
              this.isWizardOrientedFlag
                ? this.changed['regulator']
                : this.changed['changedRegulator']
            )
              ? 'summary-list-change-notification'
              : '',
          },
        ],
        action: {
          label: '',
          visuallyHidden: '',
          visible: true,
        },
      });
    }
    if (this.current['firstYear']) {
      summaryListItems.push({
        key: { label: 'First year of verified emission submission' },
        value: [
          {
            label: this.current['firstYear'],
          },
          {
            label: this.changed['firstYear'],
            class: this.changed['firstYear']
              ? 'summary-list-change-notification'
              : '',
          },
        ],
        action: {
          label: '',
          visuallyHidden: '',
          visible: true,
        },
      });
    }
    summaryListItems.push({
      key: { label: 'Last year of verified emission submission' },
      value: [
        {
          label: this.current['lastYear'],
        },
        {
          label: this.isLastYearNotEqual(
            this.current['lastYear'],
            this.changed['lastYear']
          )
            ? this.changed['lastYear']
            : '',
          class:
            this.notFromWizard() || this.loadFromWizard()
              ? 'summary-list-change-notification'
              : '',
        },
      ],
      action: {
        label: '',
        visuallyHidden: '',
        visible: true,
      },
    });
    return summaryListItems;
  }
}
