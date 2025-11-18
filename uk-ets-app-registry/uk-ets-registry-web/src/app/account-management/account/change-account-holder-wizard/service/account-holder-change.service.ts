import { HttpClient, HttpParams } from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';
import { Observable, of } from 'rxjs';
import { ChangeAccountHolderRequest } from '@change-account-holder-wizard/model';

@Injectable({
  providedIn: 'root',
})
export class AccountHolderChangeService {
  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    private ukEtsRegistryApiBaseUrl: string,
    private httpClient: HttpClient
  ) {}

  submitChangeAccountHolderRequest(
    request: ChangeAccountHolderRequest
  ): Observable<string> {
    let params = new HttpParams();
    params = params.set('accountIdentifier', request.accountIdentifier);

    return this.httpClient.post<string>(
      `${this.ukEtsRegistryApiBaseUrl}/change-account-holder.perform`,
      request,
      { params }
    );
  }

  getAccountHolderOrphan(
    accountHolderIdentifier: number,
    accountIdentifier: number | string
  ): Observable<boolean> {
    return this.httpClient.get<boolean>(
      `${this.ukEtsRegistryApiBaseUrl}/account-holder.orphan`,
      { params: { accountHolderIdentifier, accountIdentifier } }
    );
  }
}
