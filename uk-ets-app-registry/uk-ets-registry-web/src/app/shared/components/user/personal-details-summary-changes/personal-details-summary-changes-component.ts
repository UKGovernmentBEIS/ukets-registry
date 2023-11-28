import { Component, EventEmitter, Input, Output } from '@angular/core';
import { IUser } from '@shared/user';
import { SummaryListItem } from '@shared/summary-list/summary-list.info';
import { CountryNamePipe, FormatUkDatePipe } from '@shared/pipes';
import { IUkOfficialCountry } from '@shared/countries/country.interface';

@Component({
  selector: 'app-personal-details-summary-changes',
  templateUrl: './personal-details-summary-changes-component.html',
})
export class PersonalDetailsSummaryChangesComponent {
  @Input()
  current: IUser;
  @Input()
  changed: IUser;
  @Input()
  isWizardOrientedFlag: boolean;
  @Input() countries: IUkOfficialCountry[];
  @Input()
  routePathForDetails: string;
  @Output() readonly navigateToEmitter = new EventEmitter<string>();

  constructor(
    private formatUkDatePipe: FormatUkDatePipe,
    private countryNamePipe: CountryNamePipe
  ) {}

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
        key: { label: 'First and middle names' },
        value: [
          {
            label: this.current.firstName,
          },
          {
            label: this.changed.firstName,
            class: this.changed.firstName
              ? 'summary-list-change-notification'
              : '',
          },
        ],
      },
      {
        key: { label: 'Last name' },
        value: [
          {
            label: this.current.lastName,
          },
          {
            label: this.changed.lastName,
            class: this.changed.lastName
              ? 'summary-list-change-notification'
              : '',
          },
        ],
      },
      {
        key: { label: 'Country of birth' },
        value: [
          {
            label: this.countryNamePipe.transform(
              this.current.countryOfBirth,
              this.countries
            ),
          },
          {
            label: this.countryNamePipe.transform(
              this.changed.countryOfBirth,
              this.countries
            ),
            class: this.changed.countryOfBirth
              ? 'summary-list-change-notification'
              : '',
          },
        ],
      },
      {
        key: { label: 'Date of birth' },
        value: [
          {
            label: this.formatUkDatePipe.transform(this.current.birthDate),
          },
          {
            label: this.formatUkDatePipe.transform(this.changed.birthDate),
            class: this.changed.birthDate
              ? 'summary-list-change-notification'
              : '',
          },
        ],
      },
      {
        key: { label: 'Also known as' },
        value: [
          {
            label: this.current.alsoKnownAs,
          },
          {
            label: this.changed.alsoKnownAs,
            class:
              this.changed.alsoKnownAs || this.changed.alsoKnownAs === ''
                ? 'summary-list-change-notification'
                : '',
          },
        ],
      },
    ];

    if (this.isWizardOrientedFlag) {
      summaryListItems.unshift({
        key: { label: 'Personal details', class: 'govuk-heading-m' },
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
