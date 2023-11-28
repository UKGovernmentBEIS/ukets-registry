import { Inject, Injectable } from '@angular/core';
import { UK_ETS_REGISTRY_API_BASE_URL } from '../../app.tokens';
import { HttpClient } from '@angular/common/http';
import { PageParameters } from '@shared/search/paginator';
import { SortParameters } from '@shared/search/sort/SortParameters';
import { Observable } from 'rxjs';
import { PagedResults, search } from '@shared/search/util/search-service.util';
import {
  UserProjection,
  UserSearchCriteria
} from '@user-management/user-list/user-list.model';

@Injectable({
  providedIn: 'root'
})
export class UserListService {
  usersSearchApi: string;

  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    ukEtsRegistryApiBaseUrl: string,
    private http: HttpClient
  ) {
    this.usersSearchApi = `${ukEtsRegistryApiBaseUrl}/admin/users.list`;
  }

  public search(
    criteria: UserSearchCriteria,
    pageParams: PageParameters,
    sortParams: SortParameters
  ): Observable<PagedResults<UserProjection>> {
    return search<UserSearchCriteria, UserProjection>({
      pageParams,
      sortParams,
      api: this.usersSearchApi,
      criteria,
      http: this.http
    });
  }
}
