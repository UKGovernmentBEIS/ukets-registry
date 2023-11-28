import { Injectable, Inject } from '@angular/core';
import { of, Observable } from 'rxjs';
import {
  ResetPasswordRequest,
  ResetPasswordResponse,
  ValidateTokenResponse
} from '../model';
import { UK_ETS_REGISTRY_API_BASE_URL } from 'src/app/app.tokens';
import { HttpClient, HttpParams } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ForgotPasswordApiService {
  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    private ukEtsRegistryApiBaseUrl: string,
    private http: HttpClient
  ) {}

  public requestResetPasswordEmail(email: string): Observable<void> {
    return this.http.post<void>(
      `${this.ukEtsRegistryApiBaseUrl}/forgot-password.request.link`,
      null,
      {
        params: new HttpParams().set('email', email)
      }
    );
  }

  public validateToken(token: string): Observable<ValidateTokenResponse> {
    return this.http.post<ValidateTokenResponse>(
      `${this.ukEtsRegistryApiBaseUrl}/forgot-password.validate.token`,
      null,
      {
        params: new HttpParams().set('token', token)
      }
    );
  }

  public resetPassword(
    req: ResetPasswordRequest
  ): Observable<ResetPasswordResponse> {
    return this.http.post<ResetPasswordResponse>(
      `${this.ukEtsRegistryApiBaseUrl}/forgot-password.reset.password`,
      req
    );
  }
}
