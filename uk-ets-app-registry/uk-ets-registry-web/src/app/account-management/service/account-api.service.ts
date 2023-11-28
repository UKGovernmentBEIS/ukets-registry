import { Inject, Injectable } from '@angular/core';
import { UK_ETS_REGISTRY_API_BASE_URL } from 'src/app/app.tokens';
import {
  HttpClient,
  HttpHeaders,
  HttpParams,
  HttpResponse,
} from '@angular/common/http';
import { PageParameters } from '@shared/search/paginator';
import { SortParameters } from '@shared/search/sort/SortParameters';
import { Observable } from 'rxjs';
import { PagedResults, search } from '@shared/search/util/search-service.util';
import {
  AccountSearchCriteria,
  AccountSearchResult,
  FiltersDescriptor,
} from '../account-list/account-list.model';
import { Account } from '@shared/model/account/account';
import {
  AccountDetails,
  AccountHoldingsResult,
  OperatorType,
  TALSearchCriteria,
  TrustedAccount,
} from '@shared/model/account';
import { DomainEvent } from '@shared/model/event';
import { Transaction } from '@shared/model/transaction';
import { FileDetails } from '@shared/model/file/file-details.model';
import { RequestType, TaskSearchCriteria } from '@task-management/model';
import { TaskService } from '@shared/services/task-service';

@Injectable({ providedIn: 'root' })
export class AccountApiService {
  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    private ukEtsRegistryApiBaseUrl: string,
    private http: HttpClient,
    private taskService: TaskService
  ) {}

  public search(
    criteria: AccountSearchCriteria,
    pageParams: PageParameters,
    sortParams: SortParameters,
    isReport?: boolean
  ): Observable<PagedResults<AccountSearchResult>> {
    return search<AccountSearchCriteria, AccountSearchResult>({
      pageParams,
      sortParams,
      api: `${this.ukEtsRegistryApiBaseUrl}/accounts.list`,
      criteria,
      http: this.http,
      isReport,
    });
  }

  public searchTAL(
    criteria: TALSearchCriteria,
    pageParams: PageParameters,
    sortParams: SortParameters
  ): Observable<PagedResults<TrustedAccount>> {
    return search<TALSearchCriteria, TrustedAccount>({
      pageParams,
      sortParams,
      api: `${this.ukEtsRegistryApiBaseUrl}/tal.list`,
      criteria,
      http: this.http,
    });
  }

  public updateDetails(
    identifier: number,
    accountDetails: AccountDetails
  ): Observable<Account> {
    const options = {
      params: new HttpParams().set('identifier', identifier.toString()),
    };
    return this.http.post<Account>(
      `${this.ukEtsRegistryApiBaseUrl}/accounts.update.details`,
      accountDetails,
      options
    );
  }

  public excludeBilling(
    identifier: number,
    exclusionRemarks: string
  ): Observable<any> {
    const options = {
      params: new HttpParams().set('identifier', identifier.toString()),
    };
    return this.http.post<Account>(
      `${this.ukEtsRegistryApiBaseUrl}/accounts.exclude.from.billing`,
      { exclusionRemarks },
      options
    );
  }

  public includeBilling(identifier: number): Observable<any> {
    const options = {
      params: new HttpParams().set('identifier', identifier.toString()),
    };
    return this.http.post<Account>(
      `${this.ukEtsRegistryApiBaseUrl}/accounts.include.in.billing`,
      {},
      options
    );
  }

  public getFiltersPermissions(): Observable<FiltersDescriptor> {
    return this.http.get<FiltersDescriptor>(
      `${this.ukEtsRegistryApiBaseUrl}/accounts.list.filters`
    );
  }

  public fetchAccount(accountId: string): Observable<Account> {
    const param = { params: new HttpParams().set('accountId', accountId) };
    return this.http.get<Account>(
      `${this.ukEtsRegistryApiBaseUrl}/accounts.get`,
      param
    );
  }

  public fetchAccountHoldings(
    accountId: string
  ): Observable<AccountHoldingsResult> {
    const options = { params: new HttpParams().set('identifier', accountId) };
    return this.http.get<AccountHoldingsResult>(
      `${this.ukEtsRegistryApiBaseUrl}/accounts.get.holdings`,
      options
    );
  }

  accountHistory(identifier: string): Observable<DomainEvent[]> {
    const params = new HttpParams().set('identifier', identifier);
    return this.http.get<DomainEvent[]>(
      `${this.ukEtsRegistryApiBaseUrl}/accounts.get.history`,
      {
        params,
      }
    );
  }

  getAccountHolderFiles(accountIdentifier: string): Observable<FileDetails[]> {
    const options = {
      params: new HttpParams().set('accountIdentifier', accountIdentifier),
    };
    return this.http.get<FileDetails[]>(
      `${this.ukEtsRegistryApiBaseUrl}/account-holder.get.files`,
      options
    );
  }

  getAccountHolderFile(fileId: number): Observable<HttpResponse<Blob>> {
    const headers = new HttpHeaders({
      responseType: 'blob',
    });
    let params = new HttpParams();
    params = params.append('fileId', fileId.toString());
    return this.http.get(
      `${this.ukEtsRegistryApiBaseUrl}/account-holder.get.file`,
      {
        headers,
        observe: 'response',
        responseType: 'blob',
        params,
      }
    );
  }

  public fetchAccountTransactions(
    identifier: string,
    pageParams: PageParameters,
    sortParameters: SortParameters
  ): Observable<PagedResults<Transaction>> {
    let params = new HttpParams().set('accountFullIdentifier', identifier);
    params = params.set('page', String(pageParams.page));
    params = pageParams.pageSize
      ? params.set('pageSize', String(pageParams.pageSize))
      : params;
    params = sortParameters.sortField
      ? params.set('sortField', sortParameters.sortField)
      : params;
    params = sortParameters.sortDirection
      ? params.set('sortDirection', sortParameters.sortDirection)
      : params;

    return this.http.get<PagedResults<Transaction>>(
      `${this.ukEtsRegistryApiBaseUrl}/accounts.get.transactions`,
      {
        params,
      }
    );
  }

  public fetchAccountTransactionsReport(
    identifier: string
  ): Observable<number> {
    const params = new HttpParams().set('accountFullIdentifier', identifier);

    return this.http.get<number>(
      `${this.ukEtsRegistryApiBaseUrl}/accounts.get.transactions.report`,
      {
        params,
      }
    );
  }

  fetchOpenOperatorTasks(account: Account) {
    let requestType = null;
    if (account.operator?.type == OperatorType.AIRCRAFT_OPERATOR) {
      requestType = RequestType.AIRCRAFT_OPERATOR_UPDATE_REQUEST;
    } else if (account.operator?.type == OperatorType.INSTALLATION) {
      requestType = RequestType.INSTALLATION_OPERATOR_UPDATE_REQUEST;
    }
    const criteria = {
      accountNumber: account.identifier.toString(),
      taskStatus: 'OPEN',
      taskType: requestType,
    } as TaskSearchCriteria;
    const pageParams = {
      page: 0,
      pageSize: 1,
    };
    const sortParams = {
      sortDirection: 'DESC',
      sortField: 'createdOn',
    };
    return this.taskService.search(criteria, pageParams, sortParams);
  }
}
