import { Component, Input } from '@angular/core';
import { TransactionResponse } from '@transaction-management/model';

@Component({
  selector: 'app-response-messages',
  templateUrl: './response-messages.component.html',
  styleUrls: [],
})
export class ResponseMessagesComponent {
  @Input()
  transactionResponses: Array<TransactionResponse>;
}
