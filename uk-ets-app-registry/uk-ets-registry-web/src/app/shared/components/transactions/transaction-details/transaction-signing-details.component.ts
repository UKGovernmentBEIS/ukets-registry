import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-transaction-signing-details',
  templateUrl: './transaction-signing-details.component.html',
})
export class TransactionSigningDetailsComponent {
  @Input()
  transactionId: string;

  @Input()
  isTaskBased: boolean;
}
