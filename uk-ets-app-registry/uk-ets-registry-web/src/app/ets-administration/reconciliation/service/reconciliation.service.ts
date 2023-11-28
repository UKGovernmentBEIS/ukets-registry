import { Inject, Injectable } from '@angular/core';
import { UK_ETS_REGISTRY_API_BASE_URL } from '../../../app.tokens';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Reconciliation } from '@shared/model/reconciliation-model';

@Injectable({
  providedIn: 'root',
})
export class ReconciliationService {
  fetchLatestReconciliationUrl: string;
  startReconciliationUrl: string;

  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    ukEtsRegistryApiBaseUrl: string,
    private http: HttpClient
  ) {
    this.fetchLatestReconciliationUrl = `${ukEtsRegistryApiBaseUrl}/reconciliation.get.latest`;
    this.startReconciliationUrl = `${ukEtsRegistryApiBaseUrl}/reconciliation.start`;
  }

  fetchLatestReconciliation(): Observable<Reconciliation> {
    return this.http.get<Reconciliation>(this.fetchLatestReconciliationUrl);
  }

  startReconciliation(): Observable<Reconciliation> {
    const body = {};
    return this.http.post<Reconciliation>(this.startReconciliationUrl, body);
  }
}
