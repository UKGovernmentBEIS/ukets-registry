import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Store } from '@ngrx/store';
import { AllocationJob } from '../../models/allocation-job.model';
import { SortParameters } from '@registry-web/shared/search/sort/SortParameters';
import { SharedModule } from '@registry-web/shared/shared.module';
import { SortService } from '@registry-web/shared/search/sort/sort.service';
import {
  AllocationJobStatus,
  allocationJobStatusMap,
} from '../../models/allocation-job-search-criteria.model';
import { CommonModule, Location } from '@angular/common';
import {
  ActivatedRoute,
  Router,
  RouterModule,
  RouterStateSnapshot,
} from '@angular/router';
import { operatorTypeMap } from '@registry-web/shared/model/account/operator';
import { canGoBack, canGoBackToList } from '@registry-web/shared/shared.action';
import {
  cancelPendingAllocationById,
  downloadAllocationReportById,
  navigateToTask,
} from '../../store/allocation-job-status.actions';
import { SearchMode } from '@registry-web/shared/resolvers/search.resolver';

const pathCancelPendingAllocations = 'cancel-pending-allocations';

@Component({
  standalone: true,
  selector: 'app-allocation-job-status-results',
  templateUrl: './allocation-job-status-results.component.html',
  styleUrls: ['./allocation-job-status-results.component.scss'],
  imports: [CommonModule, SharedModule, RouterModule],
  providers: [SortService],
})
export class AllocationJobStatusResultsComponent {
  @Input() results: AllocationJob[];
  @Input() sortParameters: SortParameters;
  @Output() readonly sort = new EventEmitter<SortParameters>();

  allocationJobStatusMap = allocationJobStatusMap;
  AllocationJobStatus = AllocationJobStatus;
  operatorTypeMap = operatorTypeMap;
  url: string;

  constructor(
    private store: Store,
    private router: Router,
    private route: ActivatedRoute,
    private location: Location
  ) {
    const snapshot: RouterStateSnapshot = router.routerState.snapshot;
    this.url =
      snapshot.url.indexOf('?') > -1 ? snapshot.url.split('?')[0] : null;
  }

  goToTask(requestIdentifier: string) {
    this.changeLocationState();
    this.store.dispatch(navigateToTask({ requestIdentifier }));
  }

  changeLocationState(): void {
    // We need to replace the url with the appropriate param mode when entering
    // a detailed page so to load the stored criteria when returning back to results by clicking on the browser's back button
    if (this.url) {
      this.location.go(this.url, `mode=${SearchMode.LOAD}`);
    }
  }

  onCancelAllocations(job: AllocationJob) {
    const goBackRoute = this.router.url;
    return this.router
      .navigate(['cancel-pending-allocations'], {
        skipLocationChange: true,
        queryParams: { jobId: job.id },
        relativeTo: this.route,
      })
      .then(() => this.store.dispatch(canGoBack({ goBackRoute })));
  }

  cancel(job: AllocationJob) {
    this.store.dispatch(cancelPendingAllocationById({ jobId: job.id }));
  }

  download(job: AllocationJob) {
    this.store.dispatch(downloadAllocationReportById({ jobId: job.id }));
  }
}
