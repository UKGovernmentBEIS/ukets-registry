import { Component, EventEmitter, Input, Output } from '@angular/core';
import { AccountHolder, AccountHolderType } from '@shared/model/account';

@Component({
  selector: 'app-account-holder-overview',
  templateUrl: './account-holder-overview.component.html',
  styles: ``,
})
export class AccountHolderOverviewComponent {
  @Input()
  headingCaption: string;
  @Input()
  accountHolder: AccountHolder;
  @Input()
  accountHolderCompleted: boolean;
  @Input()
  accountHolderExisting: boolean;

  accountHolderTypes = AccountHolderType;

  @Output()
  readonly goToAccountHolderDetailsScreen = new EventEmitter();

  constructor() {}

  navigateToAccountHolderDetails() {
    this.goToAccountHolderDetailsScreen.emit();
  }
}
