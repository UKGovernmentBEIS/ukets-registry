import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { SortParameters } from '@shared/search/sort/SortParameters';
import { PageParameters, Pagination } from '@shared/search/paginator';
import { Option } from '@shared/form-controls/uk-select-input/uk-select.model';
import { ActivatedRoute, Data } from '@angular/router';
import { ErrorDetail } from '@shared/error-summary';
import * as AccountTransactionsActions from './account-transactions.actions';
import { SearchActionPayload } from '@account-management/account/account-details/transactions/transaction-list.model';
import { Transaction } from '@shared/model/transaction';
import { Configuration } from '@shared/configuration/configuration.interface';
import { getConfigurationValue } from '@shared/shared.util';

@Component({
  selector: 'app-transactions-list',
  templateUrl: './transactions-list.component.html',
})
export class TransactionsListComponent implements OnInit {
  @Input()
  results: Transaction[];
  @Input()
  pagination: Pagination;
  @Input()
  configuration: Configuration[];
  @Input()
  isKyotoAccountType: boolean;
  @Output() readonly refreshResults = new EventEmitter<SearchActionPayload>();
  @Output() readonly generateTransactionReport = new EventEmitter<void>();

  showRunningBalances = false;
  pageSize = 10;
  sortParameters: SortParameters = {
    sortField: 'lastUpdated',
    sortDirection: 'DESC',
  };
  pageSizeOptions: Option[] = [
    { label: '10', value: 10 },
    { label: '50', value: 50 },
  ];
  potentialErrors: Map<any, ErrorDetail>;

  constructor(private route: ActivatedRoute) {}

  ngOnInit() {
    this.route.data.subscribe((data: Data) => {
      this.potentialErrors = data['errorMap'];
    });

    if (
      this.isKyotoAccountType &&
      JSON.parse(
        getConfigurationValue(
          'registry.transaction.list.showRunningBalancesKp',
          this.configuration
        )
      )
    ) {
      this.showRunningBalances = true;
    } else if (
      !this.isKyotoAccountType &&
      JSON.parse(
        getConfigurationValue(
          'registry.transaction.list.showRunningBalancesEts',
          this.configuration
        )
      )
    ) {
      this.showRunningBalances = true;
    } else {
      this.showRunningBalances = false;
    }
  }

  onSort(sortParameters: SortParameters) {
    this.sortParameters = sortParameters;
    this.searchTransactions(
      AccountTransactionsActions.sortResults,
      {
        page: 0,
        pageSize: this.pageSize,
      },
      true
    );
  }

  goToFirstPageOfResults(pageParameters: PageParameters) {
    this.searchTransactions(
      AccountTransactionsActions.navigateToFirstPageOfResults,
      pageParameters
    );
  }

  goToLastPageOfResults(pageParameters: PageParameters) {
    this.searchTransactions(
      AccountTransactionsActions.navigateToLastPageOfResults,
      pageParameters
    );
  }

  goToNextPageOfResults(pageParameters: PageParameters) {
    this.searchTransactions(
      AccountTransactionsActions.navigateToNextPageOfResults,
      pageParameters
    );
  }

  goToPreviousPageOfResults(pageParameters: PageParameters) {
    this.searchTransactions(
      AccountTransactionsActions.navigateToPreviousPageOfResults,
      pageParameters
    );
  }

  onChangePageSize(pageParameters: PageParameters) {
    this.searchTransactions(
      AccountTransactionsActions.changePageSize,
      pageParameters
    );
  }

  searchTransactions(
    action,
    pageParameters: PageParameters,
    loadPageParametersFromState = false
  ) {
    const payload = {
      pageParameters,
      sortParameters: this.sortParameters,
    };
    this.refreshResults.emit({ ...payload, loadPageParametersFromState });
  }

  generateCurrentReport() {
    this.generateTransactionReport.emit();
  }
}
