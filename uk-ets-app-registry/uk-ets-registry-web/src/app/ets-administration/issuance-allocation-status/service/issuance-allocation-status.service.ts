import { Injectable, Inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { UK_ETS_REGISTRY_API_BASE_URL } from 'src/app/app.tokens';
import { Observable } from 'rxjs';
import { AllowanceReport } from '../model';
import { DomainEvent } from '@shared/model/event';

@Injectable({
  providedIn: 'root'
})
export class IssuanceAllocationStatusService {
  issuanceAllocationStatusApi: string;
  allocationTableHistoryApi: string;

  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    ukEtsRegistryApiBaseUrl: string,
    private http: HttpClient
  ) {
    this.issuanceAllocationStatusApi = `${ukEtsRegistryApiBaseUrl}/allocations.get.issuance-by-year`;
    this.allocationTableHistoryApi = `${ukEtsRegistryApiBaseUrl}/allocation-table.get.history`;
  }

  public getIssuanceAllocationStatuses(): Observable<AllowanceReport[]> {
    return this.http.get<AllowanceReport[]>(this.issuanceAllocationStatusApi);
  }

  public getAllocationTableEvents(): Observable<DomainEvent[]> {
    return this.http.get<DomainEvent[]>(this.allocationTableHistoryApi);
  }
}
