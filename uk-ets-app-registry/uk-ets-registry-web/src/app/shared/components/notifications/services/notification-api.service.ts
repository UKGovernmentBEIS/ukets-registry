import { Inject, Injectable } from '@angular/core';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';
import {
  HttpClient,
  HttpEvent,
  HttpParams,
  HttpResponse,
} from '@angular/common/http';
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
  cancelNotification: string;
  retrieveNotificationDefinition: string;
  retrieveNotification: string;
  uploadRecipientsEmailsFile: string;
  downloadEmailsFile: string;
  submitEmailDetails: string;

  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    ukEtsRegistryApiBaseUrl: string,
    private http: HttpClient
  ) {
    this.updateNotification = `${ukEtsRegistryApiBaseUrl}/notifications.update`;
    this.newNotification = `${ukEtsRegistryApiBaseUrl}/notifications.create`;
    this.cancelNotification = `${ukEtsRegistryApiBaseUrl}/notifications.cancel`;
    this.retrieveNotificationDefinition = `${ukEtsRegistryApiBaseUrl}/notifications.get.definition`;
    this.retrieveNotification = `${ukEtsRegistryApiBaseUrl}/notifications.get`;
    this.uploadRecipientsEmailsFile = `${ukEtsRegistryApiBaseUrl}/adhoc.recipients.upload`;
    this.downloadEmailsFile = `${ukEtsRegistryApiBaseUrl}/adhoc.recipients.download`;
    this.submitEmailDetails = `${ukEtsRegistryApiBaseUrl}/notifications.validate`;
  }

  submitRequest(
    notification: Notification,
    notificationId: string,
    fileUploadId?: number
  ): Observable<string> {
    if (notificationId) {
      const params = { params: new HttpParams().set('id', notificationId) };
      return this.http.put<string>(
        this.updateNotification,
        notification,
        params
      );
    } else {
      if (fileUploadId && notification.type === NotificationType.AD_HOC_EMAIL) {
        return this.http.post<string>(this.newNotification, {
          ...notification,
          uploadedFileId: fileUploadId,
        });
      } else {
        return this.http.post<string>(this.newNotification, notification);
      }
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

  public uploadSelectedEmailsFile(
    emailsFile: File
  ): Observable<HttpEvent<any>> {
    const fData = new FormData();
    fData.append('file', emailsFile, emailsFile.name);
    return this.http.post<HttpEvent<any>>(
      `${this.uploadRecipientsEmailsFile}`,
      fData,
      {
        observe: 'events',
        reportProgress: true,
      }
    ) as Observable<HttpEvent<any>>;
  }

  downloadRecipientsEmailsFile(fileId: number): Observable<HttpResponse<Blob>> {
    return this.http.get(`${this.downloadEmailsFile}`, {
      observe: 'response',
      responseType: 'blob',
      params: new HttpParams().append('fileId', fileId),
    });
  }

  validateEmailBody(
    emailBody: string,
    fileUploadId: number,
    type:NotificationType
  ): Observable<string> {
    if (type === NotificationType.AD_HOC_EMAIL) {
      return this.http.post<string>(this.submitEmailDetails, {
        fileId: fileUploadId,
        body: emailBody,
        type:type
      });
      } else{
      return this.http.post<string>(this.submitEmailDetails, {
        body: emailBody,
        type:type
      });
      }
  }

  cancelActiveOrPendingNotification(notificationId: string): Observable<void> {
      const params = { params: new HttpParams().set('id', notificationId) };
      return this.http.post<void>(
        this.cancelNotification,
        {},
        params
      );
  }
}
