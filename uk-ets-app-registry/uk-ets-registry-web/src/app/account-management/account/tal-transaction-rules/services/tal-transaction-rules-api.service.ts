import { Inject, Injectable } from '@angular/core';
import { UK_ETS_REGISTRY_API_BASE_URL } from '../../../../app.tokens';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TrustedAccountListRules } from '@shared/model/account';

@Injectable()
export class TalTransactionRulesApiService {
  getTalTransactionRulesApi: string;
  updateTalTransactionRulesApi: string;

  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    ukEtsRegistryApiBaseUrl: string,
    private http: HttpClient
  ) {
    this.getTalTransactionRulesApi = `${ukEtsRegistryApiBaseUrl}/tal-transaction-rules.get`;
    this.updateTalTransactionRulesApi = `${ukEtsRegistryApiBaseUrl}/tal-transaction-rules.update`;
  }

  getCurrentTalTransactionRules(
    accountId: string
  ): Observable<TrustedAccountListRules> {
    let params = new HttpParams();
    params = params.set('accountId', accountId);
    return this.http.get<TrustedAccountListRules>(
      `${this.getTalTransactionRulesApi}`,
      { params }
    );
  }

  updateCurrentTalTransactionRules(
    accountId: string,
    trustedAccountListRules: TrustedAccountListRules
  ): Observable<string> {
    let params = new HttpParams();
    params = params.set('accountId', accountId);
    return this.http.post<string>(
      this.updateTalTransactionRulesApi,
      trustedAccountListRules,
      {
        params
      }
    );
  }
}
