/* eslint-disable @typescript-eslint/no-explicit-any */
import { Inject, Injectable } from '@angular/core';
import { from, Observable, Observer, of } from 'rxjs';
import { KeycloakLoginOptions, KeycloakProfile } from 'keycloak-js';
import { KeycloakLoginCheckResponse } from './auth.reducer';
import { KeycloakService } from 'keycloak-angular';
import KeycloakAuthorization, { AuthorizationRequest } from 'keycloak-js/authz';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError, switchMap } from 'rxjs/operators';
import 'core-js/features/url-search-params';
import { AccountAccess } from './auth.model';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';

@Injectable()
export class AuthApiService {
  // The Keycloak authorization client
  private authorization: KeycloakAuthorization;
  private accountAccessApiUrl: string;

  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL)
    ukEtsRegistryApiBaseUrl: string,
    private keycloak: KeycloakService,
    private httpClient: HttpClient
  ) {
    this.accountAccessApiUrl = `${ukEtsRegistryApiBaseUrl}/account/auth`;
  }

  /**
   * @returns an observable of KeycloakLoginCheckResponse
   */
  isLoggedIn(): Observable<KeycloakLoginCheckResponse> {
    return new Observable((observer: Observer<KeycloakLoginCheckResponse>) => {
      try {
        const loggedIn: boolean = this.keycloak.isLoggedIn();
        if (loggedIn) {
          this.keycloak.loadUserProfile().then((profile) => {
            observer.next({
              authenticated: loggedIn,
              idmId: this.keycloak.getKeycloakInstance().subject,
              urid: this.keycloak.getKeycloakInstance().tokenParsed['urid'],
              username: loggedIn ? this.keycloak.getUsername() : null,
              roles: loggedIn ? this.keycloak.getUserRoles() : null,
              firstName: profile ? profile.firstName : null,
              lastName: profile ? profile.lastName : null,
              knownAs: profile ? profile['attributes'].alsoKnownAs : null,
            });
          });
          this.authorization = new KeycloakAuthorization(
            this.keycloak.getKeycloakInstance()
          );
        }
      } catch (e) {
        observer.error(e);
      }
    });
  }

  /**
   * Redirects to login of keycloak authorization server.
   */
  login(options: KeycloakLoginOptions): Observable<any> {
    const login: Promise<void> = this.keycloak.login(options);

    // Creates the observable object
    // from the KeycloakPromise
    return of((observer: Observer<boolean>) => {
      login.then(() => observer.next(true));
      login.catch(() => observer.next(false));
    });
  }

  /**
   * @description
   * Performs a request for a Requesting Party Token (a.k.a RPT) to Keycloak.
   *
   * @param authorizationRequest
   * @returns An observable with the rpt
   */
  keycloakAuthorize(
    authorizationRequest: AuthorizationRequest
  ): Observable<string> {
    return new Observable((observer: Observer<string>) => {
      this.authorization.authorize(authorizationRequest).then(
        (rpt: string) => {
          observer.next(rpt);
          observer.complete();
        },
        //onDeny
        () =>
          observer.error({
            status: 403,
            statusText: `RPT Request denied.You can not access or perform the requested operation on this resource.`,
          }),
        //onError
        () =>
          observer.error({
            status: 401,
            statusText: 'Unexpected token endpoint error.',
          }) //Return a 401 HttpErrorResponse in case of error.
      );
    });
  }

  /**
   * Loads the user profile.
   */
  loadUserProfile(): Observable<KeycloakProfile> {
    return from(this.keycloak.loadUserProfile());
  }

  /**
   * Logout from keycloak authorization server.
   */
  logout(redirectUri: string): Observable<any> {
    const logout: Promise<void> = this.keycloak.logout(redirectUri);

    // Creates the observable object
    // from the KeycloakPromise
    return of((observer: Observer<boolean>) => {
      logout.then(() => observer.next(true));
      logout.catch(() => observer.next(false));
    });
  }

  hasScope(
    scopeName: string,
    isPermission?: boolean,
    clientId?: string
  ): Observable<boolean> {
    const tokenEndpoint: string =
      this.keycloak.getKeycloakInstance().authServerUrl +
      '/realms/' +
      this.keycloak.getKeycloakInstance().realm +
      '/protocol/openid-connect/token';

    const body = new URLSearchParams();
    body.set('grant_type', 'urn:ietf:params:oauth:grant-type:uma-ticket');
    body.set('audience', clientId ? clientId : 'uk-ets-registry-api');
    body.set('permission', `${isPermission ? '' : '#'}${scopeName}`);
    body.set('response_mode', 'decision');

    return this.httpClient
      .post<any>(tokenEndpoint, body.toString(), {
        headers: new HttpHeaders({
          'Content-Type': 'application/x-www-form-urlencoded',
        }),
      })
      .pipe(
        switchMap(() => {
          return of(true);
        }),
        catchError(() => {
          return of(false);
        })
      );
  }

  retrieveAccessRights(): Observable<AccountAccess[]> {
    return this.httpClient
      .get<AccountAccess[]>(this.accountAccessApiUrl, {})
      .pipe(
        switchMap((response: AccountAccess[]) => {
          return of(response);
        }),
        catchError(() => of([]))
      );
  }

  validateOtp(otp: string): Observable<boolean> {
    const otpServiceUrl =
      this.keycloak.getKeycloakInstance().authServerUrl +
      '/realms/' +
      this.keycloak.getKeycloakInstance().realm +
      `/otp-validator`;

    // return of(true);

    return this.httpClient.post<boolean>(
      otpServiceUrl,
      { otp },
      {
        headers: new HttpHeaders({
          'Content-Type': 'application/x-www-form-urlencoded',
        }),
      }
    );
  }

  extendSession(): Observable<boolean> {
    if (this.keycloak.getKeycloakInstance()) {
      return from(this.keycloak.updateToken());
    }
  }
}
