import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { Reconciliation } from '@shared/model/reconciliation-model';
import { selectLatestKpReconciliation } from '@kp-administration/store';
import { fetchLatestKpReconciliation } from '@kp-administration/store/actions/itl-reconcilation-actions';

@Component({
  selector: 'app-reconciliation-administration-container',
  template: `
    <app-reconciliation-start
      title="KP reconciliation administration"
      [reconciliation]="lastStartedReconciliation$ | async"
      (refreshReconciliation)="refreshLatestReconciliation()"
    >
    </app-reconciliation-start>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReconciliationAdministrationContainerComponent implements OnInit {
  lastStartedReconciliation$: Observable<Reconciliation>;

  constructor(private store: Store) {}

  ngOnInit(): void {
    this.refreshLatestReconciliation();
    this.lastStartedReconciliation$ = this.store.select(
      selectLatestKpReconciliation
    );
  }

  refreshLatestReconciliation() {
    this.store.dispatch(fetchLatestKpReconciliation());
  }
}
