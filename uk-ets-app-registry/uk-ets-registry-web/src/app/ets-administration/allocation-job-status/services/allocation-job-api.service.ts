import {
  HttpClient,
  HttpHeaders,
  HttpParams,
  HttpResponse,
} from '@angular/common/http';
import { Injectable, Inject } from '@angular/core';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';
import { Observable } from 'rxjs';
import { AllocationJob } from '../models/allocation-job.model';
import { AllocationJobSearchCriteria } from '../models/allocation-job-search-criteria.model';
import { PageParameters } from '@registry-web/shared/search/paginator';
import { SortParameters } from '@registry-web/shared/search/sort/SortParameters';
import {
  PagedResults,
  search,
} from '@registry-web/shared/search/util/search-service.util';

@Injectable({
  providedIn: 'root',
})
export class AllocationJobApiService {
  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    private ukEtsRegistryApiBaseUrl: string,
    private http: HttpClient
  ) {}

  private allocationJobsGetUrl = `${this.ukEtsRegistryApiBaseUrl}/allocations.list`;
  private allocationJobsDeleteUrl = `${this.ukEtsRegistryApiBaseUrl}/allocations.cancel.pending`;
  private allocationJobsReportUrl = `${this.ukEtsRegistryApiBaseUrl}/allocations.get.report.file`;

  search(
    criteria: AllocationJobSearchCriteria,
    pageParameters: PageParameters,
    sortParameters: SortParameters,
    isReport?: boolean
  ): Observable<PagedResults<AllocationJob>> {
    return search<AllocationJobSearchCriteria, AllocationJob>({
      pageParams: pageParameters,
      sortParams: sortParameters,
      api: `${this.allocationJobsGetUrl}`,
      criteria,
      http: this.http,
      isReport,
    });
  }

  cancelPendingAllocationById(jobId: number): Observable<void> {
    return this.http.delete<void>(`${this.allocationJobsDeleteUrl}/${jobId}`);
  }

  downloadAllocationReportById(jobId: number): Observable<HttpResponse<Blob>> {
    const headers = new HttpHeaders({
      responseType: 'blob',
    });
    let params = new HttpParams();
    params = params.set('jobId', jobId.toString());

    return this.http.get(`${this.allocationJobsReportUrl}`, {
      headers,
      observe: 'response',
      responseType: 'blob',
      params,
    });
  }
}
