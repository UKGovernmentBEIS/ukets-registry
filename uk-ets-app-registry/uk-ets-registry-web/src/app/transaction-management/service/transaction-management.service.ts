import { Injectable } from '@angular/core';
import { PageParameters } from '@shared/search/paginator';
import { SortParameters } from '@shared/search/sort/SortParameters';
import { Observable } from 'rxjs';
import { PagedResults } from '@shared/search/util/search-service.util';
import { FiltersDescriptor } from '../transaction-list/transaction-list.model';
import { TransactionDetails } from '../model';
import { DomainEvent } from '@shared/model/event';
import {
  Transaction,
  TransactionSearchCriteria,
} from '@shared/model/transaction';
import { TransactionApiService } from '@shared/services';

@Injectable()
export class TransactionManagementService {
  constructor(private transactionApiService: TransactionApiService) {}

  public search(
    criteria: TransactionSearchCriteria,
    pageParams: PageParameters,
    sortParams: SortParameters,
    isReport?: boolean
  ): Observable<PagedResults<Transaction>> {
    return this.transactionApiService.search(
      criteria,
      pageParams,
      sortParams,
      isReport
    );
  }

  public getFiltersPermissions(): Observable<FiltersDescriptor> {
    return this.transactionApiService.getFiltersPermissions();
  }

  public fetchOneTransaction(
    transactionIdentifier: string
  ): Observable<TransactionDetails> {
    return this.transactionApiService.fetchOneTransaction(
      transactionIdentifier
    );
  }

  public fetchTransactionDetailsReport(
    transactionIdentifier: string
  ): Observable<number> {
    return this.transactionApiService.fetchTransactionDetailsReport(
      transactionIdentifier
    );
  }

  public manuallyCancel(
    transactionIdentifier: string,
    comment: string
  ): Observable<TransactionDetails> {
    return this.transactionApiService.manuallyCancel(
      transactionIdentifier,
      comment
    );
  }

  public transactionEvents(
    transactionIdentifier: string
  ): Observable<DomainEvent[]> {
    return this.transactionApiService.transactionEvents(transactionIdentifier);
  }
}
