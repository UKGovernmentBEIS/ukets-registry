import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-transaction-reference',
  templateUrl: './transaction-reference.component.html',
})
export class TransactionReferenceComponent {
  @Input()
  reference: string;

  getReference() {
    return this.reference ? this.reference : 'No transaction reference added';
  }
}
