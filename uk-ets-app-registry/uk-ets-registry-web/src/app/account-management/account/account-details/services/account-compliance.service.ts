import { HttpClient, HttpParams } from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import {
  ComplianceOverviewResult,
  ComplianceStatusHistoryResult,
  VerifiedEmissionsResult,
} from '@account-shared/model';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';
import { Observable } from 'rxjs';
import { DomainEvent } from '@registry-web/shared/model/event';

@Injectable({
  providedIn: 'root',
})
export class AccountComplianceService {
  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    private ukEtsRegistryApiBaseUrl: string,
    private http: HttpClient
  ) {}

  fetchAccountVerifiedEmissions(
    compliantEntityId: number
  ): Observable<VerifiedEmissionsResult> {
    const options = {
      params: new HttpParams().set(
        'compliantEntityId',
        compliantEntityId.toString()
      ),
    };
    return this.http.get<VerifiedEmissionsResult>(
      `${this.ukEtsRegistryApiBaseUrl}/compliance.get.emissions`,
      options
    );
  }

  fetchAccountComplianceStatusHistory(
    compliantEntityId: number
  ): Observable<ComplianceStatusHistoryResult> {
    const options = {
      params: new HttpParams().set(
        'compliantEntityId',
        compliantEntityId.toString()
      ),
    };
    return this.http.get<ComplianceStatusHistoryResult>(
      `${this.ukEtsRegistryApiBaseUrl}/compliance.get.status.history`,
      options
    );
  }

  fetchAccountComplianceHistoryAndComments(
    compliantEntityId: number
  ): Observable<DomainEvent[]> {
    const options = {
      params: new HttpParams().set(
        'compliantEntityId',
        compliantEntityId.toString()
      ),
    };
    return this.http.get<DomainEvent[]>(
      `${this.ukEtsRegistryApiBaseUrl}/compliance.get.events.history`,
      options
    );
  }

  public fetchAccountComplianceOverview(
    accountIdentifier: number
  ): Observable<ComplianceOverviewResult> {
    const options = {
      params: new HttpParams().set(
        'accountIdentifier',
        accountIdentifier.toString()
      ),
    };
    return this.http.get<ComplianceOverviewResult>(
      `${this.ukEtsRegistryApiBaseUrl}/compliance.get.overview`,
      options
    );
  }
}
