import { HttpClient } from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class RecalculateComplianceStatusService {
  recalculateDynamicStatusApi: string;

  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    ukEtsRegistryApiBaseUrl: string,
    private http: HttpClient
  ) {
    this.recalculateDynamicStatusApi = `${ukEtsRegistryApiBaseUrl}/compliance.recalculate.dynamic-status`;
  }

  public recalculateDynamicStatusAllCompliantEntities(): Observable<any> {
    return this.http.post(`${this.recalculateDynamicStatusApi}`, {});
  }
}
