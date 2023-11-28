import { Inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import {
  AccountHoldingDetails,
  AccountHoldingDetailsCriteria,
} from '@account-management/account/account-details/holdings/account-holding-details.model';
import { Observable } from 'rxjs';
import { fillSearchParams } from '@shared/search/util/search-service.util';
import { UK_ETS_REGISTRY_API_BASE_URL } from '../../../../app.tokens';
import { Store } from '@ngrx/store';

@Injectable({
  providedIn: 'root',
})
export class AccountHoldingDetailsService {
  accountHoldingDetailsApi: string;

  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL) ukEtsRegistryApiBaseUrl: string,
    private http: HttpClient,
    private store: Store
  ) {
    this.accountHoldingDetailsApi = `${ukEtsRegistryApiBaseUrl}/accounts.get.holding-details`;
  }

  public fetch(
    criteria: AccountHoldingDetailsCriteria
  ): Observable<AccountHoldingDetails> {
    const params = fillSearchParams(new HttpParams(), criteria);
    return this.http.get<AccountHoldingDetails>(this.accountHoldingDetailsApi, {
      params,
    });
  }
}
