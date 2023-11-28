import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  AccountHolder,
  AccountHolderContact,
  AccountHolderType,
} from '@shared/model/account';
import {
  AccountHolderContactChanged,
  AccountHolderDetailsType,
  AccountHolderDetailsWizardPathsMap,
  AccountHolderDetailsWizardPathsModel,
  AccountHolderInfoChanged,
  AccountHolderUpdate,
} from '@account-management/account/account-holder-details-wizard/model';
import { ErrorDetail } from '@shared/error-summary';
import { ContactType } from '@shared/model/account-holder-contact-type';
import { SummaryListItem } from '@shared/summary-list/summary-list.info';
import { IUkOfficialCountry } from '@registry-web/shared/countries/country.interface';

@Component({
  selector: 'app-check-update-request',
  templateUrl: './check-update-request.component.html',
})
export class CheckUpdateRequestComponent implements OnInit {
  @Input() caption: string;
  @Input() header: string;
  @Input() accountHolder: AccountHolder;
  @Input() accountHolderInfoChanged: AccountHolderInfoChanged;
  @Input() updateType: AccountHolderDetailsType;
  @Input() accountHolderContact: AccountHolderContact;
  @Input() accountHolderContactChanged: AccountHolderContactChanged;
  @Input() accountIdentifier: string;
  @Input() contactType: ContactType;
  @Input() countries: IUkOfficialCountry[];

  @Output()
  readonly clickChange = new EventEmitter<AccountHolderDetailsWizardPathsModel>();
  @Output() readonly submitRequest = new EventEmitter<AccountHolderUpdate>();
  @Output() readonly errorDetails = new EventEmitter<ErrorDetail>();

  isContactBasedUpdate: boolean;

  organisation = AccountHolderType.ORGANISATION;
  individual = AccountHolderType.INDIVIDUAL;
  routePathForDetails: string;
  routePathForSelectType: string;
  routePathForAddress: string;
  deleteAlternativePrimaryContactType =
    AccountHolderDetailsType.ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_DELETE;
  addAlternativePrimaryContactType =
    AccountHolderDetailsType.ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_ADD;
  updateAlternativePrimaryContactType =
    AccountHolderDetailsType.ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_UPDATE;
  updatePrimaryContactType =
    AccountHolderDetailsType.ACCOUNT_HOLDER_PRIMARY_CONTACT_DETAILS;

  currentValues: {
    accountHolder?: AccountHolder;
    accountHolderContact?: AccountHolderContact;
  };
  changedValues: {
    accountHolderDiff?: AccountHolder;
    accountHolderContactDiff?: AccountHolderContact;
  };

  ngOnInit() {
    this.isContactBasedUpdate =
      this.updateType === this.updatePrimaryContactType ||
      this.updateType === this.updateAlternativePrimaryContactType ||
      this.updateType === this.addAlternativePrimaryContactType ||
      this.updateType === this.deleteAlternativePrimaryContactType;
    if (!this.isContactBasedUpdate) {
      this.updateAccountHolder();
    } else if (this.updateType === this.deleteAlternativePrimaryContactType) {
      this.deleteAlternativePrimaryContact();
    } else if (this.updateType === this.addAlternativePrimaryContactType) {
      this.addAlternativePrimaryContact();
    } else {
      this.updateAccountHolderContact();
    }
  }

  /**
   * Update Account Holder Info
   * @private
   */
  private updateAccountHolder() {
    this.routePathForDetails =
      AccountHolderDetailsWizardPathsModel.UPDATE_ACCOUNT_HOLDER;
    this.routePathForAddress =
      AccountHolderDetailsWizardPathsModel.UPDATE_AH_ADDRESS;
    this.currentValues = {
      accountHolder: this.accountHolder,
    };
    this.changedValues = {
      accountHolderDiff: this.getObjectDiff(
        this.accountHolder,
        this.accountHolderInfoChanged
      ),
    };
  }

  /**
   * Update an existing Primary Contact or Alternative Primary Contact
   * @private
   */
  private updateAccountHolderContact() {
    this.routePathForDetails =
      this.updateType ===
      AccountHolderDetailsType.ACCOUNT_HOLDER_PRIMARY_CONTACT_DETAILS
        ? AccountHolderDetailsWizardPathsModel.UPDATE_PRIMARY_CONTACT
        : AccountHolderDetailsWizardPathsModel.UPDATE_ALTERNATIVE_PRIMARY_CONTACT;
    this.routePathForAddress =
      this.updateType ===
      AccountHolderDetailsType.ACCOUNT_HOLDER_PRIMARY_CONTACT_DETAILS
        ? AccountHolderDetailsWizardPathsModel.UPDATE_PRIMARY_CONTACT_WORK
        : AccountHolderDetailsWizardPathsModel.UPDATE_ALTERNATIVE_PRIMARY_CONTACT_WORK;
    this.currentValues = {
      accountHolderContact: this.accountHolderContact,
    };
    this.changedValues = {
      accountHolderContactDiff: this.getObjectDiff(
        this.accountHolderContact,
        this.accountHolderContactChanged
      ),
    };
  }

