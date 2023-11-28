import { Component, EventEmitter, Input, Output } from '@angular/core';
import { IUser } from '@shared/user';
import {
  SummaryListInfo,
  SummaryListItem,
} from '@shared/summary-list/summary-list.info';
import { IUkOfficialCountry } from '@shared/countries/country.interface';
import { CountryNamePipe } from '@shared/pipes';
import { SummaryListAddressUtil } from '@shared/summary-list/summary-list-address.util';

@Component({
  selector: 'app-work-details-summary-changes',
  templateUrl: './work-details-summary-changes-component.html',
})
export class WorkDetailsSummaryChangesComponent {
  @Input()
  current: IUser;
  @Input()
  changed: IUser;
  @Input()
  isWizardOrientedFlag: boolean;
  @Input()
  routePathForDetails: string;
  @Input() countries: IUkOfficialCountry[];
  @Output() readonly navigateToEmitter = new EventEmitter<string>();

  constructor(private countryNamePipe: CountryNamePipe) {}

  navigateTo(value: string) {
    this.navigateToEmitter.emit(value);
  }

  getSummaryListItems(): SummaryListItem[] {
    const addressLine1: SummaryListInfo[] = SummaryListAddressUtil.getValue(
      this.current.workBuildingAndStreet,
      this.changed.workBuildingAndStreet,
      true
    );

    const addressLine2: SummaryListInfo[] =
      SummaryListAddressUtil.calculateAddressLine2(
        this.current.workBuildingAndStreetOptional,
        this.changed.workBuildingAndStreetOptional
      );

    const addressLine3: SummaryListInfo[] =
      SummaryListAddressUtil.calculateAddressLine3(
        this.current.workBuildingAndStreetOptional2,
        this.changed.workBuildingAndStreetOptional2
      );

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
        key: { label: 'Phone number' },
        value: [
          {
            label:
              this.current.workCountryCode + ' ' + this.current.workPhoneNumber,
          },
          {
            label:
              (this.changed.workCountryCode
                ? this.changed.workCountryCode
                : this.changed.workPhoneNumber
                ? this.current.workCountryCode
                : '') +
              ' ' +
              (this.changed.workPhoneNumber
                ? this.changed.workPhoneNumber
                : ''),
            class:
              this.changed.workCountryCode || this.changed.workPhoneNumber
                ? 'summary-list-change-notification'
                : '',
          },
        ],
      },
      ...SummaryListAddressUtil.constructAddressItems(
        addressLine1,
        addressLine2,
        addressLine3
      ),
      {
        key: { label: 'Town or city' },
        value: [
          {
            label: this.current.workTownOrCity,
          },
          {
            label: this.changed.workTownOrCity,
            class: this.changed.workTownOrCity
              ? 'summary-list-change-notification'
              : '',
          },
        ],
      },
      {
        key: { label: 'State or Province' },
        value: [
          {
            label: this.current.workStateOrProvince,
          },
          {
            label: this.changed.workStateOrProvince,
            class:
              this.changed.workStateOrProvince ||
              this.changed.workStateOrProvince === ''
                ? 'summary-list-change-notification'
                : '',
          },
        ],
      },
      {
        key: { label: 'Country' },
        value: [
          {
            label: this.countryNamePipe.transform(
              this.current.workCountry,
              this.countries
            ),
          },
          {
            label: this.countryNamePipe.transform(
              this.changed.workCountry,
              this.countries
            ),
            class: this.changed.workCountry
              ? 'summary-list-change-notification'
              : '',
          },
        ],
      },
      {
        key: { label: 'Postal Code or ZIP' },
        value: [
          {
            label: this.current.workPostCode,
          },
          {
            label: this.changed.workPostCode,
            class:
              this.changed.workPostCode || this.changed.workPostCode === ''
                ? 'summary-list-change-notification'
                : '',
          },
        ],
      },
    ];

    if (this.isWizardOrientedFlag) {
      summaryListItems.unshift({
        key: { label: 'Work contact details', class: 'govuk-heading-m' },
        value: [
          {
            label: '',
          },
        ],
        action: {
          label: 'Change',
          visible: this.isWizardOrientedFlag,
          visuallyHidden: '',
          url: '',
          clickEvent: this.routePathForDetails,
        },
      });
    }

    return summaryListItems;
  }
}
