import { Component, Input, OnInit } from '@angular/core';
import { AccountHolderContact } from '@shared/model/account';
import { ContactType } from '@shared/model/account-holder-contact-type';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import {
  selectAlternativeAccountHolderContact,
  selectPrimaryAccountHolderContact,
} from '@account-holder-contact/account-holder-contact.selector';
import { Store } from '@ngrx/store';
import {
  nextPage,
  setViewState,
} from '@account-holder-contact/account-holder-contact.actions';
import { AccountHolderContactWizardRoutes } from '@account-holder-contact/account-holder-contact-wizard-properties';
import { selectAccountHolderExisting } from '@account-opening/account-holder/account-holder.selector';

@Component({
  selector: 'app-account-holder-contact-summary',
  templateUrl: './account-holder-contact-summary.component.html',
})
export class AccountHolderContactSummaryComponent implements OnInit {
  @Input() accountHolderContact: AccountHolderContact;
  @Input() contactType: ContactType;
  @Input() contactCountry: string;

  accountHolderContactWizardOverview =
    AccountHolderContactWizardRoutes.ACCOUNT_HOLDER_CONTACT_PERSONAL_DETAILS;

  primaryContact$: Observable<AccountHolderContact>;
  alternativeContact$: Observable<AccountHolderContact>;
  accountHolderExisting$: Observable<boolean>;

  primaryContactType = ContactType.PRIMARY;
  alternativeContactType = ContactType.ALTERNATIVE;

  constructor(private _router: Router, private store: Store) {}

  ngOnInit() {
    this.primaryContact$ = this.store.select(selectPrimaryAccountHolderContact);
    this.alternativeContact$ = this.store.select(
      selectAlternativeAccountHolderContact
    );
    this.accountHolderExisting$ = this.store.select(
      selectAccountHolderExisting
    );
  }

  getNameDetails() {
    return `${this.accountHolderContact.details.firstName} ${this.accountHolderContact.details.lastName}`;
  }

  getPrimaryOrAlternativeContactLabel() {
    return this.contactType === ContactType.PRIMARY
      ? 'Primary Contact'
      : 'Alternative Primary Contact';
  }

  goToAccountHolderContactInfo(
    accountHolderContact: AccountHolderContact,
    contactType: string,
    path: string
  ) {
    this.store.dispatch(setViewState({ view: false }));
    this.store.dispatch(
      nextPage({
        accountHolderContact,
        contactType,
      })
    );
    this._router.navigate([path, contactType], { skipLocationChange: true });
  }

  goToAccountHolderPersonalDetails(
    accountHolderContact: AccountHolderContact,
    contactType: string
  ) {
    this.goToAccountHolderContactInfo(
      accountHolderContact,
      contactType,
      AccountHolderContactWizardRoutes.ACCOUNT_HOLDER_CONTACT_PERSONAL_DETAILS
    );
  }

  goToAccountHolderAddressDetails(
    accountHolderContact: AccountHolderContact,
    contactType: string
  ) {
    this.goToAccountHolderContactInfo(
      accountHolderContact,
      contactType,
      AccountHolderContactWizardRoutes.ACCOUNT_HOLDER_CONTACT_CONTACT_DETAILS
    );
  }

  getTypeText() {
    return this.contactType === ContactType.ALTERNATIVE
      ? 'alternative Primary Contact'
      : 'Primary Contact';
  }
}
