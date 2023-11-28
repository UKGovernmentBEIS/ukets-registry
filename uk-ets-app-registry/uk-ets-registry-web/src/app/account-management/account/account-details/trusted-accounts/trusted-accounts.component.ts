import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  AccountHolder,
  TALSearchCriteria,
  TrustedAccount,
  TrustedAccountList,
  TrustedAccountStatus,
} from '@shared/model/account';
import { Option } from '@shared/form-controls/uk-select-input/uk-select.model';
import { TrustedAccountListType } from '@shared/components/account/trusted-account-table';
import { ActivatedRoute, Router } from '@angular/router';
import {
  PageParameters,
  Pagination,
} from '@registry-web/shared/search/paginator';
import { SortParameters } from '@registry-web/shared/search/sort/SortParameters';
import { Observable } from 'rxjs';
import * as TrustedAccountListActions from './trusted-accounts.actions';
import { Store } from '@ngrx/store';
import {
  selectTrustedAccountPagination,
  selectTrustedAccountShowHideCriteria,
  selectTrustedAccountSortParameters,
} from './trusted-accounts.selector';
import { ErrorDetail, ErrorSummary } from '@registry-web/shared/error-summary';
import { clearErrors, errors } from '@registry-web/shared/shared.action';
import { hideCancelPendingActivationSuccessBanner } from '@trusted-account-list/actions/trusted-account-list.actions';

@Component({
  selector: 'app-trusted-accounts',
  templateUrl: './trusted-accounts.component.html',
})
export class TrustedAccountsComponent implements OnInit {
  @Input()
  trustedAccounts: TrustedAccountList;
  @Input()
  accountHolder: AccountHolder;
  @Input()
  canRequestUpdate: boolean;
  @Output()
  readonly trustedAccountFullIdentifierDescriptionUpdate = new EventEmitter<TrustedAccount>();
  @Input()
  isCancelPendingActivationSuccessDisplayed: boolean;
  @Input()
  selectedTrustedAccountDescription: string;
  @Input()
  accountFullId: string;

  trustedAccountListTypes = TrustedAccountListType;
  pagination$: Observable<Pagination>;
  hideCriteria$: Observable<boolean>;
  sortParameters$: Observable<SortParameters>;

  pageSizeOptions: Option[] = [
    { label: '10', value: 10 },
    { label: '50', value: 50 },
  ];
  page: number;
  pageSize = 10;
  pageTotals = 10;
  criteria: TALSearchCriteria;
  sortParameters: SortParameters = {
    sortField: 'accountFullIdentifier',
    sortDirection: 'ASC',
  };

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private store: Store
  ) {}

  ngOnInit(): void {
    this.pagination$ = this.store.select(selectTrustedAccountPagination);
    this.hideCriteria$ = this.store.select(
      selectTrustedAccountShowHideCriteria
    );
    this.sortParameters$ = this.store.select(
      selectTrustedAccountSortParameters
    );
  }

  loadTrustedAccountUpdateDescription(trustedAccount: TrustedAccount) {
    this.trustedAccountFullIdentifierDescriptionUpdate.emit(trustedAccount);
  }

  getTrustedAccounts() {
    return this.trustedAccounts?.results?.filter(
      (ta) => ta.status !== TrustedAccountStatus.REJECTED
    );
  }

  searchAccounts(action, pageParameters: PageParameters) {
    this.store.dispatch(clearErrors());
    const payload = {
      criteria: this.criteria,
      pageParameters,
      sortParameters: this.sortParameters,
    };
    this.store.dispatch(action(payload));
  }

  onSearchTrustedAccounts(
    criteria: TALSearchCriteria,
    sortParameters: SortParameters
  ) {
    this.sortParameters = sortParameters;
    this.criteria = criteria;
    this.store.dispatch(hideCancelPendingActivationSuccessBanner());
    this.searchAccounts(TrustedAccountListActions.searchTAL, {
      page: 0,
      pageSize: this.pageSize,
    });
  }

  onSort(sortParameters: SortParameters) {
    this.sortParameters = sortParameters;
    this.searchAccounts(TrustedAccountListActions.sortResults, {
      page: 0,
      pageSize: this.pageSize,
    });
  }

  goToFirstPageOfResults(pageParameters: PageParameters) {
    this.searchAccounts(
      TrustedAccountListActions.navigateToFirstPageOfResults,
      pageParameters
    );
  }

  goToLastPageOfResults(pageParameters: PageParameters) {
    this.searchAccounts(
      TrustedAccountListActions.navigateToLastPageOfResults,
      pageParameters
    );
  }

  goToPageOfResults(pageParameters: PageParameters) {
    this.searchAccounts(
      TrustedAccountListActions.navigateToPageOfResults,
      pageParameters
    );
  }

  goToNextPageOfResults(pageParameters: PageParameters) {
    this.searchAccounts(
      TrustedAccountListActions.navigateToNextPageOfResults,
      pageParameters
    );
  }

  goToPreviousPageOfResults(pageParameters: PageParameters) {
    this.searchAccounts(
      TrustedAccountListActions.navigateToPreviousPageOfResults,
      pageParameters
    );
  }

  onChangePageSize(pageParameters: PageParameters) {
    this.searchAccounts(
      TrustedAccountListActions.changePageSize,
      pageParameters
    );
  }

  showHideCriteria($event: boolean) {
    this.store.dispatch(
      TrustedAccountListActions.showHideCriteria({ showHide: $event })
    );
  }

  onError(details: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: details,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
