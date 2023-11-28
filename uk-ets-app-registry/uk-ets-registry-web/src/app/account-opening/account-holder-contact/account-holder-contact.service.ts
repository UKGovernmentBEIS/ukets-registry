import { Inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { UK_ETS_REGISTRY_API_BASE_URL } from '../../app.tokens';
import { AccountHolderContactInfo } from '@shared/model/account/account-holder-contact';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AccountHolderContactService {
  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    private ukEtsRegistryApiBaseUrl: string,
    private httpClient: HttpClient
  ) {}

  fetchAccountHolderContacts(
    accountHolderId: string
  ): Observable<AccountHolderContactInfo> {
    const options = {
      params: new HttpParams().set('holderId', accountHolderId.toString()),
    };
    return this.httpClient.get<AccountHolderContactInfo>(
      `${this.ukEtsRegistryApiBaseUrl}/account-holder.get.contacts`,
      options
    );
  }
}
