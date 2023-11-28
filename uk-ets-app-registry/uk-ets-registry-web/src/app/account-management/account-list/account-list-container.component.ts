import { Component, OnInit } from '@angular/core';
import { Observable, of } from 'rxjs';
import {
  PageParameters,
  Pagination,
} from '../../shared/search/paginator/paginator.model';
import { AccountSearchCriteria, FiltersDescriptor } from './account-list.model';
import { ActivatedRoute, Data } from '@angular/router';
import { Store } from '@ngrx/store';
import { SortParameters } from '@shared/search/sort/SortParameters';
import { Option } from '@shared/form-controls/uk-select-input/uk-select.model';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import * as AccountListSelectors from './account-list.selector';
import { clearErrors, errors } from '@shared/shared.action';
import * as AccountListActions from './account-list.actions';
import { SortService } from '@shared/search/sort/sort.service';
import { AccountApiService } from '../service/account-api.service';
import { selectIsReportSuccess } from '@reports/selectors';
import {
  isAdmin,
  isAuthorizedRepresentative,
} from '@registry-web/auth/auth.selector';
import { Account } from '@shared/model/account';

@Component({
  selector: 'app-account-list',
  templateUrl: './account-list-container.component.html',
  providers: [AccountApiService, SortService],
})
export class AccountListContainerComponent implements OnInit {
  page: number;
  pageSize = 10;
  criteria: AccountSearchCriteria;
  sortParameters: SortParameters = {
    sortField: 'accountHolderName',
    sortDirection: 'ASC',
  };
  pageSizeOptions: Option[] = [
    { label: '10', value: 10 },
    { label: '50', value: 50 },
  ];
  loadPageParametersFromState = true;

  pagination$: Observable<Pagination>;
  results$: Observable<Account[]>;
  hideCriteria$: Observable<boolean>;
  resultsLoaded$: Observable<boolean>;
  storedCriteria$: Observable<AccountSearchCriteria>;
  potentialErrors: Map<any, ErrorDetail>;
  filtersDescriptor: FiltersDescriptor;
  isReportSuccess$: Observable<boolean>;
  isAdmin$: Observable<boolean>;
  isAR$: Observable<boolean>;
  showAdvancedSearch$: Observable<boolean>;
  sortParameters$: Observable<SortParameters>;

  constructor(private route: ActivatedRoute, private store: Store) {}

  ngOnInit() {
    this.pagination$ = this.store.select(AccountListSelectors.selectPagination);
    this.isAdmin$ = this.store.select(isAdmin);
    this.isAR$ = this.store.select(isAuthorizedRepresentative);
    this.results$ = this.store.select(AccountListSelectors.selectResults);
    this.hideCriteria$ = this.store.select(
      AccountListSelectors.selectHideCriteria
    );
    this.showAdvancedSearch$ = this.store.select(
      AccountListSelectors.selectShowAdvancedSearch
    );
    this.resultsLoaded$ = this.store.select(
      AccountListSelectors.selectResultsLoaded
    );
    this.storedCriteria$ = this.store.select(
      AccountListSelectors.selectCriteria
    );

    this.sortParameters$ = this.store.select(
      AccountListSelectors.selectSortParameters
    );

    this.isReportSuccess$ = this.store.select(selectIsReportSuccess);

    this.route.data.subscribe((data: Data) => {
      this.potentialErrors = data['errorMap'];
      this.filtersDescriptor = data['filtersDescriptor'];
    });
  }

  searchAccounts(
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

  onSubmitClick() {
    this.loadPageParametersFromState = false;
  }

  onSearchAccounts(
    criteria: AccountSearchCriteria,
    sortParameters: SortParameters,
    isReport?: boolean
  ) {
    this.sortParameters = sortParameters;
    this.criteria = criteria;
    this.searchAccounts(
      AccountListActions.searchAccounts,
      {
        page: 0,
        pageSize: this.pageSize,
      },
      isReport,
      this.loadPageParametersFromState
    );
  }

  onSort(sortParameters: SortParameters) {
    this.sortParameters = sortParameters;
    this.searchAccounts(
      AccountListActions.sortResults,
      {
        page: 0,
        pageSize: this.pageSize,
      },
      false,
      true
    );
  }

  goToFirstPageOfResults(pageParameters: PageParameters) {
    this.searchAccounts(
      AccountListActions.navigateToFirstPageOfResults,
      pageParameters
    );
  }

  goToLastPageOfResults(pageParameters: PageParameters) {
    this.searchAccounts(
      AccountListActions.navigateToLastPageOfResults,
      pageParameters
    );
  }

  goToPageOfResults(pageParameters: PageParameters) {
    this.searchAccounts(
      AccountListActions.navigateToPageOfResults,
      pageParameters
    );
  }

  goToNextPageOfResults(pageParameters: PageParameters) {
    this.searchAccounts(
      AccountListActions.navigateToNextPageOfResults,
      pageParameters
    );
  }

  goToPreviousPageOfResults(pageParameters: PageParameters) {
    this.searchAccounts(
      AccountListActions.navigateToPreviousPageOfResults,
      pageParameters
    );
  }

  onChangePageSize(pageParameters: PageParameters) {
    this.searchAccounts(AccountListActions.changePageSize, pageParameters);
  }

  hideShowCriteria($event: boolean) {
    $event
      ? this.store.dispatch(AccountListActions.hideCriteria())
      : this.store.dispatch(AccountListActions.showCriteria());
  }

  onError(details: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: details,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }

  requestReport(criteria: AccountSearchCriteria) {
    this.onSearchAccounts(criteria, this.sortParameters, true);
  }

  toggleAdvancedSearch(event: boolean) {
    this.store.dispatch(
      AccountListActions.showAdvancedSearch({ isVisible: event })
    );
  }
}
