import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Data } from '@angular/router';
import { Store } from '@ngrx/store';
import * as PaymentListActions from '@payment-management/payment-list/store/actions/payment-list.actions';
import * as PaymentListSelectors from '@payment-management/payment-list/store/reducer/payment-list.selector';
import { isAdmin } from '@registry-web/auth/auth.selector';
import {
  PaymentSearchCriteria,
  PaymentSearchResult,
} from '@payment-management/model';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { Pagination, PageParameters } from '@shared/search/paginator';
import { SortParameters } from '@shared/search/sort/SortParameters';
import { clearErrors, errors } from '@shared/shared.action';
import { Observable } from 'rxjs';
import { Option } from '@shared/form-controls/uk-select-input/uk-select.model';
import { TaskDetailsActions } from '@task-management/task-details/actions';

@Component({
  selector: 'app-payment-list-container',
  templateUrl: './payment-list-container.component.html',
  styles: ``,
})
export class PaymentListContainerComponent implements OnInit {
  page: number;
  pageSize = 10;
  criteria: PaymentSearchCriteria;
  sortParameters: SortParameters = {
    sortField: 'updated',
    sortDirection: 'DESC',
  };
  pageSizeOptions: Option[] = [
    { label: '10', value: 10 },
    { label: '50', value: 50 },
  ];
  loadPageParametersFromState = true;

  pagination$: Observable<Pagination>;
  results$: Observable<PaymentSearchResult[]>;
  hideCriteria$: Observable<boolean>;
  resultsLoaded$: Observable<boolean>;
  storedCriteria$: Observable<PaymentSearchCriteria>;
  potentialErrors: Map<any, ErrorDetail>;
  // filtersDescriptor: FiltersDescriptor;
  isAdmin$: Observable<boolean>;
  showAdvancedSearch$: Observable<boolean>;
  sortParameters$: Observable<SortParameters>;

  constructor(
    private route: ActivatedRoute,
    private store: Store
  ) {}

  ngOnInit() {
    this.isAdmin$ = this.store.select(isAdmin);
    this.pagination$ = this.store.select(PaymentListSelectors.selectPagination);
    this.showAdvancedSearch$ = this.store.select(
      PaymentListSelectors.selectShowAdvancedSearch
    );
    this.results$ = this.store.select(PaymentListSelectors.selectResults);
    this.hideCriteria$ = this.store.select(
      PaymentListSelectors.selectHideCriteria
    );
    this.resultsLoaded$ = this.store.select(
      PaymentListSelectors.selectResultsLoaded
    );
    this.storedCriteria$ = this.store.select(
      PaymentListSelectors.selectCriteria
    );

    this.route.data.subscribe((data: Data) => {
      this.potentialErrors = data['errorMap'];
      //this.filtersDescriptor = data['filtersDescriptor'];
    });

    this.sortParameters$ = this.store.select(
      PaymentListSelectors.selectSortParameters
    );
  }

  hideShowCriteria($event: boolean) {
    $event
      ? this.store.dispatch(PaymentListActions.hideCriteria())
      : this.store.dispatch(PaymentListActions.showCriteria());
  }

  searchPayments(
    action,
    pageParameters: PageParameters,
    isReport?: boolean,
    loadPageParametersFromState?: boolean
  ) {
    this.store.dispatch(clearErrors());
    const payload = {
      criteria: this.criteria,
      pageParameters,
      sortParameters: this.sortParameters,
      potentialErrors: this.potentialErrors,
      isReport,
      loadPageParametersFromState,
    };
    this.store.dispatch(action(payload));
  }

  onSearchPayments(
    criteria: PaymentSearchCriteria,
    sortParameters: SortParameters,
    isReport?: boolean
  ) {
    this.sortParameters = sortParameters;
    this.criteria = criteria;
    this.searchPayments(
      PaymentListActions.searchPayments,
      {
        page: 0,
        pageSize: this.pageSize,
      },
      isReport,
      this.loadPageParametersFromState
    );
  }

  onSubmitClick() {
    this.loadPageParametersFromState = false;
  }

  onSort(sortParameters: SortParameters) {
    this.sortParameters = sortParameters;
    this.searchPayments(
      PaymentListActions.sortResults,
      {
        page: 0,
        pageSize: this.pageSize,
      },
      false,
      true
    );
  }

  onOpenTaskDetails(taskId: string) {
    this.store.dispatch(TaskDetailsActions.loadTaskFromList({ taskId }));
  }

  goToFirstPageOfResults(pageParameters: PageParameters) {
    this.searchPayments(
      PaymentListActions.navigateToFirstPageOfResults,
      pageParameters
    );
  }

  goToLastPageOfResults(pageParameters: PageParameters) {
    this.searchPayments(
      PaymentListActions.navigateToLastPageOfResults,
      pageParameters
    );
  }

  goToPageOfResults(pageParameters: PageParameters) {
    this.searchPayments(
      PaymentListActions.navigateToPageOfResults,
      pageParameters
    );
  }

  goToNextPageOfResults(pageParameters: PageParameters) {
    this.searchPayments(
      PaymentListActions.navigateToNextPageOfResults,
      pageParameters
    );
  }

  goToPreviousPageOfResults(pageParameters: PageParameters) {
    this.searchPayments(
      PaymentListActions.navigateToPreviousPageOfResults,
      pageParameters
    );
  }

  onChangePageSize(pageParameters: PageParameters) {
    this.searchPayments(PaymentListActions.changePageSize, pageParameters);
  }

  onError(details: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: details,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }

  toggleAdvancedSearch(event: boolean) {
    this.store.dispatch(
      PaymentListActions.showAdvancedSearch({ isVisible: event })
    );
  }
}
