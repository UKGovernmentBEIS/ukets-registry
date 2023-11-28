import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-account-transfer-request-submitted',
  templateUrl: './account-transfer-request-submitted.component.html',
})
export class AccountTransferRequestSubmittedComponent implements OnInit {
  @Input() submittedIdentifier: string;

  @Input() accountId: string;

  path: string;
  goBack: string;

  ngOnInit() {
    if (this.accountId) {
      this.path = '/account/' + this.accountId;
      this.goBack = 'Go back to the account';
    }
  }
}
