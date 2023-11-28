import { Component, EventEmitter, Input, Output } from '@angular/core';
import { IUser } from '@shared/user';
import { SummaryListItem } from '@shared/summary-list/summary-list.info';

@Component({
  selector: 'app-memorable-phrase-summary-changes',
  templateUrl: './memorable-phrase-summary-changes.component.html',
})
export class MemorablePhraseSummaryChangesComponent {
  @Input()
  current: IUser;
  @Input()
  changed: IUser;
  @Input()
  isWizardOrientedFlag: boolean;
  @Input()
  routePathForDetails: string;
  @Output() readonly navigateToEmitter = new EventEmitter<string>();

  navigateTo(value: string) {
    this.navigateToEmitter.emit(value);
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
      },
      {
        key: { label: 'Memorable phrase' },
        value: [
          {
            label: this.current.memorablePhrase,
          },
          {
            label: this.changed.memorablePhrase,
            class: this.changed.memorablePhrase
              ? 'summary-list-change-notification'
              : '',
          },
        ],
      },
    ];

    if (this.isWizardOrientedFlag) {
      summaryListItems.unshift({
        key: { label: 'Memorable phrase', class: 'govuk-heading-m' },
        value: [
          {
            label: '',
          },
        ],
        action: {
          label: 'Change',
          visible: this.isWizardOrientedFlag,
          visuallyHidden: '',
          clickEvent: this.routePathForDetails,
        },
      });
    }
    return summaryListItems;
  }
}
