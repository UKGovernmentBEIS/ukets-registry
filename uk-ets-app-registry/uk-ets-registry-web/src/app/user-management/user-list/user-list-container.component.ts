import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import * as UserListSelectors from './user-list.selector';
import { Observable } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import {
  PageParameters,
  Pagination,
} from '@shared/search/paginator/paginator.model';
import { UserProjection, UserSearchCriteria } from './user-list.model';
import { Option } from '@shared/form-controls/uk-select-input/uk-select.model';
import { ErrorDetail } from '@shared/error-summary/error-detail';
import * as UserListActions from './user-list.actions';
import { SortParameters } from '@shared/search/sort/SortParameters';
import { clearErrors, errors } from '@shared/shared.action';
import { ErrorSummary } from '@shared/error-summary/error-summary';
import { selectIsReportSuccess } from '@reports/selectors';

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list-container.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UserListContainerComponent implements OnInit {
  page: number;
  pageSize = 10;
  criteria: UserSearchCriteria;
  sortParameters: SortParameters = {
    sortField: 'registeredOn',
    sortDirection: 'DESC',
  };
  pageSizeOptions: Option[] = [
    { label: '10', value: 10 },
    { label: '50', value: 50 },
  ];
  loadPageParametersFromState = true;

  pagination$: Observable<Pagination>;
  results$: Observable<UserProjection[]>;
  hideCriteria$: Observable<boolean>;
  resultsLoaded$: Observable<boolean>;
  storedCriteria$: Observable<UserSearchCriteria>;
  potentialErrors: Map<any, ErrorDetail>;
  isReportSuccess$: Observable<boolean>;
  sortParameters$: Observable<SortParameters>;

  constructor(private route: ActivatedRoute, private store: Store) {}

  ngOnInit() {
    this.pagination$ = this.store.select(UserListSelectors.selectPagination);
    this.results$ = this.store.select(UserListSelectors.selectResults);
    this.hideCriteria$ = this.store.select(
      UserListSelectors.selectHideCriteria
    );
    this.resultsLoaded$ = this.store.select(
      UserListSelectors.selectResultsLoaded
    );
    this.storedCriteria$ = this.store.select(UserListSelectors.selectCriteria);
    this.isReportSuccess$ = this.store.select(selectIsReportSuccess);

    this.sortParameters$ = this.store.select(
      UserListSelectors.selectSortParameters
    );
  }

  hideShowCriteria($event: boolean) {
    $event
      ? this.store.dispatch(UserListActions.hideCriteria())
      : this.store.dispatch(UserListActions.showCriteria());
  }

  searchUsers(
    action,
    pageParameters: PageParameters,
    loadPageParametersFromState?: boolean
  ) {
    this.store.dispatch(clearErrors());
    const payload = {
      criteria: this.criteria,
      pageParameters,
      sortParameters: this.sortParameters,
      potentialErrors: this.potentialErrors,
      loadPageParametersFromState,
    };
    this.store.dispatch(action(payload));
  }

  onSearchUsers(criteria: UserSearchCriteria, sortParameters: SortParameters) {
    this.criteria = criteria;
    this.sortParameters = sortParameters;
    this.searchUsers(
      UserListActions.searchUsers,
      {
        page: 0,
        pageSize: this.pageSize,
      },
      this.loadPageParametersFromState
    );
  }

  onSubmitClick() {
    this.loadPageParametersFromState = false;
  }

  onSort(sortParameters: SortParameters) {
    this.sortParameters = sortParameters;
    this.searchUsers(
      UserListActions.sortResults,
      {
        page: 0,
        pageSize: this.pageSize,
      },
      true
    );
  }

  goToFirstPageOfResults(pageParameters: PageParameters) {
    this.searchUsers(
      UserListActions.navigateToFirstPageOfResults,
      pageParameters
    );
  }

  goToLastPageOfResults(pageParameters: PageParameters) {
    this.searchUsers(
      UserListActions.navigateToLastPageOfResults,
      pageParameters
    );
  }

  goToPageOfResults(pageParameters: PageParameters) {
    this.searchUsers(UserListActions.navigateToPageOfResults, pageParameters);
  }

  goToNextPageOfResults(pageParameters: PageParameters) {
    this.searchUsers(
      UserListActions.navigateToNextPageOfResults,
      pageParameters
    );
  }

  goToPreviousPageOfResults(pageParameters: PageParameters) {
    this.searchUsers(
      UserListActions.navigateToPreviousPageOfResults,
      pageParameters
    );
  }

  onChangePageSize(pageParameters: PageParameters) {
    this.pageSize = pageParameters.pageSize;
    this.searchUsers(UserListActions.changePageSize, pageParameters);
  }

  onError(details: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: details,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
