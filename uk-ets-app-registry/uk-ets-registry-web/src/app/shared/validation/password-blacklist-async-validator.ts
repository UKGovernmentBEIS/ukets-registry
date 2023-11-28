import {
  AbstractControl,
  AsyncValidator,
  ValidationErrors,
} from '@angular/forms';
import { Observable, of } from 'rxjs';
import { Inject, Injectable } from '@angular/core';
import { catchError, map } from 'rxjs/operators';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import * as CryptoJS from 'crypto-js';
import { PWNED_PASSWORDS_API_URL } from '@registry-web/app.tokens';

@Injectable({ providedIn: 'root' })
export class PasswordBlacklistAsyncValidator implements AsyncValidator {
  constructor(
    @Inject(PWNED_PASSWORDS_API_URL)
    private pwnedPasswordsApiUrl: string,
    private http: HttpClient
  ) {}

  /**
   * @description
   * Validate that the supplied password has not been compromised using
   * search by range API of https://haveibeenpwned.com/API/v3#SearchingPwnedPasswordsByRange.
   * In case the http request fails the result is null (i.e. passes the validation) so that we
   * do not depend on the external service.
   *
   * @usageNotes
   *
   * ### Validate that the supplied password is not compromised
   *
   * ```typescript
   * FormGroup fg = formbuilder.group(
   * {
   *   password: new FormControl(''),
   *   pconfirm: new FormControl(''),
   * },
   * {
   *   asyncValidators: [this.passwordBlacklistValidatorService.denyListValidator()],
   * }
   *
   * console.log(control.errors); // {blacklisted: true}
   * ```
   *
   * @returns An async validator function that returns an error map with the
   * `blacklisted` property if the validation check fails, otherwise `null`.
   *
   * @see `updateValueAndValidity()`
   *
   */
  validate(
    control: AbstractControl
  ): Promise<ValidationErrors | null> | Observable<ValidationErrors | null> {
    return this.isBlacklistedPassword(control.value['password']).pipe(
      map((isBlacklisted) => (isBlacklisted ? { blacklisted: true } : null)),
      catchError(() => of(null))
    );
  }

  isBlacklistedPassword(password: string): Observable<boolean> {
    const hashObject = CryptoJS.SHA1(password);
    const hexString = hashObject.toString();
    const prefix = hexString.substr(0, 5);
    const suffix = hexString.substr(5);

    const httpOptions = new HttpHeaders({
      'Content-Type': 'text/plain',
    });

    const remoteServiceURL = `${this.pwnedPasswordsApiUrl}/${prefix}`;
    return this.http
      .get(remoteServiceURL, { headers: httpOptions, responseType: 'text' })
      .pipe(
        map((result) => this.isPwned(result, suffix)),
        catchError(() => of(false))
      );
  }

  /**
   * Check if the provided suffix is contained in the result list.
   * @param result The response from the external system.
   * @param suffix The hexadecimal representation of the hashed password form position 5 to end.
   */
  private isPwned(result: string, suffix: string): boolean {
    return result.toUpperCase().indexOf(suffix.toUpperCase()) >= 0;
  }
}
