import { Inject, Injectable } from '@angular/core';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';
import { HttpClient, HttpParams } from '@angular/common/http';
import {
  BusinessCheckResult,
  ReturnExcessAllocationTransactionSummary,
  SignedReturnExcessAllocationTransactionSummary,
  SignedTransactionSummary,
  Transaction,
  TransactionSearchCriteria,
  TransactionSummary,
  TransactionType,
} from '@shared/model/transaction';
import { Observable } from 'rxjs';
import { FiltersDescriptor } from '@transaction-management/transaction-list/transaction-list.model';
import { PageParameters } from '@shared/search/paginator';
import { SortParameters } from '@shared/search/sort/SortParameters';
import { PagedResults, search } from '@shared/search/util/search-service.util';
import { DomainEvent } from '@shared/model/event';
import { TransactionDetails } from '@transaction-management/model';
import { Notice } from '@kp-administration/itl-notices/model';

/**
 * api calls to transaction controller
 */
@Injectable({
  providedIn: 'root',
})
export class TransactionApiService {
  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    private ukEtsRegistryApiBaseUrl: string,
    private httpClient: HttpClient
  ) {}

  validate(
    transactionSummary: TransactionSummary,
    businessCheckGroup?: string
  ): Observable<BusinessCheckResult> {
    return this.httpClient.post<BusinessCheckResult>(
      `${this.ukEtsRegistryApiBaseUrl}/transactions.validate`,
      transactionSummary,
      {
        params: businessCheckGroup
          ? {
              group: businessCheckGroup,
            }
          : {},
      }
    );
  }

  validateNatAndNer(
    // transactionSummary: TransactionSummary,
    returnExcessAllocationTransactionSummary: ReturnExcessAllocationTransactionSummary,
    businessCheckGroup?: string
  ): Observable<BusinessCheckResult> {
    return this.httpClient.post<BusinessCheckResult>(
      `${this.ukEtsRegistryApiBaseUrl}/transactions.excess-allocation-nat-and-ner.validate`,
      // {
      //   transactionSummary,
      returnExcessAllocationTransactionSummary,
      // },
      {
        params: businessCheckGroup
          ? {
              group: businessCheckGroup,
            }
          : {},
      }
    );
  }

  propose(
    signedTransactionSummary: SignedTransactionSummary
  ): Observable<BusinessCheckResult> {
    return this.httpClient.post<BusinessCheckResult>(
      `${this.ukEtsRegistryApiBaseUrl}/transactions.propose`,
      signedTransactionSummary
    );
  }

  proposeReturnExcessAllocation(
    signedTransactionSummary: SignedReturnExcessAllocationTransactionSummary
  ): Observable<BusinessCheckResult> {
    return this.httpClient.post<BusinessCheckResult>(
      `${this.ukEtsRegistryApiBaseUrl}/transactions.excess-allocation-nat-and-ner.propose`,
      signedTransactionSummary
    );
  }

  validateITLNotificationId(
    itlNotificationId: number,
    transactionType: TransactionType
  ): Observable<Notice> {
    let params = new HttpParams();
    if (itlNotificationId) {
      params = params.append(
        'notificationIdentifier',
        itlNotificationId.toString()
      );
    }
    params = params.append('transactionType', transactionType);
    return this.httpClient.get<Notice>(
      `${this.ukEtsRegistryApiBaseUrl}/itl.notices.validate`,
      {
        params,
      }
    );
  }

  proposeAllowance(
    signedTransactionSummary: SignedTransactionSummary
  ): Observable<BusinessCheckResult> {
    return this.httpClient.post<BusinessCheckResult>(
      `${this.ukEtsRegistryApiBaseUrl}/transactions.propose-allowances`,
      signedTransactionSummary
    );
  }

  fetchOneTransaction(
    transactionIdentifier: string
  ): Observable<TransactionDetails> {
    const options = {
      params: new HttpParams().set(
        'transactionIdentifier',
        transactionIdentifier
      ),
    };
    return this.httpClient.get<TransactionDetails>(
      `${this.ukEtsRegistryApiBaseUrl}/transactions.get`,
      options
    );
  }

  transactionEvents(transactionIdentifier: string): Observable<DomainEvent[]> {
    const options = {
      params: new HttpParams().set(
        'transactionIdentifier',
        transactionIdentifier
      ),
    };
    return this.httpClient.get<DomainEvent[]>(
      `${this.ukEtsRegistryApiBaseUrl}/transactions.get.history`,
      options
    );
  }

  manuallyCancel(
    transactionIdentifier: string,
    comment: string
  ): Observable<TransactionDetails> {
    let params = new HttpParams();
    params = params.append('transactionIdentifier', transactionIdentifier);
    params = params.append('comment', comment);
    return this.httpClient.post<TransactionDetails>(
      `${this.ukEtsRegistryApiBaseUrl}/transactions.cancel`,
      params
    );
  }

  search(
    criteria: TransactionSearchCriteria,
    pageParams: PageParameters,
    sortParams: SortParameters,
    isReport?: boolean
  ): Observable<PagedResults<Transaction>> {
    return search<TransactionSearchCriteria, Transaction>({
      pageParams,
      sortParams,
      api: `${this.ukEtsRegistryApiBaseUrl}/transactions.list`,
      criteria,
      http: this.httpClient,
      isReport,
    });
  }

  fetchTransactionDetailsReport(
    transactionIdentifier: string
  ): Observable<number> {
    const options = {
      params: new HttpParams().set(
        'transactionIdentifier',
        transactionIdentifier
      ),
    };
    return this.httpClient.get<number>(
      `${this.ukEtsRegistryApiBaseUrl}/transactions.generate.details.report`,
      options
    );
  }

  getFiltersPermissions(): Observable<FiltersDescriptor> {
    return this.httpClient.get<FiltersDescriptor>(
      `${this.ukEtsRegistryApiBaseUrl}/transactions.list.filters`
    );
  }
}
