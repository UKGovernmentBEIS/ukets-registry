import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  AccountHolder,
  AccountHolderContact,
  AccountHolderType,
} from '@shared/model/account';
import {
  AccountHolderContactChanged,
  AccountHolderInfoChanged,
} from '@account-management/account/account-holder-details-wizard/model';
import { ContactType } from '@shared/model/account-holder-contact-type';
import {
  SummaryListInfo,
  SummaryListItem,
} from '@shared/summary-list/summary-list.info';
import {
  IndividualFullNamePipe,
  IndividualPipe,
  OrganisationPipe,
} from '@shared/pipes';
import { SummaryListAddressUtil } from '@shared/summary-list/summary-list-address.util';
import { IUkOfficialCountry } from '@registry-web/shared/countries/country.interface';
import { getCountryNameFromCountryCode } from '@registry-web/shared/shared.util';

@Component({
  selector: 'app-account-holder-summary-changes',
  templateUrl: './account-holder-summary-changes.component.html',
  styleUrls: ['./account-holder-summary-changes.component.scss'],
})
export class AccountHolderSummaryChangesComponent implements OnInit {
  @Input()
  currentValues: {
    accountHolder?: AccountHolder;
    accountHolderContact?: AccountHolderContact;
  };
  @Input()
  changedValues: {
    accountHolderDiff?: AccountHolderInfoChanged;
    accountHolderContactDiff?: AccountHolderContactChanged;
  };
  @Input()
  isWizardOrientedFlag: boolean;
  @Input()
  isContactBasedUpdate: boolean;
  @Input()
  showRemoveAPCMessage = false;
  @Input()
  routePathForSelectType: string;
  @Input()
  routePathForDetails: string;
  @Input()
  routePathForAddress: string;
  @Input()
  contactType: ContactType;
  @Input()
  showOnlyCurrentValues: boolean;
  @Input()
  showBorderLine = true;
  @Input()
  countries: IUkOfficialCountry[];

  @Output() readonly navigateToEmitter = new EventEmitter<string>();
  @Output() readonly updateRequestChecked = new EventEmitter();

  isIndividual: boolean;

  changedFullName: string;

  constructor(
    private individualPipe: IndividualPipe,
    private individualFullNamePipe: IndividualFullNamePipe,
    private organisationPipe: OrganisationPipe
  ) {}

  ngOnInit(): void {
    if (!this.currentValues.accountHolder) {
      return;
    }
    this.isIndividual =
      this.currentValues.accountHolder.type === AccountHolderType.INDIVIDUAL;
    const firstName = 'firstName';
    const lastName = 'lastName';
    const currentDetails = this.currentValues.accountHolder.details;
    const changedFirstName =
      this.changedValues?.accountHolderDiff?.details[firstName];
    const changedLastName =
      this.changedValues?.accountHolderDiff?.details[lastName];
    if (changedFirstName && changedLastName) {
      this.changedFullName = `${changedFirstName} ${changedLastName}`;
    } else if (changedFirstName && !changedLastName) {
      this.changedFullName = `${changedFirstName} ${currentDetails[lastName]}`;
    } else if (!changedFirstName && changedLastName) {
      this.changedFullName = `${currentDetails[firstName]} ${changedLastName} `;
    }
  }

  onContinue() {
    this.updateRequestChecked.emit();
  }

  navigateTo(value: string) {
    this.navigateToEmitter.emit(value);
  }

  getContactLabel() {
    return this.contactType === ContactType.PRIMARY
      ? 'Primary '
      : 'Alternative Primary ';
  }

  getRemoveMessageSummaryListItemsHeader(): SummaryListItem[] {
    return this.getCommonTitleSection(
      this.getContactLabel() + 'Contact',
      true,
      this.routePathForSelectType
    );
  }

  getRemoveMessageSummaryListItems(): SummaryListItem[] {
    return [
      {
        key: {
          label:
            'You will remove the following Alternative Primary Contact from the account holder',
          class: '',
        },
        value: [],
      },
    ];
  }

  getSummaryListItemsDetailsHeader(): SummaryListItem[] {
    return this.getCommonTitleSection(
      this.getContactLabel() + 'Contact details',
      this.isWizardOrientedFlag && this.routePathForDetails != null,
      this.routePathForDetails
    );
  }

