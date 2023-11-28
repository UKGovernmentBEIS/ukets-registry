import { Inject, Injectable } from '@angular/core';
import { PageParameters } from '@registry-web/shared/search/paginator';
import { SortParameters } from '@registry-web/shared/search/sort/SortParameters';
import {
  PagedResults,
  search,
} from '@registry-web/shared/search/util/search-service.util';
import { Observable } from 'rxjs';
import {
  MessageSearchCriteria,
  MessageSearchResult,
  MessageDetails,
  SendMessageResponse,
} from '@kp-administration/itl-messages/model';
import { HttpClient, HttpParams } from '@angular/common/http';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';

@Injectable({
  providedIn: 'root',
})
export class MessageApiService {
  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    private ukEtsRegistryApiBaseUrl: string,
    private http: HttpClient
  ) {}

  public search(
    criteria: MessageSearchCriteria,
    pageParams: PageParameters,
    sortParams: SortParameters
  ): Observable<PagedResults<MessageSearchResult>> {
    return search<MessageSearchCriteria, MessageSearchResult>({
      pageParams,
      sortParams,
      api: `${this.ukEtsRegistryApiBaseUrl}/itl.messages.list`,
      criteria,
      http: this.http,
    });
  }

  fetchITLMessage(messageId: string): Observable<MessageDetails> {
    const options = {
      params: new HttpParams().set('messageId', messageId),
    };
    return this.http.get<MessageDetails>(
      `${this.ukEtsRegistryApiBaseUrl}/itl.messages.get`,
      options
    );
  }

  public sendMessage(content: string): Observable<SendMessageResponse> {
    return this.http.post<SendMessageResponse>(
      `${this.ukEtsRegistryApiBaseUrl}/itl.messages.send`,
      {
        content,
      }
    );
  }
}
