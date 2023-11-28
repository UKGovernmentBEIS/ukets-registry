import { HttpClient, HttpParams } from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';
import { AccountHolder, AccountHolderContactInfo } from '@shared/model/account';
import { Observable } from 'rxjs';
import { AccountTransferRequest } from '@account-transfer/model';

@Injectable({
  providedIn: 'root',
})
export class AccountTransferService {
  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    private ukEtsRegistryApiBaseUrl: string,
    private httpClient: HttpClient
  ) {}

  public fetchAccountHolder(identifier: number): Observable<AccountHolder> {
    const options = {
      params: new HttpParams().set('identifier', identifier.toString()),
    };
    return this.httpClient.get<AccountHolder>(
      `${this.ukEtsRegistryApiBaseUrl}/account-holder.get`,
      options
    );
  }

  public fetchAccountHolderContacts(
    accountHolderId: number
  ): Observable<AccountHolderContactInfo> {
    const options = {
      params: new HttpParams().set('holderId', accountHolderId.toString()),
    };
    return this.httpClient.get<AccountHolderContactInfo>(
      `${this.ukEtsRegistryApiBaseUrl}/account-holder.get.contacts`,
      options
    );
  }

  submitRequest(request: AccountTransferRequest): Observable<string> {
    return this.httpClient.post<string>(
      `${this.ukEtsRegistryApiBaseUrl}/account-transfer.perform`,
      request
    );
  }
}
