import { Inject, Injectable } from '@angular/core';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  Notification,
  NotificationDefinition,
  NotificationType,
} from '@notifications/notifications-wizard/model';

@Injectable({ providedIn: 'root' })
export class NotificationApiService {
  updateNotification: string;
  newNotification: string;
  retrieveNotificationDefinition: string;
  retrieveNotification: string;

  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    ukEtsRegistryApiBaseUrl: string,
    private http: HttpClient
  ) {
    this.updateNotification = `${ukEtsRegistryApiBaseUrl}/notifications.update`;
    this.newNotification = `${ukEtsRegistryApiBaseUrl}/notifications.create`;
    this.retrieveNotificationDefinition = `${ukEtsRegistryApiBaseUrl}/notifications.get.definition`;
    this.retrieveNotification = `${ukEtsRegistryApiBaseUrl}/notifications.get`;
  }

  submitRequest(
    notification: Notification,
    notificationId: string
  ): Observable<string> {
    if (notificationId) {
      const params = { params: new HttpParams().set('id', notificationId) };
      return this.http.put<string>(
        this.updateNotification,
        notification,
        params
      );
    } else {
      return this.http.post<string>(this.newNotification, notification);
    }
  }

  getNotificationInstance(notificationId: string): Observable<Notification> {
    const params = { params: new HttpParams().set('id', notificationId) };
    return this.http.get<Notification>(this.retrieveNotification, params);
  }

  getNotificationDefinition(
    type: NotificationType
  ): Observable<NotificationDefinition> {
    const params = { params: new HttpParams().set('type', type) };
    return this.http.get<NotificationDefinition>(
      this.retrieveNotificationDefinition,
      params
    );
  }
}
