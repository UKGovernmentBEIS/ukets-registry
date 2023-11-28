import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { AccountHolderContactInfo } from '@shared/model/account';

import { Store } from '@ngrx/store';
import {
  selectAccountHolderContactInfo,
  selectAlternativeContactAddressCountry,
  selectPrimaryContactAddressCountry,
} from '@account-holder-contact/account-holder-contact.selector';
import { ContactType } from '@shared/model/account-holder-contact-type';

@Component({
  selector: 'app-account-holder-contacts-details',
  templateUrl: './account-holder-contacts-details.component.html',
})
export class AccountHolderContactsDetailsComponent {
  accountHolderContactInfo$: Observable<AccountHolderContactInfo> =
    this.store.select(
      selectAccountHolderContactInfo
    ) as Observable<AccountHolderContactInfo>;
  primaryType = ContactType.PRIMARY;
  alternativeType = ContactType.ALTERNATIVE;
  primaryContactAddressCountry$: Observable<string>;
  alternativeContactAddressCountry$: Observable<string>;

  constructor(private store: Store) {}

  ngOnInit() {
    this.primaryContactAddressCountry$ = this.store.select(
      selectPrimaryContactAddressCountry
    );
    this.alternativeContactAddressCountry$ = this.store.select(
      selectAlternativeContactAddressCountry
    );
  }
}
