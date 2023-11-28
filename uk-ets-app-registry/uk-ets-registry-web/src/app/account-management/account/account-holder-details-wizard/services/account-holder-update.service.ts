import { Inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UK_ETS_REGISTRY_API_BASE_URL } from '../../../../app.tokens';
import {
  AccountHolderDetailsType,
  AccountHolderUpdate,
} from '@account-management/account/account-holder-details-wizard/model';

@Injectable()
export class AccountHolderUpdateService {
  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    private ukEtsRegistryApiBaseUrl: string,
    private httpClient: HttpClient
  ) {}

  submitRequest(accountHolderUpdate: AccountHolderUpdate): Observable<string> {
    let params = new HttpParams();
    params = params.set(
      'accountIdentifier',
      accountHolderUpdate.accountIdentifier
    );
    params = params.set(
      'accountHolderIdentifier',
      String(accountHolderUpdate.accountHolderIdentifier)
    );
    let path = '';
    if (
      accountHolderUpdate.updateType ===
      AccountHolderDetailsType.ACCOUNT_HOLDER_UPDATE_DETAILS
    ) {
      path = 'account-holder.update-details';
    } else if (
      accountHolderUpdate.updateType ===
      AccountHolderDetailsType.ACCOUNT_HOLDER_PRIMARY_CONTACT_DETAILS
    ) {
      path = 'account-holder.update-primary-contact';
    } else if (
      accountHolderUpdate.updateType ===
        AccountHolderDetailsType.ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_UPDATE ||
      accountHolderUpdate.updateType ===
        AccountHolderDetailsType.ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_DELETE ||
      accountHolderUpdate.updateType ===
        AccountHolderDetailsType.ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_ADD
    ) {
      path = 'account-holder.update-alternative-primary-contact';
    }
    return this.httpClient.post<string>(
      `${this.ukEtsRegistryApiBaseUrl}/${path}`,
      accountHolderUpdate,
      { params }
    );
  }
}
