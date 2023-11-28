import { Component, EventEmitter, Input, Output } from '@angular/core';
import {
  TrustedAccountListRoutePaths,
  TrustedAccountListUpdateType,
} from '@account-management/account/trusted-account-list/model';
import { Account, TrustedAccount } from '@shared/model/account';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-check-update-request',
  templateUrl: './check-update-request.component.html',
})
export class CheckUpdateRequestComponent {
  @Input()
  account: Account;
  @Input()
  isTrustedAccountKyotoType: boolean;
  @Input()
  updateType: TrustedAccountListUpdateType;
  @Input()
  trustedAccounts: TrustedAccount[];
  @Output() readonly updateRequestChecked = new EventEmitter();
  @Output() readonly cancel = new EventEmitter<string>();
  @Output() readonly navigateToEmitter = new EventEmitter<string>();

  updateTypes = TrustedAccountListUpdateType;
  trustedAccountListRoutePaths = TrustedAccountListRoutePaths;

  constructor(private route: ActivatedRoute) {}

  getActiveRouteUrl() {
    return this.route.snapshot['_routerState'].url;
  }

  onCancel() {
    this.cancel.emit(this.route.snapshot['_routerState'].url);
  }

  onContinue() {
    this.updateRequestChecked.emit();
  }

  navigateTo(value: TrustedAccountListRoutePaths) {
    this.navigateToEmitter.emit(value);
  }
}
