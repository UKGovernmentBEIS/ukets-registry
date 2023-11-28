import { Inject, Injectable } from '@angular/core';
import { UK_ETS_REGISTRY_API_BASE_URL } from '../../../app.tokens';
import {
  HttpClient,
  HttpHeaders,
  HttpParams,
  HttpResponse,
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { BusinessCheckResult } from '@shared/model/transaction';
import { map } from 'rxjs/operators';
import { AllocationCategory } from '@registry-web/shared/model/allocation';

@Injectable({
  providedIn: 'root',
})
export class RequestAllocationService {
  allocationApi: string;

  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    ukEtsRegistryApiBaseUrl: string,
    private http: HttpClient
  ) {
    this.allocationApi = `${ukEtsRegistryApiBaseUrl}`;
  }

  public getAllocationYears(): Observable<number[]> {
    return this.http.get<number[]>(
      `${this.allocationApi}/allocations.get.available-years`
    );
  }

  public submitRequest(
    allocationYear: number,
    allocationCategory: AllocationCategory
  ): Observable<BusinessCheckResult> {
    return this.http.post<BusinessCheckResult>(
      `${this.allocationApi}/allocations.submit`,
      { allocationYear, allocationCategory }
    );
  }

  fetchRequestedFile(
    allocationYear: number,
    allocationCategory: AllocationCategory
  ): Observable<HttpResponse<Blob>> {
    const headers = new HttpHeaders({
      responseType: 'blob',
    });
    let params = new HttpParams();
    params = params.set('allocationYear', allocationYear.toString());
    params = params.set('allocationCategory', allocationCategory.toString());

    return this.http.get(`${this.allocationApi}/allocations.get.file`, {
      headers,
      observe: 'response',
      responseType: 'blob',
      params,
    });
  }

  getIsPendingAllocation(): Observable<boolean> {
    return this.http
      .get<{ isPending: boolean }>(
        `${this.allocationApi}/allocations.get.pending`
      )
      .pipe(map((r) => r.isPending));
  }

  cancelPendingAllocation(): Observable<void> {
    return this.http.delete<void>(
      `${this.allocationApi}/allocations.cancel.pending`
    );
  }
}
