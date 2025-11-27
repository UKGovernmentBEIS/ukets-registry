import { Component, Input } from '@angular/core';
import { AccountHolderType } from '@shared/model/account';

@Component({
  selector: 'app-account-holder-heading-overview',
  templateUrl: './account-holder-heading-overview.component.html',
  styles: ``,
})
export class AccountHolderHeadingOverviewComponent {
  @Input()
  headingCaption: string;
  @Input()
  accountHolderType: AccountHolderType;
  @Input()
  accountHolderCompleted: boolean;
  @Input()
  accountHolderExisting: boolean;

  accountHolderTypes = AccountHolderType;
}