  getSummaryListItemsDetailsItems(): SummaryListItem[] {
    const items = [] as SummaryListItem[];
    if (!this.showOnlyCurrentValues) {
      items.push(this.getCommonHeaderColumns());
    }
    items.push(
      {
        key: this.getKey('First and middle names'),
        value: this.getValue(
          this.currentValues?.accountHolderContact?.details?.firstName,
          this.changedValues?.accountHolderContactDiff['details']['firstName'],
          !this.showOnlyCurrentValues
        ),
      },
      {
        key: this.getKey('Last name'),
        value: this.getValue(
          this.currentValues?.accountHolderContact?.details?.lastName,
          this.changedValues?.accountHolderContactDiff['details']['lastName'],
          !this.showOnlyCurrentValues
        ),
      },
      {
        key: this.getKey(
          'I confirm that the ' +
            this.getContactLabel() +
            'Contact is aged 18 or over'
        ),
        value: [
          {
            label: 'Yes',
            class: 'govuk-summary-list__value',
          },
          {
            label: '',
            class: '',
          },
        ],
      }
    );
    return items;
  }

  getSummaryListItemsWorkDetailsHeader(): SummaryListItem[] {
    return this.getCommonTitleSection(
      this.getContactLabel() + 'Contact work details',
      this.isWizardOrientedFlag && this.routePathForAddress != null,
      this.routePathForAddress
    );
  }

  getSummaryListItemsWorkDetailsItems(): SummaryListItem[] {
    const addressLine1: SummaryListInfo[] = this.getValue(
      this.currentValues?.accountHolderContact?.address?.buildingAndStreet,
      this.changedValues?.accountHolderContactDiff['address'][
        'buildingAndStreet'
      ],
      true
    );

    const addressLine2: SummaryListInfo[] = this.calculateAddressLine2(
      this.currentValues?.accountHolderContact?.address?.buildingAndStreet2,
      this.changedValues?.accountHolderContactDiff['address'][
        'buildingAndStreet2'
      ]
    );

    const addressLine3: SummaryListInfo[] = this.calculateAddressLine3(
      this.currentValues?.accountHolderContact?.address?.buildingAndStreet3,
      this.changedValues?.accountHolderContactDiff['address'][
        'buildingAndStreet3'
      ]
    );

    let items = [] as SummaryListItem[];
    if (!this.showOnlyCurrentValues) {
      items.push(this.getCommonHeaderColumns());
    }

    items.push({
      key: this.getKey('Company position'),
      value: this.getValue(
        this.currentValues?.accountHolderContact?.positionInCompany,
        this.changedValues?.accountHolderContactDiff['positionInCompany'],
        !this.showOnlyCurrentValues
      ),
    });

    items = [
      ...items,
      ...this.constructAddressItems(addressLine1, addressLine2, addressLine3),
    ];

    return [
      ...items,
      {
        key: this.getKey('Town or city'),
        value: this.getValue(
          this.currentValues?.accountHolderContact?.address?.townOrCity,
          this.changedValues?.accountHolderContactDiff['address']['townOrCity'],
          !this.showOnlyCurrentValues
        ),
      },
      {
        key: this.getKey('State or Province'),
        value: this.getValue(
          this.currentValues?.accountHolderContact?.address?.stateOrProvince,
          this.changedValues?.accountHolderContactDiff['address'][
            'stateOrProvince'
          ],
          !this.showOnlyCurrentValues
        ),
      },
      {
        key: this.getKey('Country'),
        value: this.getValue(
          this.parseCountryCode(
            this.currentValues?.accountHolderContact?.address.country
          ),
          this.parseCountryCode(
            this.changedValues?.accountHolderContactDiff['address']['country']
          ),
          !this.showOnlyCurrentValues
        ),
      },
      {
        key: this.getKey('Postal Code or ZIP'),
        value: this.getValue(
          this.currentValues?.accountHolderContact?.address.postCode,
          this.changedValues?.accountHolderContactDiff['address']['postCode'],
          !this.showOnlyCurrentValues
        ),
      },
      {
        key: this.getKey('Phone number 1'),
        value: this.getMultipleValues(
          [
            this.currentValues?.accountHolderContact?.phoneNumber.countryCode1,
            this.currentValues?.accountHolderContact?.phoneNumber.phoneNumber1,
          ],
          [
            this.changedValues?.accountHolderContactDiff['phoneNumber'][
              'countryCode1'
            ],
            this.changedValues?.accountHolderContactDiff['phoneNumber'][
              'phoneNumber1'
            ],
          ],
          !this.showOnlyCurrentValues
        ),
      },
      {
        key: this.getKey('Phone number 2'),
        value: this.getMultipleValues(
          [
            this.currentValues?.accountHolderContact?.phoneNumber.countryCode2,
            this.currentValues?.accountHolderContact?.phoneNumber.phoneNumber2,
          ],
          [
            this.changedValues?.accountHolderContactDiff['phoneNumber'][
              'countryCode2'
            ],
            this.changedValues?.accountHolderContactDiff['phoneNumber'][
              'phoneNumber2'
            ],
          ],
          !this.showOnlyCurrentValues
        ),
      },
      {
        key: this.getKey('Email address'),
        value: this.getValue(
          this.currentValues?.accountHolderContact?.emailAddress?.emailAddress,
          this.changedValues?.accountHolderContactDiff['emailAddress'][
            'emailAddress'
          ],
          !this.showOnlyCurrentValues
        ),
      },
    ];
  }

