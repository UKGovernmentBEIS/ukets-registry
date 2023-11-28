import { PageParameters } from '@shared/search/paginator';
import { SortParameters } from '../sort/SortParameters';
import { Observable } from 'rxjs';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';

export interface PagedResults<T> {
  totalResults: number;
  items: T[];
}

export interface SearchCommand<C> {
  criteria: C;
  pageParams: PageParameters;
  sortParams: SortParameters;
  http: HttpClient;
  api: string;
  isReport?: boolean; // set if the search is for report generation
}

export function search<C, R>(
  command: SearchCommand<C>
): Observable<PagedResults<R>> {
  let params = new HttpParams();
  params = fillSearchParams(params, command.pageParams);
  params = fillSearchParams(params, command.criteria);
  params = fillSearchParams(params, command.sortParams);

  let headers = new HttpHeaders();
  if (command.isReport) {
    headers = new HttpHeaders({
      'Is-Report': 'true',
    });
  }

  return command.http.get<PagedResults<R>>(command.api, { headers, params });
}

export function fillSearchParams(params: HttpParams, filter: any): HttpParams {
  for (const property in filter) {
    if (
      filter[property] !== undefined &&
      filter[property] !== null &&
      filter[property] !== ''
    ) {
      params = params.set(property, filter[property]);
    }
  }
  return params;
}
