import { Component, OnDestroy, OnInit } from '@angular/core';
import { IUkOfficialCountry } from '@shared/countries/country.interface';
import { Account, AccountDetails, AccountType } from '@shared/model/account';
import { Store } from '@ngrx/store';
import { Observable, Subscription } from 'rxjs';
import {
  selectAccount,
  selectUpdateAccountDetails,
} from '@account-management/account/account-details/account.selector';
import { selectCountries } from '@registry-web/registration/check-answers-and-submit/check-answers-and-submit.selectors';
import {
  cancelUpdateAccountDetails,
  submitAccountDetailsUpdate,
} from '@account-management/account/account-details/account.actions';
import { canGoBack, errors } from '@shared/shared.action';
import { ActivatedRoute } from '@angular/router';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { empty } from '@shared/shared.util';
import { isAdmin } from '@registry-web/auth/auth.selector';

@Component({
  selector: 'app-confirm-account-details-update',
  templateUrl: './confirm-account-details-update-container.component.html',
})
export class ConfirmAccountDetailsUpdateContainerComponent
  implements OnInit, OnDestroy
{
  account$: Observable<Account>;
  updatedDetails$: Observable<AccountDetails>;
  countries$: Observable<IUkOfficialCountry[]>;
  isAdmin$: Observable<boolean>;
  private subscriptionAccount: Subscription;
  private subscriptionUpdatedDetails: Subscription;

  private accountType: AccountType;
  private accountDetails: AccountDetails;
  private updatedAccountDetails: AccountDetails;

  // In case of OHA/AOHA, the account details contain only the account name
  private currentValue: string;
  private updatedValue: string;

  // In case of trading/person holding accounts, the account details contain also the billing address and the billing emails
  private currentAddress: any;
  private updatedAddress: any;

  private currentBillingContactDetails: any;
  private updatedBillingContactDetails: any;

  private currentSalesContactDetails: any;
  private updatedSalesContactDetails: any;

  constructor(private store: Store, private activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.account$ = this.store.select(selectAccount);
    this.updatedDetails$ = this.store.select(selectUpdateAccountDetails);
    this.subscriptionAccount = this.account$.subscribe(
      (t) => (
        (this.accountDetails = t?.accountDetails),
        (this.accountType = t?.accountType),
        (this.currentValue = t?.accountDetails?.name),
        (this.currentAddress = t?.accountDetails?.address),
        (this.currentBillingContactDetails =
          t?.accountDetails?.billingContactDetails),
        (this.currentSalesContactDetails =
          t?.accountDetails?.salesContactDetails)
      )
    );
    this.subscriptionUpdatedDetails = this.updatedDetails$.subscribe(
      (t) => (
        (this.updatedAccountDetails = t),
        (this.updatedValue = t?.name),
        (this.updatedAddress = t?.address),
        (this.updatedBillingContactDetails = t?.billingContactDetails),
        (this.updatedSalesContactDetails = t?.salesContactDetails)
      )
    );
    this.countries$ = this.store.select(selectCountries);
    this.isAdmin$ = this.store.select(isAdmin);
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.activatedRoute.snapshot.paramMap.get(
          'accountId'
        )}/edit`,
        extras: { skipLocationChange: true },
      })
    );
  }

  onCancel() {
    this.store.dispatch(
      cancelUpdateAccountDetails({
        currentRoute: this.activatedRoute.snapshot['_routerState'].url,
      })
    );
  }

  submitChanges() {
    const isOHAChanged =
      this.accountType === AccountType.OPERATOR_HOLDING_ACCOUNT &&
      (this.currentValue !== this.updatedValue ||
        this.salesDetailsChanged(
          this.currentSalesContactDetails,
          this.updatedSalesContactDetails
        ));

    const isAOHAChanged =
      this.accountType === AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT &&
      (this.currentValue !== this.updatedValue ||
        this.salesDetailsChanged(
          this.currentSalesContactDetails,
          this.updatedSalesContactDetails
        ));

    const isTradingChanged =
      this.accountType === AccountType.TRADING_ACCOUNT &&
      (this.currentValue !== this.updatedValue ||
        this.salesDetailsChanged(
          this.currentSalesContactDetails,
          this.updatedSalesContactDetails
        ) ||
        this.billingDetailsChanged(
          this.currentBillingContactDetails,
          this.updatedBillingContactDetails
        ) ||
        this.addressChanged(this.currentAddress, this.updatedAddress));

    const isKPChanged =
      this.accountType === AccountType.PERSON_HOLDING_ACCOUNT &&
      (this.currentValue !== this.updatedValue ||
        this.billingDetailsChanged(
          this.currentBillingContactDetails,
          this.updatedBillingContactDetails
        ) ||
        this.addressChanged(this.currentAddress, this.updatedAddress));

    const isOtherChanged =
      this.accountType !== AccountType.OPERATOR_HOLDING_ACCOUNT &&
      this.accountType !== AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT &&
      this.accountType !== AccountType.TRADING_ACCOUNT &&
      this.accountType !== AccountType.PERSON_HOLDING_ACCOUNT &&
      JSON.stringify(this.accountDetails) !==
        JSON.stringify(this.updatedAccountDetails);

    if (
      !(
        isOHAChanged ||
        isAOHAChanged ||
        isTradingChanged ||
        isKPChanged ||
        isOtherChanged
      )
    ) {
      const summary: ErrorSummary = {
        errors: [
          new ErrorDetail(
            null,
            'You can not make a request without any changes.'
          ),
        ],
      };
      this.store.dispatch(errors({ errorSummary: summary }));
    } else {
      this.store.dispatch(submitAccountDetailsUpdate());
    }
  }

  ngOnDestroy(): void {
    this.subscriptionAccount?.unsubscribe();
    this.subscriptionUpdatedDetails?.unsubscribe();
  }

  addressChanged(initial, changed): boolean {
    if (
      initial.buildingAndStreet === changed.buildingAndStreet &&
      (initial.buildingAndStreet2 === changed.buildingAndStreet2 ||
        (empty(initial.buildingAndStreet2) &&
          empty(changed.buildingAndStreet2))) &&
      (initial.buildingAndStreet3 === changed.buildingAndStreet3 ||
        (empty(initial.buildingAndStreet3) &&
          empty(changed.buildingAndStreet3))) &&
      initial.townOrCity === changed.townOrCity &&
      (initial.stateOrProvince === changed.stateOrProvince ||
        (empty(initial.stateOrProvince) && empty(changed.stateOrProvince))) &&
      initial.country === changed.country &&
      (initial.postCode === changed.postCode ||
        (empty(initial.postCode) && empty(changed.postCode)))
    ) {
      return false;
    } else {
      return true;
    }
  }

  billingDetailsChanged(initial, changed): boolean {
    if (
      initial.contactName === changed.contactName &&
      initial.phoneNumberCountryCode === changed.phoneNumberCountryCode &&
      initial.phoneNumber === changed.phoneNumber &&
      initial.email === changed.email &&
      initial.sopCustomerId === changed.sopCustomerId
    ) {
      return false;
    } else {
      return true;
    }
  }

  salesDetailsChanged(_initial, changed): boolean {
    const initial = { ..._initial } || {
      emailAddress: null,
      phoneNumber: null,
      phoneNumberCountryCode: null,
    };
    if (
      !initial.emailAddress?.emailAddress ||
      initial.emailAddress?.emailAddress === ''
    )
      initial.emailAddress = null;
    if (!initial.phoneNumber || initial.phoneNumber === '')
      initial.phoneNumber = null;
    if (!initial.phoneNumberCountryCode) initial.phoneNumberCountryCode = null;

    return (
      initial?.phoneNumberCountryCode !== changed?.phoneNumberCountryCode ||
      initial?.phoneNumber !== changed?.phoneNumber ||
      initial?.emailAddress?.emailAddress !==
        changed?.emailAddress?.emailAddress
    );
  }
}
