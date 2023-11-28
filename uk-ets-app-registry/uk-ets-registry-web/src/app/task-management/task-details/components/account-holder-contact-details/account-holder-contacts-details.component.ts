import { Component, Input } from '@angular/core';
import { AccountOpeningTaskDetails } from '@task-management/model';
import { ContactType } from '@shared/model/account-holder-contact-type';

@Component({
  selector: 'app-task-details-account-holder-contacts-details',
  templateUrl: './account-holder-contacts-details.component.html',
})
export class AccountHolderContactsDetailsComponent {
  @Input() taskDetails: AccountOpeningTaskDetails;
  @Input() accountHolderChanged: boolean;
  @Input() installationTransfer: boolean;
  primaryContactType = ContactType.PRIMARY;
  alternativeContactType = ContactType.ALTERNATIVE;
}
