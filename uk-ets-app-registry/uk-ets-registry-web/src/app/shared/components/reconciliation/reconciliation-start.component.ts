import { Component, EventEmitter, Input, Output } from '@angular/core';
import {
  Reconciliation,
  reconciliationStatusMap,
} from '@shared/model/reconciliation-model';

@Component({
  selector: 'app-reconciliation-start',
  templateUrl: './reconciliation-start.component.html',
})
export class ReconciliationStartComponent {
  @Input() title = 'Reconciliation Process Administration';
  @Input() reconciliation: Reconciliation;

  @Output() readonly startReconciliationClick = new EventEmitter<void>();
  @Output() readonly refreshReconciliation = new EventEmitter<void>();

  readonly reconciliationStatusMap = reconciliationStatusMap;

  refresh() {
    this.refreshReconciliation.next();
  }

  startReconciliation() {
    this.startReconciliationClick.next();
  }
}
