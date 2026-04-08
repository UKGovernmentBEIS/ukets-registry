import { Component, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-bulk-claim-account-info',
  templateUrl: './bulk-claim-account-info.component.html',
  styles: ``,
})
export class BulkClaimAccountInfoComponent {
  @Output()
  readonly sendInvitationClicked = new EventEmitter();

  onContinue() {
    this.sendInvitationClicked.emit();
  }
}
