import { Inject, Injectable } from '@angular/core';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TokenChangeState } from '@user-management/token-change/reducer';
import { EmailChange } from '@email-change/model';
import { UserProfileService } from '@user-management/service';

@Injectable()
export class TokenChangeService {
  serviceEndPoint: string;
  validateTokenUrl: string;
  validateOtpUrl: string;
  getTokenDateUrl: string;

  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    ukEtsRegistryApiBaseUrl: string,
    private userProfileService: UserProfileService,
    private http: HttpClient
  ) {
    this.serviceEndPoint = `${ukEtsRegistryApiBaseUrl}/user-profile.update.token`;
    this.validateTokenUrl = `${ukEtsRegistryApiBaseUrl}/token-change.request.link`;
    this.validateOtpUrl = `${ukEtsRegistryApiBaseUrl}/validate-otp`;
    this.getTokenDateUrl = `${ukEtsRegistryApiBaseUrl}/user-profile.get.token-date`;
  }

  requestTokenChange(state: TokenChangeState): Observable<string> {
    return this.http.post<string>(this.serviceEndPoint, null, {
      params: new HttpParams().set('reason', state.reason)
    });
  }

  validateToken(token: string): Observable<boolean> {
    return this.http.post<boolean>(this.validateTokenUrl, null, {
      params: new HttpParams().set('token', token)
    });
  }

  validateOtp(otp: string): Observable<boolean> {
    return this.userProfileService.validateOtp(otp);
  }

  getTokenDate(): Observable<EmailChange> {
    return this.http.get<EmailChange>(this.getTokenDateUrl);
  }
}
