import { Inject, Injectable } from '@angular/core';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Reconciliation } from '@shared/model/reconciliation-model';

@Injectable({ providedIn: 'root' })
export class ItlReconciliationService {
  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    private ukEtsRegistryApiBaseUrl: string,
    private http: HttpClient
  ) {}

  fetchLatestReconciliation(): Observable<Reconciliation> {
    return this.http.get<Reconciliation>(
      `${this.ukEtsRegistryApiBaseUrl}/itl-reconciliation.get.latest`
    );
  }
}
