import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { cancel } from '@allocation-status/actions/allocation-status.actions';

@Component({
  selector: 'app-cancel-update-allocation-status',
  template: `
    <app-cancel-update-request
      updateRequestText="allocation status"
      (cancelRequest)="onCancel()"
    ></app-cancel-update-request>
  `
})
export class CancelUpdateAllocationStatusComponent {
  constructor(private store: Store) {}

  onCancel() {
    this.store.dispatch(cancel());
  }
}
