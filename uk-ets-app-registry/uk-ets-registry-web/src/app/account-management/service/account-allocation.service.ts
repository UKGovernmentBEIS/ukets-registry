import { Inject, Injectable } from '@angular/core';
import {
  AccountAllocationStatus,
  UpdateAllocationStatusRequest,
} from '@allocation-status/model';
import { HttpClient, HttpParams } from '@angular/common/http';
import { UK_ETS_REGISTRY_API_BASE_URL } from '../../app.tokens';
import { Observable } from 'rxjs';
import { AccountAllocation } from '@shared/model/account';

@Injectable()
export class AccountAllocationService {
  fetchAllocationStatusApi: string;
  updateAllocationStatusApi: string;
  accountAllocationApi: string;
  fetchAllocationPendingTaskExistsApi: string;

  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    ukEtsRegistryApiBaseUrl: string,
    private http: HttpClient
  ) {
    this.accountAllocationApi = `${ukEtsRegistryApiBaseUrl}/allocations.get`;
    this.fetchAllocationStatusApi = `${ukEtsRegistryApiBaseUrl}/allocations.get.status`;
    this.fetchAllocationPendingTaskExistsApi = `${ukEtsRegistryApiBaseUrl}/allocations.get.pendingTaskExists`;
    this.updateAllocationStatusApi = `${ukEtsRegistryApiBaseUrl}/allocations.update.status`;
  }

  fetchAllocation(accountId: string): Observable<AccountAllocation> {
    return this.http.get<AccountAllocation>(this.accountAllocationApi, {
      params: new HttpParams().set('accountId', accountId),
    });
  }

  fetchAllocationStatus(
    accountId: string
  ): Observable<AccountAllocationStatus> {
    return this.http.get<AccountAllocationStatus>(
      this.fetchAllocationStatusApi,
      { params: new HttpParams().set('accountId', accountId) }
    );
  }

  fetchPendingAllocationTaskExists(accountId: string): Observable<boolean> {
    return this.http.get<boolean>(this.fetchAllocationPendingTaskExistsApi, {
      params: new HttpParams().set('accountId', accountId),
    });
  }

  updateAllocationStatus(
    accountId: string,
    updateReq: UpdateAllocationStatusRequest
  ): Observable<string> {
    return this.http.post<string>(this.updateAllocationStatusApi, updateReq, {
      params: new HttpParams().set('accountId', accountId),
    });
  }
}
