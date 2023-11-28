import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import {
  AccountAllocationStatus,
  UpdateAllocationStatusRequest
} from '@allocation-status/model';
import { Store } from '@ngrx/store';
import { continueToUpdateRequestVerification } from '@allocation-status/actions/allocation-status.actions';
import {
  selectAnnualAllocationStatuses,
  selectUpdateRequest
} from '@allocation-status/reducers/allocation-status.selector';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { errors } from '@shared/shared.action';

@Component({
  selector: 'app-update-allocation-status-form-container',
  template: `
    <app-update-allocation-status-form
      [accountAllocationStatus]="accountAllocationStatus$ | async"
      [updateAllocationStatusRequest]="updateAllocationStatusRequest$ | async"
      (updateAllocationStatus)="onUpdateAllocationStatus($event)"
      (errorDetails)="onError($event)"
    ></app-update-allocation-status-form>
  `
})
export class UpdateAllocationStatusFormContainerComponent implements OnInit {
  accountAllocationStatus$: Observable<AccountAllocationStatus>;
  updateAllocationStatusRequest$: Observable<UpdateAllocationStatusRequest>;

  constructor(private store: Store) {}

  ngOnInit(): void {
    this.accountAllocationStatus$ = this.store.select(
      selectAnnualAllocationStatuses
    );
    this.updateAllocationStatusRequest$ = this.store.select(
      selectUpdateRequest
    );
  }

  onUpdateAllocationStatus($event: UpdateAllocationStatusRequest) {
    this.store.dispatch(
      continueToUpdateRequestVerification({
        updateAllocationStatusRequest: $event
      })
    );
  }

  onError(details: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: details
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