  getSummaryListIndividualItemsHeader(): SummaryListItem[] {
    return this.getCommonTitleSection(
      "Individual's details",
      this.isWizardOrientedFlag,
      this.routePathForDetails
    );
  }

  getSummaryListIndividualItems(): SummaryListItem[] {
    return [
      this.getCommonHeaderColumns(),
      {
        key: this.getKey('Full name'),
        value: this.getValue(
          this.individualFullNamePipe.transform(
            this.individualPipe.transform(this.currentValues?.accountHolder)
              ?.details
          ),
          this.changedFullName,
          true
        ),
      },
      {
        key: this.getKey('Country of birth'),
        value: this.getValue(
          this.parseCountryCode(
            this.individualPipe.transform(this.currentValues?.accountHolder)
              ?.details?.countryOfBirth
          ),
          this.parseCountryCode(
            this.changedValues?.accountHolderDiff['details']['countryOfBirth']
          ),
          true
        ),
      },
      {
        key: this.getKey(
          'I confirm that the account holder is aged 18 or over'
        ),
        value: [
          {
            label: 'Yes',
            class: 'govuk-summary-list__value',
          },
          {
            label: '',
            class: '',
          },
        ],
      },
    ];
  }

  getSummaryListIndividualContactItemsHeader(): SummaryListItem[] {
    return this.getCommonTitleSection(
      "Individual's contact details",
      this.isWizardOrientedFlag,
      this.routePathForAddress
    );
  }

