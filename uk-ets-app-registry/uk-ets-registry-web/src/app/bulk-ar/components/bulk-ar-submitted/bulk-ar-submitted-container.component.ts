import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { selectBulkArRequestId } from '@registry-web/bulk-ar/reducers';

@Component({
  selector: 'app-bulk-ar-submitted-container',
  template: `
    <app-bulk-ar-submitted
      [numberOfBulkArTasks]="requestId$ | async"
    ></app-bulk-ar-submitted>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BulkArSubmittedContainerComponent implements OnInit {
  requestId$: Observable<string>;

  constructor(private store: Store) {}

  ngOnInit() {
    this.requestId$ = this.store.select(selectBulkArRequestId);
  }
}
