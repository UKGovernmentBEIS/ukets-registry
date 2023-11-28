import { Component, EventEmitter, Input, Output } from '@angular/core';
import { SummaryListItem } from '@shared/summary-list/summary-list.info';
import {
  CountryNamePipe,
  DateOfBirthPipe,
  FormatUkDatePipe,
} from '@shared/pipes';
import { IUkOfficialCountry } from '@shared/countries/country.interface';
import { KeycloakUser, userStatusMap } from '@shared/user';
import { EnrolmentKey } from '@user-management/user-details/model';
import { empty } from '@shared/shared.util';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-user-deactivation-summary',
  templateUrl: './user-deactivation-summary.component.html',
})
export class UserDeactivationSummaryComponent {
  @Input()
  isWizardOrientedFlag: boolean;
  @Input()
  countries: IUkOfficialCountry[];
  @Input()
  routePathForDeactivationComment: string;
  @Input()
  userDetails: KeycloakUser;
  @Input()
  enrolmentKeyDetails: EnrolmentKey;
  @Input()
  comment: string;
  @Output() readonly navigateToEmitter = new EventEmitter<string>();

  constructor(
    private formatUkDatePipe: FormatUkDatePipe,
    private dateOfBirthPipe: DateOfBirthPipe,
    private datePipe: DatePipe,
    private countryNamePipe: CountryNamePipe
  ) {}

  readonly userStatusMap = userStatusMap;

  navigateTo(value: string) {
    this.navigateToEmitter.emit(value);
  }

  get userDetailsSummaryList(): SummaryListItem[] {
    return [
      {
        key: { label: 'User details', class: 'govuk-heading-m' },
        value: [
          {
            label: '',
          },
        ],
      },
    ];
  }

  get userAttributes() {
    if (this.userDetails) {
      if (this.userDetails.attributes) {
        return this.userDetails.attributes;
      }
    }
    return null;
  }

  get registrationDetailsSummaryList(): SummaryListItem[] {
    return [
      {
        key: { label: 'Registration details', class: 'govuk-heading-m' },
        value: [
          {
            label: '',
          },
        ],
      },
      {
        key: { label: 'User ID' },
        value: [
          {
            label: this.userAttributes.urid[0],
            url: '/user-details/' + this.userAttributes.urid[0],
          },
        ],
      },
      {
        key: { label: 'Status' },
        value: [
          {
            label: userStatusMap[this.userAttributes.state[0]].label,
            class: `govuk-tag govuk-tag--${
              userStatusMap[this.userAttributes.state[0]].color
            }`,
          },
        ],
      },
      {
        key: { label: 'Email address' },
        value: [
          {
            label: this.userDetails.email,
          },
        ],
      },
    ];
  }

  get personalDetailsSummaryList(): SummaryListItem[] {
    return [
      {
        key: { label: 'Personal details', class: 'govuk-heading-m' },
        value: [
          {
            label: '',
          },
        ],
      },
      {
        key: { label: 'First and middle names' },
        value: [
          {
            label: this.userDetails.firstName,
          },
        ],
      },
      {
        key: { label: 'Last name' },
        value: [
          {
            label: this.userDetails.lastName,
          },
        ],
      },
      {
        key: { label: 'Known as' },
        value: [
          {
            label: empty(this.userAttributes.alsoKnownAs)
              ? ''
              : this.userAttributes.alsoKnownAs[0],
          },
        ],
      },
      {
        key: { label: 'Country of birth' },
        value: [
          {
            label: this.countryNamePipe.transform(
              this.userAttributes.countryOfBirth[0],
              this.countries
            ),
          },
        ],
      },
      {
        key: { label: 'Date of birth' },
        value: [
          {
            label:
              empty(this.userAttributes.birthDate) ||
              empty(this.userAttributes.birthDate[0])
                ? ''
                : this.formatUkDatePipe.transform(
                    this.dateOfBirthPipe.transform(
                      this.userAttributes.birthDate[0]
                    )
                  ),
          },
        ],
      },
    ];
  }

  get deactivationJustification(): SummaryListItem[] {
    return [
      {
        key: { label: 'Reason for deactivating this user' },
        value: [
          {
            label: this.comment,
          },
        ],
        action: {
          label: 'Change',
          visible: this.isWizardOrientedFlag,
          visuallyHidden: '',
          clickEvent: this.routePathForDeactivationComment,
        },
      },
    ];
  }
}