  getSummaryListIndividualContactItems(): SummaryListItem[] {
    const addressLine1: SummaryListInfo[] = this.getValue(
      this.individualPipe.transform(this.currentValues?.accountHolder)?.address
        ?.buildingAndStreet,
      this.changedValues?.accountHolderDiff['address']['buildingAndStreet'],
      true
    );
    const addressLine2: SummaryListInfo[] = this.calculateAddressLine2(
      this.individualPipe.transform(this.currentValues?.accountHolder)?.address
        ?.buildingAndStreet2,
      this.changedValues?.accountHolderDiff['address']['buildingAndStreet2']
    );

    const addressLine3: SummaryListInfo[] = this.calculateAddressLine3(
      this.individualPipe.transform(this.currentValues?.accountHolder)?.address
        ?.buildingAndStreet3,
      this.changedValues?.accountHolderDiff['address']['buildingAndStreet3']
    );

    let items = [] as SummaryListItem[];
    items.push(this.getCommonHeaderColumns());

    items = [
      ...items,
      ...this.constructAddressItems(addressLine1, addressLine2, addressLine3),
    ];

    return [
      ...items,
      {
        key: this.getKey('Town or city'),
        value: this.getValue(
          this.individualPipe.transform(this.currentValues?.accountHolder)
            ?.address?.townOrCity,
          this.changedValues?.accountHolderDiff['address']['townOrCity'],
          true
        ),
      },
      {
        key: this.getKey('State or Province'),
        value: this.getValue(
          this.individualPipe.transform(this.currentValues?.accountHolder)
            ?.address?.stateOrProvince,
          this.changedValues?.accountHolderDiff['address']['stateOrProvince'],
          true
        ),
      },
      {
        key: this.getKey('Country'),
        value: this.getValue(
          this.parseCountryCode(
            this.individualPipe.transform(this.currentValues?.accountHolder)
              ?.address?.country
          ),
          this.parseCountryCode(
            this.changedValues?.accountHolderDiff['address']['country']
          ),
          true
        ),
      },
      {
        key: this.getKey('Postal Code or ZIP'),
        value: this.getValue(
          this.individualPipe.transform(this.currentValues?.accountHolder)
            ?.address?.postCode,
          this.changedValues?.accountHolderDiff['address']['postCode'],
          true
        ),
      },
      {
        key: this.getKey('Phone number 1'),
        value: this.getValue(
          this.individualPipe.transform(this.currentValues?.accountHolder)
            ?.phoneNumber?.phoneNumber1,
          this.changedValues?.accountHolderDiff['phoneNumber']['phoneNumber1'],
          true
        ),
      },
      {
        key: this.getKey('Phone number 2'),
        value: this.getValue(
          this.individualPipe.transform(this.currentValues?.accountHolder)
            ?.phoneNumber?.phoneNumber2,
          this.changedValues?.accountHolderDiff['phoneNumber']['phoneNumber2'],
          true
        ),
      },
      {
        key: this.getKey('Email address'),
        value: this.getValue(
          this.individualPipe.transform(this.currentValues?.accountHolder)
            ?.emailAddress?.emailAddress,
          this.changedValues?.accountHolderDiff['emailAddress']['emailAddress'],
          true
        ),
      },
    ];
  }

  getSummaryListOrganizationItemsHeader(): SummaryListItem[] {
    return this.getCommonTitleSection(
      'Organization details',
      this.isWizardOrientedFlag,
      this.routePathForDetails
    );
  }

  getSummaryListOrganizationItems(): SummaryListItem[] {
    const organisation = this.organisationPipe.transform(
      this.currentValues?.accountHolder
    );
    const currentRegNumber = organisation?.details?.registrationNumber;
    const changedRegNumber =
      this.changedValues?.accountHolderDiff['details']['registrationNumber'];
    const currentNoRegNumberJustification =
      organisation?.details?.noRegistrationNumJustification;
    const changedNoRegNumberJustification =
      this.changedValues?.accountHolderDiff['details'][
        'noRegistrationNumJustification'
      ];

    const regNumberItem = this.getValue(
      currentRegNumber === '' ? 'No registration number' : currentRegNumber,
      changedRegNumber === '' ? 'No registration number' : changedRegNumber,
      true
    );
    const noRegNumberJustificationItem = this.getValue(
      currentNoRegNumberJustification,
      changedNoRegNumberJustification,
      true
    );

    return [
      this.getCommonHeaderColumns(),
      {
        key: this.getKey('Name'),
        value: this.getValue(
          organisation?.details?.name,
          this.changedValues?.accountHolderDiff['details']['name'],
          true
        ),
      },
      {
        key: this.getKey('Registration number', false),
        value: regNumberItem,
      },
      {
        key: this.getKey('', true),
        value: noRegNumberJustificationItem,
      },
    ];
  }

  getSummaryListOrganizationAddressItemsHeader(): SummaryListItem[] {
    return this.getCommonTitleSection(
      'Organization address',
      this.isWizardOrientedFlag,
      this.routePathForAddress
    );
  }

