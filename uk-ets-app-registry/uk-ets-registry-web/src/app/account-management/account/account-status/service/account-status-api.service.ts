import { Injectable, Inject } from '@angular/core';
import { UK_ETS_REGISTRY_API_BASE_URL } from 'src/app/app.tokens';
import { HttpClient, HttpParams } from '@angular/common/http';
import {
  AccountStatusActionOption,
  AccountStatusRequest
} from '@shared/model/account/account-status-action';
import { Observable } from 'rxjs';
import { AccountStatus } from '@shared/model/account';

@Injectable({
  providedIn: 'root'
})
export class AccountStatusApiService {
  sampleAllowedAccountStatusActions: string;

  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    private ukEtsRegistryApiBaseUrl: string,
    private http: HttpClient
  ) {
    this.sampleAllowedAccountStatusActions =
      'assets/json/account-status-actions.json';
  }

  getAllowedAccountStatusActions(
    accountId: string
  ): Observable<AccountStatusActionOption[]> {
    const param = {
      params: new HttpParams().set('accountId', accountId)
    };
    return this.http.get<AccountStatusActionOption[]>(
      // this.sampleAllowedAccountStatusActions,
      `${this.ukEtsRegistryApiBaseUrl}/accounts.get.statuses`,
      param
    );
  }

  changeAccountStatus(
    request: Required<AccountStatusRequest>
  ): Observable<AccountStatus> {
    const param = {
      params: new HttpParams().set('accountId', request.accountId)
    };
    return this.http.patch<AccountStatus>(
      `${this.ukEtsRegistryApiBaseUrl}/accounts.update`,
      {
        accountStatus: request.status,
        comment: request.comment
      },
      param
    );
  }
}
