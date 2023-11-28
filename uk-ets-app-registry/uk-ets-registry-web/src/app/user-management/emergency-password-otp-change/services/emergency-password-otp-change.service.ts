import { Inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UK_ETS_REGISTRY_API_BASE_URL } from '../../../app.tokens';
import { EmergencyPasswordOtpChangeTaskResponse } from '@user-management/emergency-password-otp-change/model';

@Injectable({
  providedIn: 'root'
})
export class EmergencyPasswordOtpChangeService {
  requestEmergencyPasswordOtpChangeUrl: string;
  requestEmergencyPasswordOtpChangeTaskUrl: string;

  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    ukEtsRegistryApiBaseUrl: string,
    private http: HttpClient
  ) {
    this.requestEmergencyPasswordOtpChangeUrl = `${ukEtsRegistryApiBaseUrl}/user-profile.emergency.password-otp.request`;
    this.requestEmergencyPasswordOtpChangeTaskUrl = `${ukEtsRegistryApiBaseUrl}/user-profile.emergency.password-otp.task`;
  }

  submitEmail(email: string): Observable<void> {
    return this.http.post<void>(this.requestEmergencyPasswordOtpChangeUrl, {
      email
    });
  }

  createTask(
    token: string
  ): Observable<EmergencyPasswordOtpChangeTaskResponse> {
    return this.http.post<EmergencyPasswordOtpChangeTaskResponse>(
      this.requestEmergencyPasswordOtpChangeTaskUrl,
      { token }
    );
  }
}
