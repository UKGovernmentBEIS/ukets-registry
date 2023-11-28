import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { selectAllocationTableRequestId } from '@allocation-table/reducers/allocation-table.selector';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-allocation-table-submitted-container',
  template: `
    <app-allocation-table-submitted
      [requestId]="requestId$ | async"
    ></app-allocation-table-submitted>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AllocationTableSubmittedContainerComponent implements OnInit {
  requestId$: Observable<string>;

  constructor(private store: Store) {}

  ngOnInit() {
    this.requestId$ = this.store.select(selectAllocationTableRequestId);
  }
}
