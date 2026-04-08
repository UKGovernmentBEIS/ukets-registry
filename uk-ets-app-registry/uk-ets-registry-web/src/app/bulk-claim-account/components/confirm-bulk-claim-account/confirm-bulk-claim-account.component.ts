import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-confirm-bulk-claim-account',
  templateUrl: './confirm-bulk-claim-account.component.html',
})
export class ConfirmBulkClaimAccountComponent {
  @Output()
  readonly submitRequest = new EventEmitter();
  @Input()
  numberAffectedAccounts!: number;

  onContinue() {
    this.submitRequest.emit();
  }
}
