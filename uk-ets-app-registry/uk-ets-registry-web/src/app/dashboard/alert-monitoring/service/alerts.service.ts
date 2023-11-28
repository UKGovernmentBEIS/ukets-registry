import { HttpClient, HttpParams } from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { UK_ETS_REGISTRY_API_BASE_URL } from 'src/app/app.tokens';
import { AlertsModel } from '@registry-web/dashboard/alert-monitoring/model/alerts.model';

@Injectable({
  providedIn: 'root',
})
export class AlertsService {
  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    private ukEtsRegistryApiBaseUrl: string,
    private httpClient: HttpClient
  ) {}

  getAlerts(urId: string): Observable<AlertsModel> {
    const options = { params: new HttpParams().set('urId', urId) };
    return this.httpClient.get<AlertsModel>(
      `${this.ukEtsRegistryApiBaseUrl}/alerts.get.details`,
      options
    );
  }
}
