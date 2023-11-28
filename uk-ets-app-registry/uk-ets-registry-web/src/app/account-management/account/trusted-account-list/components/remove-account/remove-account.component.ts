import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { TrustedAccount } from '@shared/model/account';

@Component({
  selector: 'app-remove-account',
  templateUrl: './remove-account.component.html',
})
export class RemoveAccountComponent {
  @Input()
  eligibleTrustedAccounts: TrustedAccount[];
  @Input()
  selectedAccountsForRemoval: TrustedAccount[];
  @Output() readonly selectTrustedAccountsForRemoval = new EventEmitter<
    TrustedAccount[]
  >();
  @Output() readonly cancel = new EventEmitter<string>();

  constructor(private route: ActivatedRoute) {}

  toggle(checked: boolean, trustedAccount: TrustedAccount) {
    this.selectedAccountsForRemoval = Array.from(
      this.selectedAccountsForRemoval
    );
    if (checked) {
      this.selectedAccountsForRemoval.push(trustedAccount);
    } else {
      this.selectedAccountsForRemoval = this.selectedAccountsForRemoval.filter(
        (ta) =>
          ta.accountFullIdentifier !== trustedAccount.accountFullIdentifier
      );
    }
  }

  checked(trustedAccount: TrustedAccount) {
    return (
      this.selectedAccountsForRemoval.filter(
        (ta) =>
          ta.accountFullIdentifier === trustedAccount.accountFullIdentifier
      ).length > 0
    );
  }

  onCancel() {
    this.cancel.emit(this.route.snapshot['_routerState'].url);
  }

  onContinue() {
    this.selectTrustedAccountsForRemoval.emit(this.selectedAccountsForRemoval);
  }
}
