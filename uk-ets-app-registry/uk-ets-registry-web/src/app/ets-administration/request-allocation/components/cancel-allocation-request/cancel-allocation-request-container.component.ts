import { Component, OnInit } from '@angular/core';
import { canGoBack } from '@shared/shared.action';
import { cancelRequestAllocationConfirmed } from '@request-allocation/actions';
import { Store } from '@ngrx/store';

@Component({
  selector: 'app-cancel-allocation-request-container',
  template: `
    <app-cancel-update-request
      updateRequestText="allocation request"
      (cancelRequest)="onCancel()"
    ></app-cancel-update-request>
  `
})
export class CancelAllocationRequestContainerComponent implements OnInit {
  constructor(private store: Store) {}

  ngOnInit(): void {
    this.store.dispatch(
      canGoBack({
        goBackRoute:
          'ets-administration/request-allocation/check-allocation-request',
        extras: { skipLocationChange: true }
      })
    );
  }

  onCancel() {
    this.store.dispatch(cancelRequestAllocationConfirmed());
  }
}
