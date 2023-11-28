import { HttpClient } from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import { UK_ETS_PASSWORD_VALIDATION_API_BASE_URL } from '@registry-web/app.tokens';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import {
  PasswordStrengthRequest,
  PasswordStrengthResponse,
  Score,
} from '@uk-password-control/model';

@Injectable({
  providedIn: 'root',
})
export class PasswordStrengthValidatorService {
  private passwordStrengthApiUrl: string;

  constructor(
    @Inject(UK_ETS_PASSWORD_VALIDATION_API_BASE_URL)
    private ukEtsPasswordValidationApiBaseUrl: string,
    private http: HttpClient
  ) {
    this.passwordStrengthApiUrl = `${this.ukEtsPasswordValidationApiBaseUrl}/strength.calculate`;
  }

  /**
   * Performs the call to the external strength service.
   *
   *  @param passwordStrengthRequest - Password
   */
  getPasswordStrength(
    passwordStrengthRequest: PasswordStrengthRequest
  ): Observable<PasswordStrengthResponse> {
    const score: Score = 0;
    return this.http
      .post<PasswordStrengthResponse>(
        this.passwordStrengthApiUrl,
        passwordStrengthRequest
      )
      .pipe(catchError(() => of({ score }))); //When the service is down return score 0
  }
}
