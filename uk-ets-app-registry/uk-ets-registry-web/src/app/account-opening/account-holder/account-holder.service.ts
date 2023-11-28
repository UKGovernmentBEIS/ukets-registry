import { Inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';
import { AccountHolder } from '@shared/model/account';

export class FetchOneRequestRepresentation {
  identifier: number;
}

@Injectable({
  providedIn: 'root',
})
export class AccountHolderService {
  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    private ukEtsRegistryApiBaseUrl: string,
    private httpClient: HttpClient
  ) {}

  fetchList(holderType: string) {
    return this.httpClient.get<AccountHolder[]>(
      `${this.ukEtsRegistryApiBaseUrl}/account-holder.get.list`,
      { params: { holderType } }
    );
  }

  fetchOne(request: FetchOneRequestRepresentation | number) {
    const holderId =
      typeof request !== 'number'
        ? (request as FetchOneRequestRepresentation).identifier
        : request;
    const options = {
      params: new HttpParams().set('identifier', holderId.toString()),
    };
    return this.httpClient.get<AccountHolder>(
      `${this.ukEtsRegistryApiBaseUrl}/account-holder.get`,
      options
    );
  }
}
