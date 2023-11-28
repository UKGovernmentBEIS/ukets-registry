import { Component, Input } from '@angular/core';
import { AccountHolderContact } from '@registry-web/shared/model/account';

@Component({
  selector: 'app-acquiring-account-holder-contact-summary',
  templateUrl: './acquiring-account-holder-contact-summary.component.html',
  styles: [],
})
export class AcquiringAccountHolderContactSummaryComponent {
  @Input()
  acquiringAccountHolderPrimaryContact: AccountHolderContact;
}
