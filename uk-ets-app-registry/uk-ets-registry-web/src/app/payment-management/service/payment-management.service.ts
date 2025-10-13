import { HttpClient } from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';
import { PaymentSearchCriteria, PaymentSearchResult } from '../model';
import { PagedResults, search } from '@shared/search/util/search-service.util';
import { PageParameters } from '@shared/search/paginator';
import { SortParameters } from '@shared/search/sort/SortParameters';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class PaymentManagementService {
  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    private ukEtsRegistryApiBaseUrl: string,
    private httpClient: HttpClient
  ) {}
  search(
    criteria: PaymentSearchCriteria,
    pageParams: PageParameters,
    sortParams: SortParameters,
    isReport?: boolean
  ): Observable<PagedResults<PaymentSearchResult>> {
    return search<PaymentSearchCriteria, PaymentSearchResult>({
      pageParams,
      sortParams,
      api: `${this.ukEtsRegistryApiBaseUrl}/payments.list`,
      criteria,
      http: this.httpClient,
      isReport,
    });
  }
}
