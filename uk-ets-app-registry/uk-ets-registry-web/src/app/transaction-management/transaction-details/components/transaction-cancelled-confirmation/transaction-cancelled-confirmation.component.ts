import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-transaction-cancelled-confirmation',
  templateUrl: './transaction-cancelled-confirmation.component.html',
})
export class TransactionCancelledConfirmationComponent {
  @Input()
  transactionIdentifier: string;
}
