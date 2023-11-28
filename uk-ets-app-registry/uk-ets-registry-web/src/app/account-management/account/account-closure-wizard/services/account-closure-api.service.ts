import { Inject, Injectable } from '@angular/core';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';
import { HttpClient, HttpParams } from '@angular/common/http';
import { AccountDetails } from '@shared/model/account';
import { AllocationStatus } from '@account-management/account-list/account-list.model';

@Injectable({ providedIn: 'root' })
export class AccountClosureApiService {
  closeAccount: string;

  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    private ukEtsRegistryApiBaseUrl: string,
    private httpClient: HttpClient
  ) {
    this.closeAccount = `${ukEtsRegistryApiBaseUrl}/accounts.close`;
  }

  closureRequest(
    fullIdentifier: string,
    accountDetails: AccountDetails,
    closureComment: string,
    allocationClassification: AllocationStatus,
    noActiveAR: boolean
  ) {
    const param = {
      params: new HttpParams().set('fullIdentifier', fullIdentifier),
    };
    return this.httpClient.post<string>(
      `${this.closeAccount}`,
      {
        accountDetails: accountDetails,
        closureComment: closureComment,
        allocationClassification: allocationClassification,
        noActiveAR: noActiveAR,
      },
      param
    );
  }
}
