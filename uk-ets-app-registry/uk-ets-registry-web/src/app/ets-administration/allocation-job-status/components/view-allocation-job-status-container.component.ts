import { Component, ChangeDetectionStrategy, OnInit } from '@angular/core';
import * as AllocationJobStatusActions from '../store/allocation-job-status.actions';
import { Store } from '@ngrx/store';
import { Observable, of } from 'rxjs';
import { CommonModule } from '@angular/common';
import { AllocationJob } from '../models/allocation-job.model';
import { AllocationJobStatusFiltersComponent } from './allocation-job-status-filters/allocation-job-status-filters.component';
import { AllocationJobStatusResultsComponent } from './allocation-job-status-results/allocation-job-status-results.component';
import { AllocationJobSearchCriteria } from '../models/allocation-job-search-criteria.model';
import { ErrorDetail, ErrorSummary } from '@registry-web/shared/error-summary';
import {
  canGoBackToList,
  clearErrors,
  errors,
} from '@registry-web/shared/shared.action';
import { SortParameters } from '@registry-web/shared/search/sort/SortParameters';
import {
  PageParameters,
  Pagination,
} from '@registry-web/shared/search/paginator';
import { Option } from '@shared/form-controls/uk-select-input/uk-select.model';
import { SharedModule } from '@registry-web/shared/shared.module';
import {
  selectCriteria,
  selectHideCriteria,
  selectPagination,
  selectResults,
  selectResultsLoaded,
  selectSortParameters,
} from '../store/allocation-job-status.selectors';
import { SearchMode } from '@registry-web/shared/resolvers/search.resolver';

@Component({
  standalone: true,
  selector: 'app-view-allocation-job-status-container',
  templateUrl: './view-allocation-job-status-container.component.html',
  imports: [
    CommonModule,
    SharedModule,
    AllocationJobStatusFiltersComponent,
    AllocationJobStatusResultsComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ViewAllocationJobStatusContainerComponent implements OnInit {
  allocationJobs$: Observable<AllocationJob[]>;
  hideCriteria$: Observable<boolean>;
  storedCriteria$: Observable<AllocationJobSearchCriteria>;

  pagination$: Observable<Pagination>;
  results$: Observable<AllocationJob[]>;
  resultsLoaded$: Observable<boolean>;
  potentialErrors: Map<any, ErrorDetail>;
  // filtersDescriptor: FiltersDescriptor;
  sortParameters$: Observable<SortParameters>;

  page: number;
  pageSize = 10;
  criteria: AllocationJobSearchCriteria;
  sortParameters: SortParameters = {
    sortField: 'id',
    sortDirection: 'DESC',
  };
  pageSizeOptions: Option[] = [
    { label: '10', value: 10 },
    { label: '50', value: 50 },
  ];
  loadPageParametersFromState = true;

  constructor(private store: Store) {}

  ngOnInit() {
    this.hideCriteria$ = this.store.select(selectHideCriteria);
    this.storedCriteria$ = this.store.select(selectCriteria);
    this.pagination$ = this.store.select(selectPagination);
    this.results$ = this.store.select(selectResults);
    this.resultsLoaded$ = this.store.select(selectResultsLoaded);
    this.sortParameters$ = this.store.select(selectSortParameters);

    this.store.dispatch(
      canGoBackToList({
        goBackToListRoute: `/ets-administration/view-allocation-job-status`,
        extras: {
          queryParams: { mode: SearchMode.CACHE },
        },
      })
    );
  }

  toggleCriteria() {
    this.store.dispatch(AllocationJobStatusActions.toggleCriteria());
  }

  onSubmit() {
    this.loadPageParametersFromState = false;
  }

  onError(details: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: details,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }

  onSort(sortParameters: SortParameters) {
    this.sortParameters = sortParameters;
    this.search(
      AllocationJobStatusActions.sortResults,
      {
        page: 0,
        pageSize: this.pageSize,
      },
      true
    );
  }

  onSearch(
    criteria: AllocationJobSearchCriteria,
    sortParameters: SortParameters
  ) {
    this.sortParameters = sortParameters;
    this.criteria = criteria;
    this.search(
      AllocationJobStatusActions.searchAllocationJobs,
      {
        page: 0,
        pageSize: this.pageSize,
      },
      this.loadPageParametersFromState
    );
  }

  goToFirstPageOfResults(pageParameters: PageParameters) {
    this.search(
      AllocationJobStatusActions.navigateToFirstPageOfResults,
      pageParameters
    );
  }

  goToLastPageOfResults(pageParameters: PageParameters) {
    this.search(
      AllocationJobStatusActions.navigateToLastPageOfResults,
      pageParameters
    );
  }

  goToPageOfResults(pageParameters: PageParameters) {
    this.search(
      AllocationJobStatusActions.navigateToPageOfResults,
      pageParameters
    );
  }

  goToNextPageOfResults(pageParameters: PageParameters) {
    this.search(
      AllocationJobStatusActions.navigateToNextPageOfResults,
      pageParameters
    );
  }

  goToPreviousPageOfResults(pageParameters: PageParameters) {
    this.search(
      AllocationJobStatusActions.navigateToPreviousPageOfResults,
      pageParameters
    );
  }

  onChangePageSize(pageParameters: PageParameters) {
    this.search(AllocationJobStatusActions.changePageSize, pageParameters);
  }

  search(
    action,
    pageParameters: PageParameters,
    loadPageParametersFromState?: boolean
  ) {
    console.log('search', this.criteria);
    this.clearErrors();
    const payload = {
      criteria: this.criteria,
      pageParameters,
      sortParameters: this.sortParameters,
      potentialErrors: this.potentialErrors,
      loadPageParametersFromState,
    };
    this.store.dispatch(action(payload));
  }

  private clearErrors() {
    this.store.dispatch(clearErrors());
  }
}
