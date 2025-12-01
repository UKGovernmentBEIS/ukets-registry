import { Component, input, output } from '@angular/core';
import { AccountHolderContact } from '@shared/model/account';

@Component({
  selector: 'app-account-holder-primary-contact-overview',
  templateUrl: './account-holder-primary-contact-overview.component.html',
})
export class AccountHolderPrimaryContactOverviewComponent {
  readonly accountHolderContact = input.required<AccountHolderContact>();
  readonly showChangeLink = input<boolean>(false);
  readonly goToChangeForm = output<void>();

  navigateToChangeForm() {
    this.goToChangeForm.emit();
  }
}
