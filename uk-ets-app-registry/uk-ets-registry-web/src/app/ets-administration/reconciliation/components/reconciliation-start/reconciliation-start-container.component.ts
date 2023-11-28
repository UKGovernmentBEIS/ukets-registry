import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import {
  ReconciliationState,
  selectLastStartedReconciliation,
} from '@reconciliation-administration/reducers';
import { Observable } from 'rxjs';
import { Reconciliation } from '@shared/model/reconciliation-model';
import {
  fetchLastStartedReconciliation,
  startReconciliation,
} from '@reconciliation-administration/actions/reconciliation.actions';

@Component({
  selector: 'app-reconciliation-start-container',
  template: `
    <app-reconciliation-start
      [reconciliation]="lastStartedReconciliation$ | async"
      (startReconciliationClick)="startReconciliation()"
      (refreshReconciliation)="refreshLastStartedReconciliation()"
    >
    </app-reconciliation-start>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReconciliationStartContainerComponent implements OnInit {
  lastStartedReconciliation$: Observable<Reconciliation>;

  constructor(private store: Store) {}

  ngOnInit(): void {
    this.lastStartedReconciliation$ = this.store.select(
      selectLastStartedReconciliation
    );
    this.refreshLastStartedReconciliation();
  }

  startReconciliation() {
    this.store.dispatch(startReconciliation());
  }

  refreshLastStartedReconciliation() {
    this.store.dispatch(fetchLastStartedReconciliation());
  }
}
