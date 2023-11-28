import { HttpClient } from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { UK_ETS_REGISTRY_API_BASE_URL } from 'src/app/app.tokens';
import { DashboardNotification } from '@registry-web/dashboard/notifications/model';

@Injectable({
  providedIn: 'root',
})
export class NotificationsService {
  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    private ukEtsRegistryApiBaseUrl: string,
    private httpClient: HttpClient
  ) {}

  getNotifications(): Observable<DashboardNotification[]> {
    return this.httpClient.get<DashboardNotification[]>(
      `${this.ukEtsRegistryApiBaseUrl}/notifications.list.for-dashboard`
    );
  }
}
