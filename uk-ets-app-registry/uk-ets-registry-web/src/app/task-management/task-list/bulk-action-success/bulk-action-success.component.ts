import { Component } from '@angular/core';
import { BulkActionSuccess } from '@task-management/model';
import { Observable } from 'rxjs';
import { Store } from '@ngrx/store';
import { selectSuccess } from '../task-list.selector';

@Component({
  selector: 'app-bulk-action-success',
  templateUrl: './bulk-action-success.component.html',
  styleUrls: ['./bulk-action-success.component.scss'],
})
export class BulkActionSuccessComponent {
  bulkActionSuccess$: Observable<BulkActionSuccess> = this.store.select(
    selectSuccess
  );

  constructor(private store: Store) {}
}
