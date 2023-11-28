import { Inject, Injectable } from '@angular/core';
import { UK_ETS_REGISTRY_API_BASE_URL } from '../../../app.tokens';
import { HttpClient, HttpParams } from '@angular/common/http';
import {
  EmailChange,
  EmailChangeRequest,
} from '@email-change/model/email-change.model';
import { Observable } from 'rxjs';

@Injectable()
export class RequestEmailChangeService {
  requestEmailChangeUrl: string;
  confirmEmailChangeUrl: string;

  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    ukEtsRegistryApiBaseUrl: string,
    private http: HttpClient
  ) {
    this.requestEmailChangeUrl = `${ukEtsRegistryApiBaseUrl}/user-profile.update.email`;
    this.confirmEmailChangeUrl = `${ukEtsRegistryApiBaseUrl}/user-profile.confirm.email`;
  }

  changeEmail(request: EmailChangeRequest): Observable<EmailChange> {
    return this.http.post<EmailChange>(
      this.requestEmailChangeUrl,
      {
        urid: request.urid,
        newEmail: request.newEmail,
      },
      {
        params: new HttpParams().set('otp', request.otp),
      }
    );
  }

  confirmEmailChange(token: string): Observable<EmailChange> {
    return this.http.post<EmailChange>(this.confirmEmailChangeUrl, null, {
      params: new HttpParams().set('token', token),
    });
  }
}
