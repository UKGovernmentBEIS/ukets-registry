import {
  ChangeDetectionStrategy,
  Component,
  Input,
  OnInit,
} from '@angular/core';
import {
  getRuleLabel,
  TrustedAccountListRules,
} from '@shared/model/account/trusted-account-list-rules';
import { ActivatedRoute, Router } from '@angular/router';
import * as AccountTransactionsActions from './account-transactions.actions';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { Pagination } from '@shared/search/paginator';
import { SearchActionPayload } from '@account-management/account/account-details/transactions/transaction-list.model';
import { Transaction } from '@shared/model/transaction';
import {
  selectAccountTransactions,
  selectTransactionPagination,
} from '@account-management/account/account-details/transactions/account-transactions.selector';
import { SearchMode } from '@shared/resolvers/search.resolver';
import { Configuration } from '@shared/configuration/configuration.interface';
import { selectConfigurationRegistry } from '@shared/shared.selector';

@Component({
  selector: 'app-transactions-container',
  templateUrl: './transactions-container.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TransactionsContainerComponent implements OnInit {
  @Input()
  fullIdentifier: string;
  @Input()
  trustedAccountListRules: TrustedAccountListRules;
  @Input()
  canRequestUpdate: boolean;
  @Input()
  isOHAOrAOHA: boolean;
  @Input()
  isKyotoAccountType: boolean;

  transactions$: Observable<Transaction[]>;
  pagination$: Observable<Pagination>;
  configuration$: Observable<Configuration[]>;

  pageParameters = {
    page: 0,
    pageSize: 10,
  };
  sortParameters = {
    sortField: 'lastUpdated',
    sortDirection: 'DESC',
  };

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private store: Store
  ) {}

  ngOnInit() {
    this.store.dispatch(
      AccountTransactionsActions.fetchAccountTransactions({
        fullIdentifier: this.fullIdentifier,
        pageParameters: this.pageParameters,
        sortParameters: this.sortParameters,
      })
    );
    this.transactions$ = this.store.select(selectAccountTransactions);
    this.pagination$ = this.store.select(selectTransactionPagination);
    this.configuration$ = this.store.select(selectConfigurationRegistry);
  }

  goToRequestUpdate() {
    this.router.navigate([
      this.activatedRoute.snapshot['_routerState'].url +
        '/tal-transaction-rules',
    ]);
  }

  onRefreshTransactionResults(searchActionPayload: SearchActionPayload) {
    this.store.dispatch(
      AccountTransactionsActions.fetchAccountTransactions({
        fullIdentifier: this.fullIdentifier,
        pageParameters: searchActionPayload.pageParameters,
        sortParameters: searchActionPayload.sortParameters,
        loadPageParametersFromState:
          searchActionPayload.loadPageParametersFromState,
      })
    );
  }

  onGenerateTransactionReport(event: any) {
    this.store.dispatch(
      AccountTransactionsActions.fetchAccountTransactionsReport({
        fullIdentifier: this.fullIdentifier,
      })
    );
  }

  advancedSearch(event: any) {
    event.preventDefault();
    this.router.navigate(['/transaction-list'], {
      queryParams: { mode: SearchMode.INITIAL_LOAD },
      state: {
        transferringAccountNumber: this.fullIdentifier,
        acquiringAccountNumber: this.fullIdentifier,
      },
    });
  }

  getTransactionRuleValue(rule: boolean | null) {
    return getRuleLabel(rule);
  }
}