  /**
   * Delete an existing Alternative Primary Contact
   * @private
   */
  private deleteAlternativePrimaryContact() {
    this.currentValues = {
      accountHolderContact: this.accountHolderContact,
    };
    this.routePathForSelectType =
      AccountHolderDetailsWizardPathsModel.SELECT_UPDATE_TYPE;
  }

  /**
   * Add a new Alternative Primary Contact
   * @private
   */
  private addAlternativePrimaryContact() {
    this.currentValues = {
      accountHolderContact: {
        id: null,
        new: true,
        ...this.accountHolderContactChanged,
      } as AccountHolderContact,
    };
    this.routePathForDetails =
      AccountHolderDetailsWizardPathsModel.UPDATE_ALTERNATIVE_PRIMARY_CONTACT;
    this.routePathForAddress =
      AccountHolderDetailsWizardPathsModel.UPDATE_ALTERNATIVE_PRIMARY_CONTACT_WORK;
  }

  getObjectDiff(current, changed) {
    let positionInCompanyDiff = null;
    if (current['positionInCompany'] !== changed['positionInCompany']) {
      positionInCompanyDiff = {
        positionInCompany: changed['positionInCompany'],
      };
    }
    return {
      details: {
        ...this.getOnlyChangedValues(current['details'], changed['details']),
      },
      address: {
        ...this.getOnlyChangedValues(current['address'], changed['address']),
      },
      emailAddress: {
        ...this.getOnlyChangedValues(
          current['emailAddress'],
          changed['emailAddress']
        ),
      },
      phoneNumber: {
        ...this.getOnlyChangedValues(
          current['phoneNumber'],
          changed['phoneNumber']
        ),
      },
      ...positionInCompanyDiff,
    };
  }

  onSubmit() {
    if (
      this.updateType === this.deleteAlternativePrimaryContactType ||
      this.updateType === this.addAlternativePrimaryContactType
    ) {
      this.submitValuesForAddOrDeleteAlternativePrimaryContact();
    } else {
      const submittedValues = {
        accountIdentifier: this.accountIdentifier,
        accountHolderIdentifier: this.accountHolder.id,
        currentAccountHolder: this.isContactBasedUpdate
          ? this.currentValues.accountHolderContact
          : this.currentValues.accountHolder,
        accountHolderDiff: this.isContactBasedUpdate
          ? this.changedValues['accountHolderContactDiff']
          : this.changedValues['accountHolderDiff'],
        updateType: this.updateType,
      } as AccountHolderUpdate;

      if (this.checkIfDiffIsEmpty(submittedValues.accountHolderDiff)) {
        this.errorDetails.emit(
          new ErrorDetail(
            null,
            'You can not make a request without any changes.'
          )
        );
      } else {
        this.submitRequest.emit(submittedValues);
      }
    }
  }

  /**
   * Values to submit for Delete Alternative Primary Contact
   * @private
   */
  private submitValuesForAddOrDeleteAlternativePrimaryContact() {
    const submittedValues = {
      accountIdentifier: this.accountIdentifier,
      accountHolderIdentifier: this.accountHolder.id,
      currentAccountHolder: this.currentValues.accountHolderContact,
      accountHolderDiff: {},
      updateType: this.updateType,
    } as AccountHolderUpdate;
    this.submitRequest.emit(submittedValues);
  }

  navigateTo(routePath: string) {
    this.clickChange.emit(AccountHolderDetailsWizardPathsMap.get(routePath));
  }

  getSummaryListItems(): SummaryListItem[] {
    return [
      {
        key: {
          label: 'Account holder name',
          class: 'govuk-summary-list__key govuk-body-m',
        },
        value: [
          {
            label: this.accountHolder.details.name,
            class: 'govuk-summary-list__value',
          },
        ],
      },
    ];
  }

  private getOnlyChangedValues(initialValues, updatedValues) {
    const diff = {};
    if (updatedValues) {
      Object.keys(updatedValues).forEach((r) => {
        if (updatedValues[r] !== initialValues[r]) {
          diff[r] = updatedValues[r];
        }
      });
    }
    return diff;
  }

  private checkIfDiffIsEmpty(obj) {
    delete obj['details']['isOverEighteen'];
    return (
      !Object.keys(obj['details']).length &&
      !Object.keys(obj['address']).length &&
      !Object.keys(obj['emailAddress']).length &&
      !Object.keys(obj['phoneNumber']).length &&
      obj['positionInCompany'] === undefined
    );
  }
}
