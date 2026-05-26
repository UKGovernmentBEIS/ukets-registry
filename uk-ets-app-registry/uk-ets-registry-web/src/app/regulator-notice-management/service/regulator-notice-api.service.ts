import { Inject, Injectable } from '@angular/core';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class RegulatorNoticeApiService {
  regulatorNoticePendingTaskExistsApiUrl: string;

  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    private ukEtsRegistryApiBaseUrl: string,
    private httpClient: HttpClient
  ) {
    this.regulatorNoticePendingTaskExistsApiUrl = `${this.ukEtsRegistryApiBaseUrl}/regulator-notices.get.pendingTaskExists`;
  }

  fetchPendingRegulatorNoticeTaskExists(
    accountId: string
  ): Observable<boolean> {
    return this.httpClient.get<boolean>(
      this.regulatorNoticePendingTaskExistsApiUrl,
      {
        params: new HttpParams().set('accountId', accountId),
      }
    );
  }
}
