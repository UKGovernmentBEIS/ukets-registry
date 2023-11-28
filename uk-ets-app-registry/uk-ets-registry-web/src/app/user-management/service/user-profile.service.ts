import { Inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';

@Injectable({
  providedIn: 'root'
})
export class UserProfileService {
  validateOtpUrl: string;

  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    ukEtsRegistryApiBaseUrl: string,
    private http: HttpClient
  ) {
    this.validateOtpUrl = `${ukEtsRegistryApiBaseUrl}/validate-otp`;
  }

  validateOtp(otp: string): Observable<boolean> {
    return this.http.post<boolean>(this.validateOtpUrl, null, {
      params: new HttpParams().set('otp', otp)
    });
  }
}
