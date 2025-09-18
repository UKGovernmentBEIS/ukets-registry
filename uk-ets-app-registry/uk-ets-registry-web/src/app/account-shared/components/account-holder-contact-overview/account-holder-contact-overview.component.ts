import { Component, EventEmitter, Input, Output } from '@angular/core';
import { AccountHolderContact } from '@shared/model/account';
import { ContactType } from '@shared/model/account-holder-contact-type';

//TODO merge with account-holder-contact-summary from account-opening module
@Component({
  selector: 'app-account-holder-contact-overview',
  templateUrl: './account-holder-contact-overview.component.html',
  styles: ``,
})
export class AccountHolderContactOverviewComponent {
  @Input()
  accountHolderContact: AccountHolderContact;
  @Input()
  contactType: ContactType;
  @Input()
  contactCountry: string;
  @Input()
  accountHolderExisting: boolean;
  @Input()
  contactDetailsRoute: string;
  @Input()
  headingPrefix: string;
  @Input()
  showAccountHolderContactFullNameHeading: boolean;
  @Output()
  readonly goToContactDetailsScreen = new EventEmitter();

  primaryContactType = ContactType.PRIMARY;
  alternativeContactType = ContactType.ALTERNATIVE;

  constructor() {}

  getNameDetails() {
    return `${this.accountHolderContact.details.firstName} ${this.accountHolderContact.details.lastName}`;
  }

  getPrimaryOrAlternativeContactLabel() {
    return this.contactType === ContactType.PRIMARY
      ? 'Primary Contact'
      : 'Alternative Primary Contact';
  }

  getTypeText() {
    return this.contactType === ContactType.ALTERNATIVE
      ? 'alternative Primary Contact'
      : 'Primary Contact';
  }

  navigateToContactDetails() {
    console.log(`Navigating to account holder contact details event output...`);
    this.goToContactDetailsScreen.emit();
  }
}
