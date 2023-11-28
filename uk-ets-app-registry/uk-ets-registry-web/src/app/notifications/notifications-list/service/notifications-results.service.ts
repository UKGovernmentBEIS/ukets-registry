import { Inject, Injectable } from '@angular/core';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';
import { HttpClient } from '@angular/common/http';
import { PageParameters } from '@shared/search/paginator';
import { SortParameters } from '@shared/search/sort/SortParameters';
import { Observable } from 'rxjs';
import { PagedResults, search } from '@shared/search/util/search-service.util';
import {
  NotificationProjection,
  NotificationSearchCriteria,
} from '@notifications/notifications-list/model/notification-projection.model';

@Injectable()
export class NotificationsResultsService {
  notificationSearchApi: string;

  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    ukEtsRegistryApiBaseUrl: string,
    private http: HttpClient
  ) {
    this.notificationSearchApi = `${ukEtsRegistryApiBaseUrl}/notifications.list`;
  }

  public search(
    criteria: NotificationSearchCriteria,
    pageParams: PageParameters,
    sortParams: SortParameters
  ): Observable<PagedResults<NotificationProjection>> {
    return search<NotificationSearchCriteria, NotificationProjection>({
      pageParams,
      sortParams,
      api: this.notificationSearchApi,
      criteria,
      http: this.http,
    });
  }
}
