import { Inject, Injectable } from '@angular/core';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PagedResults, search } from '@shared/search/util/search-service.util';
import { Notice } from '@kp-administration/itl-notices/model/notice';
import { PageParameters } from '@shared/search/paginator';
import { SortParameters } from '@shared/search/sort/SortParameters';
import { map } from 'rxjs/operators';

@Injectable()
export class NoticeApiService {
  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    private readonly ukEtsRegistryApiBaseUrl: string,
    private readonly http: HttpClient
  ) {}

  getNotices(
    pageParams: PageParameters,
    sortParams: SortParameters = {
      sortDirection: 'ASC',
      sortField: 'notificationIdentifier'
    }
  ): Observable<PagedResults<Notice>> {
    return search<any, Notice>({
      api: `${this.ukEtsRegistryApiBaseUrl}/itl.notices.list`,
      criteria: null,
      http: this.http,
      pageParams,
      sortParams
    });
  }

  getNoticeByIdentifier(noticeIdentifier: string): Observable<Notice[]> {
    return this.http
      .get<Notice[]>(`${this.ukEtsRegistryApiBaseUrl}/itl.notices.get`, {
        params: new HttpParams().set('notificationIdentity', noticeIdentifier)
      })
      .pipe(
        map(notice =>
          notice.sort(
            (a, b) =>
              new Date(b.createdDate).getTime() -
              new Date(a.createdDate).getTime()
          )
        )
      );
  }
}
