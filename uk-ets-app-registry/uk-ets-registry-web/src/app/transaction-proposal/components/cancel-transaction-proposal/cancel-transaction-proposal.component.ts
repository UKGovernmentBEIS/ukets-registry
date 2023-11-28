import { Component, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-cancel-transaction-proposal',
  templateUrl: './cancel-transaction-proposal.component.html',
})
export class CancelTransactionProposalComponent {
  @Output() readonly cancelProposal = new EventEmitter();

  onCancel() {
    this.cancelProposal.emit();
  }
}
