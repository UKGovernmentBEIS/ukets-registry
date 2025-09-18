import { Component, Input } from '@angular/core';
import { AccountHolder, AccountHolderType } from '@shared/model/account';

@Component({
  selector: 'app-account-holder-short-overview',
  templateUrl: './account-holder-short-overview.component.html',
  styles: ``,
})
export class AccountHolderShortOverviewComponent {
  @Input()
  heading: string;
  @Input()
  accountHolder: AccountHolder;

  accountHolderTypes = AccountHolderType;
}
