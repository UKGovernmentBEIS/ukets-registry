import { Inject, Injectable } from '@angular/core';
import { UK_ETS_REGISTRY_API_BASE_URL } from '../../../../app.tokens';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TrustedAccount } from '@shared/model/account';
import { TrustedAccountListApiRequestType } from './trusted-account-list-api-request-type.enum';
import { ValidateAccount } from '@shared/model/account/validate-account';

@Injectable()
export class TrustedAccountListApiService {
  getTrustedAccountsApi: string;
  addTrustedAccountsApi: string;
  updateTrustedAccountsApi: string;
  removeTrustedAccountsApi: string;
  validateAccountApi: string;
  cancelPendingActivationApi: string;

  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    ukEtsRegistryApiBaseUrl: string,
    private http: HttpClient
  ) {
    this.getTrustedAccountsApi = `${ukEtsRegistryApiBaseUrl}/tal.get`;
    this.addTrustedAccountsApi = `${ukEtsRegistryApiBaseUrl}/tal.add`;
    this.updateTrustedAccountsApi = `${ukEtsRegistryApiBaseUrl}/tal.update`;
    this.removeTrustedAccountsApi = `${ukEtsRegistryApiBaseUrl}/tal.remove`;
    this.validateAccountApi = `${ukEtsRegistryApiBaseUrl}/accounts.validate`;
    this.cancelPendingActivationApi = `${ukEtsRegistryApiBaseUrl}/tal.cancel.pending`;
  }

  public getApprovedOrActivatedTrustedAccounts(
    accountId: string
  ): Observable<TrustedAccount[]> {
    let params = new HttpParams();
    params = params.set('accountId', accountId);
    params = params.set(
      'type',
      TrustedAccountListApiRequestType.ELIGIBLE_FOR_REMOVAL
    );
    return this.http.get<TrustedAccount[]>(this.getTrustedAccountsApi, {
      params,
    });
  }

  validate(accountFullIdentifier: string): Observable<ValidateAccount> {
    let params = new HttpParams();
    params = params.set('accountFullIdentifier', accountFullIdentifier);
    return this.http.post<any>(`${this.validateAccountApi}`, params);
  }

  submitToAddTrustedAccount(
    trustedAccount: TrustedAccount,
    accountId: string
  ): Observable<string> {
    let params = new HttpParams();
    params = params.set('accountId', accountId);
    return this.http.post<string>(this.addTrustedAccountsApi, trustedAccount, {
      params,
    });
  }

  submitToUpdateDescriptionTrustedAccount(
    trustedAccountUpdated: {
      description: string;
      accountFullIdentifier: string;
    },
    accountId: string
  ): Observable<TrustedAccount> {
    let params = new HttpParams();
    params = params.set('accountId', accountId);
    return this.http.post<TrustedAccount>(
      this.updateTrustedAccountsApi,
      trustedAccountUpdated,
      { params }
    );
  }

  submitToRemoveTrustedAccount(
    trustedAccounts: TrustedAccount[],
    accountId: string
  ): Observable<string> {
    let params = new HttpParams();
    params = params.set('accountId', accountId);
    return this.http.post<string>(
      this.removeTrustedAccountsApi,
      trustedAccounts,
      { params }
    );
  }

  cancelPendingActivationTrustedAccount(
    accountIdentifier: string,
    trustedAccountFullIdentifier: string
  ): Observable<string> {
    let params = new HttpParams();
    params = params.set('accountIdentifier', accountIdentifier);
    params = params.set(
      'trustedAccountFullIdentifier',
      trustedAccountFullIdentifier
    );
    return this.http.post<string>(
      this.cancelPendingActivationApi,
      {},
      { params }
    );
  }
}
