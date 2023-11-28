import { Component, Input } from '@angular/core';
import { AccountHolderContact } from '@shared/model/account';
import { ContactType } from '@shared/model/account-holder-contact-type';

@Component({
  selector: 'app-task-details-account-holder-contact-summary',
  templateUrl: './account-holder-contact-summary.component.html',
})
export class AccountHolderContactSummaryComponent {
  @Input() accountHolderContact: AccountHolderContact;
  @Input() accountHolderChanged: boolean;
  @Input() contactType: ContactType;

  getNameDetails() {
    return `${this.accountHolderContact.details.firstName} ${this.accountHolderContact.details.lastName} `;
  }

  getTypeText() {
    return this.contactType === ContactType.ALTERNATIVE
      ? 'alternative Primary Contact'
      : 'Primary Contact';
  }
}
