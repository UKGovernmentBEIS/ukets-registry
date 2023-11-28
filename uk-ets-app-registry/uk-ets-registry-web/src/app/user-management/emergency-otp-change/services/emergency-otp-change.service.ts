import { Inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UK_ETS_REGISTRY_API_BASE_URL } from '../../../app.tokens';
import { EmergencyOtpChangeTaskResponse } from '@user-management/emergency-otp-change/model/emergency-otp-change.model';

@Injectable({
  providedIn: 'root'
})
export class EmergencyOtpChangeService {
  requestEmergencyOtpChangeUrl: string;
  requestEmergencyOtpChangeTaskUrl: string;

  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    ukEtsRegistryApiBaseUrl: string,
    private http: HttpClient
  ) {
    this.requestEmergencyOtpChangeUrl = `${ukEtsRegistryApiBaseUrl}/user-profile.emergency.otp.request`;
    this.requestEmergencyOtpChangeTaskUrl = `${ukEtsRegistryApiBaseUrl}/user-profile.emergency.otp.task`;
  }

  submitEmail(email: string): Observable<void> {
    return this.http.post<void>(this.requestEmergencyOtpChangeUrl, { email });
  }

  createTask(token: string): Observable<EmergencyOtpChangeTaskResponse> {
    return this.http.post<EmergencyOtpChangeTaskResponse>(
      this.requestEmergencyOtpChangeTaskUrl,
      { token }
    );
  }
}
