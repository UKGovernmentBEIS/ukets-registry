import { Component, OnInit } from '@angular/core';
import * as TransactionListSelectors from './transaction-list.selector';
import { Observable } from 'rxjs';
import { ActivatedRoute, Data } from '@angular/router';
import { Store } from '@ngrx/store';
import {
  PageParameters,
  Pagination,
} from '../../shared/search/paginator/paginator.model';
import { FiltersDescriptor } from './transaction-list.model';
import { Option } from '@shared/form-controls/uk-select-input/uk-select.model';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import * as TransactionListActions from './transaction-list.actions';
import { SortParameters } from '@shared/search/sort/SortParameters';
import { clearErrors, errors } from '@shared/shared.action';
import {
  Transaction,
  TransactionSearchCriteria,
} from '@shared/model/transaction';
import { isAdmin } from '@registry-web/auth/auth.selector';
import { selectIsReportSuccess } from '@reports/selectors';

@Component({
  selector: 'app-transaction-list',
  templateUrl: './transaction-list-container.component.html',
})
export class TransactionListContainerComponent implements OnInit {
  page: number;
  pageSize = 10;
  criteria: TransactionSearchCriteria;
  sortParameters: SortParameters = {
    sortField: 'lastUpdated',
    sortDirection: 'DESC',
  };
  pageSizeOptions: Option[] = [
    { label: '10', value: 10 },
    { label: '50', value: 50 },
  ];
  loadPageParametersFromState = true;

  pagination$: Observable<Pagination>;
  results$: Observable<Transaction[]>;
  hideCriteria$: Observable<boolean>;
  resultsLoaded$: Observable<boolean>;
  storedCriteria$: Observable<TransactionSearchCriteria>;
  potentialErrors: Map<any, ErrorDetail>;
  filtersDescriptor: FiltersDescriptor;
  isAdmin$: Observable<boolean>;
  isReportSuccess$: Observable<boolean>;
  showAdvancedSearch$: Observable<boolean>;
  sortParameters$: Observable<SortParameters>;

  constructor(private route: ActivatedRoute, private store: Store) {}

  ngOnInit() {
    this.isAdmin$ = this.store.select(isAdmin);
    this.pagination$ = this.store.select(
      TransactionListSelectors.selectPagination
    );
    this.showAdvancedSearch$ = this.store.select(
      TransactionListSelectors.selectShowAdvancedSearch
    );
    this.results$ = this.store.select(TransactionListSelectors.selectResults);
    this.hideCriteria$ = this.store.select(
      TransactionListSelectors.selectHideCriteria
    );
    this.resultsLoaded$ = this.store.select(
      TransactionListSelectors.selectResultsLoaded
    );
    this.storedCriteria$ = this.store.select(
      TransactionListSelectors.selectCriteria
    );

    this.isReportSuccess$ = this.store.select(selectIsReportSuccess);

    this.route.data.subscribe((data: Data) => {
      this.potentialErrors = data['errorMap'];
      this.filtersDescriptor = data['filtersDescriptor'];
    });

    this.sortParameters$ = this.store.select(
      TransactionListSelectors.selectSortParameters
    );
  }

  hideShowCriteria($event: boolean) {
    $event
      ? this.store.dispatch(TransactionListActions.hideCriteria())
      : this.store.dispatch(TransactionListActions.showCriteria());
  }

  searchTransactions(
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

  onSearchTransactions(
    criteria: TransactionSearchCriteria,
    sortParameters: SortParameters,
    isReport?: boolean
  ) {
    this.sortParameters = sortParameters;
    this.criteria = criteria;
    this.searchTransactions(
      TransactionListActions.searchTransactions,
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
    this.searchTransactions(
      TransactionListActions.sortResults,
      {
        page: 0,
        pageSize: this.pageSize,
      },
      false,
      true
    );
  }

  goToFirstPageOfResults(pageParameters: PageParameters) {
    this.searchTransactions(
      TransactionListActions.navigateToFirstPageOfResults,
      pageParameters
    );
  }

  goToLastPageOfResults(pageParameters: PageParameters) {
    this.searchTransactions(
      TransactionListActions.navigateToLastPageOfResults,
      pageParameters
    );
  }

  goToPageOfResults(pageParameters: PageParameters) {
    this.searchTransactions(
      TransactionListActions.navigateToPageOfResults,
      pageParameters
    );
  }

  goToNextPageOfResults(pageParameters: PageParameters) {
    this.searchTransactions(
      TransactionListActions.navigateToNextPageOfResults,
      pageParameters
    );
  }

  goToPreviousPageOfResults(pageParameters: PageParameters) {
    this.searchTransactions(
      TransactionListActions.navigateToPreviousPageOfResults,
      pageParameters
    );
  }

  onChangePageSize(pageParameters: PageParameters) {
    this.searchTransactions(
      TransactionListActions.changePageSize,
      pageParameters
    );
  }

  onError(details: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: details,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }

  requestReport(criteria: TransactionSearchCriteria) {
    this.onSearchTransactions(criteria, this.sortParameters, true);
  }

  toggleAdvancedSearch(event: boolean) {
    this.store.dispatch(
      TransactionListActions.showAdvancedSearch({ isVisible: event })
    );
  }
}
