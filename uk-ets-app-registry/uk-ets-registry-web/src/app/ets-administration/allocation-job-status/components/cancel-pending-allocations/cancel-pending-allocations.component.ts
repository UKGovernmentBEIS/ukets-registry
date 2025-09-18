import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { cancelPendingAllocationById } from '@registry-web/ets-administration/allocation-job-status/store/allocation-job-status.actions';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-cancel-pending-allocations',
  templateUrl: './cancel-pending-allocations.component.html',
})
export class CancelPendingAllocationsComponent {
  jobId: number;

  constructor(private store: Store, private route: ActivatedRoute) {
    this.route.queryParams.subscribe((params) => {
      this.jobId = params['jobId'];
    });
  }

  cancel() {
    console.log(`cancelling ${this.jobId}`);
    this.store.dispatch(cancelPendingAllocationById({ jobId: this.jobId }));
  }
}
