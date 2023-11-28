import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import {
  selectedAllocationCategory,
  selectedAllocationYear,
} from '@request-allocation/reducers';
import { canGoBack } from '@shared/shared.action';
import {
  cancelRequestAllocationRequested,
  submitAllocationRequest,
} from '@request-allocation/actions';
import * as RequestAllocationActions from '@request-allocation/actions/request-allocation.actions';
import { AllocationCategory } from '@registry-web/shared/model/allocation';

@Component({
  selector: 'app-check-allocation-request-container',
  template: `
    <app-check-allocation-request
      [selectedAllocationYear]="selectedAllocationYear$ | async"
      [selectedAllocationCategory]="selectedAllocationCategory$ | async"
      (submitRequest)="onRequestSubmitted()"
      (downloadAllocationFile)="onDownloadFile()"
    ></app-check-allocation-request>
    <app-cancel-request-link (goToCancelScreen)="onCancel()">
    </app-cancel-request-link>
  `,
})
export class CheckAllocationRequestContainerComponent implements OnInit {
  selectedAllocationYear$: Observable<number>;
  selectedAllocationCategory$: Observable<AllocationCategory>;
  constructor(private store: Store) {}

  ngOnInit(): void {
    this.store.dispatch(
      canGoBack({
        goBackRoute: 'ets-administration/request-allocation',
        extras: { skipLocationChange: true },
      })
    );

    this.selectedAllocationYear$ = this.store.select(selectedAllocationYear);
    this.selectedAllocationCategory$ = this.store.select(
      selectedAllocationCategory
    );
  }

  onCancel() {
    this.store.dispatch(cancelRequestAllocationRequested());
  }

  onRequestSubmitted() {
    this.store.dispatch(submitAllocationRequest());
  }

  onDownloadFile() {
    this.store.dispatch(RequestAllocationActions.downloadAllocationFile());
  }
}
