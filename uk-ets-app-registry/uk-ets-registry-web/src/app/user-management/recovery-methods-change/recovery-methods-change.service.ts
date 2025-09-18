import { HttpClient, HttpParams } from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';
import {
  RemoveRecoveryEmailRequest,
  RemoveRecoveryPhoneRequest,
  ResendUpdateRecoveryEmailSecurityCodeRequest,
  ResendUpdateRecoveryPhoneSecurityCodeRequest,
  UpdateRecoveryEmailRequest,
  UpdateRecoveryEmailResponse,
  UpdateRecoveryEmailVerificationRequest,
  UpdateRecoveryPhoneRequest,
  UpdateRecoveryPhoneResponse,
  UpdateRecoveryPhoneVerificationRequest,
} from './recovery-methods-change.models';

@Injectable({ providedIn: 'root' })
export class RecoveryMethodsChangeService {
  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    private readonly ukEtsRegistryApiBaseUrl: string,
    private readonly httpClient: HttpClient
  ) {}

  updateRecoveryPhone(
    request: UpdateRecoveryPhoneRequest
  ): Observable<UpdateRecoveryPhoneResponse> {
    return this.httpClient.post<UpdateRecoveryPhoneResponse>(
      `${this.ukEtsRegistryApiBaseUrl}/user-profile.request.security-code`,
      {
        countryCode: request.newRecoveryCountryCode,
        phoneNumber: request.newRecoveryPhoneNumber,
        otpCode: request.otpCode,
      }
    );
  }

  resendUpdateRecoveryPhoneSecurityCode(
    request: ResendUpdateRecoveryPhoneSecurityCodeRequest
  ): Observable<UpdateRecoveryPhoneResponse> {
    return this.httpClient.post<UpdateRecoveryPhoneResponse>(
      `${this.ukEtsRegistryApiBaseUrl}/user-profile.resend.security-code`,
      {
        countryCode: request.newRecoveryCountryCode,
        phoneNumber: request.newRecoveryPhoneNumber,
      }
    );
  }

  updateRecoveryPhoneVerification(
    request: UpdateRecoveryPhoneVerificationRequest
  ): Observable<any> {
    return this.httpClient.post(
      `${this.ukEtsRegistryApiBaseUrl}/user-profile.update.recovery`,
      {
        countryCode: request.newRecoveryCountryCode,
        phoneNumber: request.newRecoveryPhoneNumber,
        securityCode: request.securityCode,
      }
    );
  }

  removeRecoveryPhone(request: RemoveRecoveryPhoneRequest): Observable<any> {
    return this.httpClient.post(
      `${this.ukEtsRegistryApiBaseUrl}/user-profile.remove.recovery`,
      { removePhoneNumber: true, otpCode: request.otpCode }
    );
  }

  updateRecoveryEmail(
    request: UpdateRecoveryEmailRequest
  ): Observable<UpdateRecoveryEmailResponse> {
    return this.httpClient.post<UpdateRecoveryEmailResponse>(
      `${this.ukEtsRegistryApiBaseUrl}/user-profile.request.security-code`,
      { email: request.newRecoveryEmailAddress, otpCode: request.otpCode }
    );
  }

  resendUpdateRecoveryEmailSecurityCode(
    request: ResendUpdateRecoveryEmailSecurityCodeRequest
  ): Observable<UpdateRecoveryEmailResponse> {
    return this.httpClient.post<UpdateRecoveryEmailResponse>(
      `${this.ukEtsRegistryApiBaseUrl}/user-profile.resend.security-code`,
      { email: request.newRecoveryEmailAddress }
    );
  }

  updateRecoveryEmailVerification(
    request: UpdateRecoveryEmailVerificationRequest
  ): Observable<any> {
    return this.httpClient.post(
      `${this.ukEtsRegistryApiBaseUrl}/user-profile.update.recovery`,
      {
        email: request.newRecoveryEmailAddress,
        securityCode: request.securityCode,
      }
    );
  }

  removeRecoveryEmail(request: RemoveRecoveryEmailRequest): Observable<any> {
    return this.httpClient.post(
      `${this.ukEtsRegistryApiBaseUrl}/user-profile.remove.recovery`,
      { removeEmail: true, otpCode: request.otpCode }
    );
  }

  hideRecoveryMethods(): Observable<any> {
    return this.httpClient.post(
      `${this.ukEtsRegistryApiBaseUrl}/user-profile.hide.recovery-methods`,
      {}
    );
  }
}
