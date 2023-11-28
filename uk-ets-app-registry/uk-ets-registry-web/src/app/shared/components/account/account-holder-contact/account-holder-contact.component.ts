import { Component, Input } from '@angular/core';
import { AccountHolderContact } from '../../../model/account/account-holder-contact';
import { IUkOfficialCountry } from '../../../countries/country.interface';
import * as SharedUtil from '../../../shared.util';
import { ContactType } from '@shared/model/account-holder-contact-type';

@Component({
  selector: 'app-shared-account-holder-contact',
  templateUrl: './account-holder-contact.component.html',
})
export class AccountHolderContactComponent {
  @Input()
  accountHolderContact: AccountHolderContact;
  @Input()
  countries: IUkOfficialCountry[];
  @Input()
  contactType: ContactType;

  sharedUtil = SharedUtil;

  getNameDetails() {
    return `${this.accountHolderContact.details.firstName} ${this.accountHolderContact.details.lastName} `;
  }

  getTypeText(isCapital: boolean): string {
    if (this.contactType === ContactType.ALTERNATIVE) {
      return isCapital
        ? 'Alternative Primary Contact'
        : 'alternative Primary Contact';
    } else {
      return isCapital ? 'Primary Contact' : 'Primary Contact';
    }
  }
}