  getSummaryListOrganizationAddressItems(): SummaryListItem[] {
    const addressLine1: SummaryListInfo[] = this.getValue(
      this.organisationPipe.transform(this.currentValues?.accountHolder)
        ?.address?.buildingAndStreet,
      this.changedValues?.accountHolderDiff['address']['buildingAndStreet'],
      true
    );

    const addressLine2: SummaryListInfo[] = this.calculateAddressLine2(
      this.organisationPipe.transform(this.currentValues?.accountHolder)
        ?.address?.buildingAndStreet2,
      this.changedValues?.accountHolderDiff['address']['buildingAndStreet2']
    );

    const addressLine3: SummaryListInfo[] = this.calculateAddressLine3(
      this.organisationPipe.transform(this.currentValues?.accountHolder)
        ?.address?.buildingAndStreet3,
      this.changedValues?.accountHolderDiff['address']['buildingAndStreet3']
    );

    let items = [] as SummaryListItem[];
    items.push(this.getCommonHeaderColumns());

    items = [
      ...items,
      ...this.constructAddressItems(addressLine1, addressLine2, addressLine3),
    ];

    return [
      ...items,
      {
        key: this.getKey('Town or city'),
        value: this.getValue(
          this.organisationPipe.transform(this.currentValues?.accountHolder)
            ?.address?.townOrCity,
          this.changedValues?.accountHolderDiff['address']['townOrCity'],
          true
        ),
      },
      {
        key: this.getKey('State or Province'),
        value: this.getValue(
          this.organisationPipe.transform(this.currentValues?.accountHolder)
            ?.address?.stateOrProvince,
          this.changedValues?.accountHolderDiff['address']['stateOrProvince'],
          true
        ),
      },
      {
        key: this.getKey('Country'),
        value: this.getValue(
          this.parseCountryCode(
            this.organisationPipe.transform(this.currentValues?.accountHolder)
              ?.address?.country
          ),
          this.parseCountryCode(
            this.changedValues?.accountHolderDiff['address']['country']
          ),
          true
        ),
      },
      {
        key: this.getKey('Postal Code or ZIP'),
        value: this.getValue(
          this.organisationPipe.transform(this.currentValues?.accountHolder)
            ?.address?.postCode,
          this.changedValues?.accountHolderDiff['address']['postCode'],
          true
        ),
      },
    ];
  }

  private getKey(name, hasBorder = true) {
    return SummaryListAddressUtil.getKey(name, hasBorder);
  }

  private getValue(current, changed, showChanged) {
    return SummaryListAddressUtil.getValue(current, changed, showChanged);
  }

  private getMultipleValues(current, changed, showChanged) {
    let currentLabelText = '';
    current.forEach((val) => {
      if (val != null) {
        currentLabelText += val + ' ';
      }
    });
    const items = [
      {
        label: currentLabelText,
        class: 'govuk-summary-list__value',
      },
    ];
    if (showChanged) {
      let changedLabelText = '';
      changed.forEach((val, index) => {
        changedLabelText +=
          (val != null && true ? val : current[index] ? current[index] : '') +
          ' ';
      });
      items.push({
        label: changedLabelText !== currentLabelText ? changedLabelText : '',
        class:
          changedLabelText.trim() !== currentLabelText.trim()
            ? 'govuk-summary-list__value focused-text'
            : 'govuk-summary-list__value',
      });
    }
    return items;
  }

  private getCommonHeaderColumns() {
    return {
      key: {
        label: 'Field',
        class: 'govuk-summary-list__key',
      },
      value: [
        {
          label: 'Current value',
          class: 'govuk-summary-list__key',
        },
        {
          label: 'Changed value',
          class: 'govuk-summary-list__key',
        },
      ],
    };
  }

  private getCommonTitleSection(
    title: string,
    showLink: boolean,
    goTo: string
  ): SummaryListItem[] {
    return [
      {
        key: {
          label: title,
          class: 'govuk-summary-list__key govuk-body-l',
        },
        value: [],
        action: {
          label: 'Change',
          visible: showLink,
          visuallyHidden: '',
          clickEvent: goTo,
        },
      },
    ];
  }
  private calculateAddressLine2(current, changed) {
    return SummaryListAddressUtil.calculateAddressLine2(current, changed);
  }

  private calculateAddressLine3(current, changed) {
    return SummaryListAddressUtil.calculateAddressLine3(current, changed);
  }

  private constructAddressItems(
    addressLine1: SummaryListInfo[],
    addressLine2: SummaryListInfo[],
    addressLine3: SummaryListInfo[]
  ) {
    return SummaryListAddressUtil.constructAddressItems(
      addressLine1,
      addressLine2,
      addressLine3
    );
  }

  private parseCountryCode(code: string) {
    return getCountryNameFromCountryCode(this.countries, code);
  }
}
