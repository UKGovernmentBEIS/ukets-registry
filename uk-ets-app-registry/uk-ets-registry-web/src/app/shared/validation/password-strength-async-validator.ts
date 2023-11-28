import {
  AbstractControl,
  AsyncValidator,
  ValidationErrors,
} from '@angular/forms';
import { Observable, of } from 'rxjs';
import { Inject, Injectable } from '@angular/core';
import { PasswordStrengthResponse, Score } from '@uk-password-control/model';
import { catchError, delay, map } from 'rxjs/operators';
import { UK_ETS_PASSWORD_VALIDATION_API_BASE_URL } from '@registry-web/app.tokens';
import { HttpClient } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class PasswordStrengthAsyncValidator implements AsyncValidator {
  private passwordStrengthApiUrl: string;

  constructor(
    @Inject(UK_ETS_PASSWORD_VALIDATION_API_BASE_URL)
    private ukEtsPasswordValidationApiBaseUrl: string,
    private http: HttpClient
  ) {
    this.passwordStrengthApiUrl = `${this.ukEtsPasswordValidationApiBaseUrl}/strength.calculate`;
  }

  /**
   * @description
   * Validate that the supplied password has at least strength that is strictly greater than
   * the provided minimum strength.The calculation is delegated to the API endpoint /strength.calculate
   * which uses the zxcvbn4j library for calculating the password strength.
   * In case the http request fails the result is zero-(0) Strength.
   *
   * @usageNotes
   *
   * ### Validate the strength of the supplied password
   *
   * ```typescript
   * FormGroup fg = formbuilder.group(
   * {
   *   password: new FormControl(''),
   *   pconfirm: new FormControl(''),
   * },
   * {
   *   asyncValidators: [this.passwordStrengthValidatorService.strengthValidator(2)],
   * }
   *
   * console.log(control.errors); // {strength: true} or { serverError: true }
   * ```
   *
   * @returns An async validator function that returns an error map with the
   * `strength` property if the validation check fails,
   * `serverError` property if there was a communication or server error
   * otherwise `null`.
   *
   * @see `updateValueAndValidity()`
   *
   */
  validate(
    control: AbstractControl
  ): Promise<ValidationErrors | null> | Observable<ValidationErrors | null> {
    const minScore = 2 as Score;
    return this.http
      .post<PasswordStrengthResponse>(this.passwordStrengthApiUrl, {
        password: control.value['password'],
      })
      .pipe(
        map((response) => {
          return response.score <= minScore ? { strength: true } : null;
        }),
        catchError(() => of({ serverError: true }))
      ); //When the service is down return server error
  }
}
