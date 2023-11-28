import { Inject, Injectable } from '@angular/core';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';
import { HttpClient, HttpParams } from '@angular/common/http';
import { PasswordChangeRequest } from '@password-change/model';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class RequestPasswordChangeService {
  requestPasswordChangeUrl: string;

  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    ukEtsRegistryApiBaseUrl: string,
    private http: HttpClient
  ) {
    this.requestPasswordChangeUrl = `${ukEtsRegistryApiBaseUrl}/user-profile.update.password`;
  }

  changePassword(request: PasswordChangeRequest): Observable<unknown> {
    return this.http.post(
      this.requestPasswordChangeUrl,
      {
        currentPassword: request.currentPassword,
        newPassword: request.newPassword,
      },
      { params: new HttpParams().set('otp', request.otp) }
    );
  }
}
