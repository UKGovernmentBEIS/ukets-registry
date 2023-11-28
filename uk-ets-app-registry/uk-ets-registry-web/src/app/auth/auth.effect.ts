import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import {
  AuthActionTypes,
  extendSession,
  extendSessionSuccess,
  FetchIsAdmin,
  FetchIsAdminFail,
  FetchIsAdminSuccess,
  FetchIsSeniorOrJuniorAdmin,
  FetchIsSeniorOrJuniorAdminFail,
  FetchIsSeniorOrJuniorAdminSuccess,
  getSsoSessionIdleTimeout,
  IsLoggedInCheckFail,
  IsLoggedInCheckSuccess,
  LoginFail,
  LogoutFail,
  setSsoSessionIdleTimeout,
} from './auth.actions';
import { catchError, map, mapTo, mergeMap, switchMap } from 'rxjs/operators';
import { of } from 'rxjs';
import { AuthApiService } from './auth-api.service';
import { Store } from '@ngrx/store';
import { loadNavMenuPermissions } from '@shared/shared.action';
import { UserDetailService } from '@user-management/service';

@Injectable()
export class AuthEffects {
  constructor(
    private actions$: Actions,
    private store: Store,
    private authApiService: AuthApiService,
    private userDetailsService: UserDetailService
  ) {}

  isLoggedInCheck$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActionTypes.IsLoggedInCheck),
      mergeMap(() =>
        this.authApiService.isLoggedIn().pipe(
          switchMap((response) => [IsLoggedInCheckSuccess(response)]),
          catchError((error) => of(IsLoggedInCheckFail(error)))
        )
      )
    )
  );

  login$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActionTypes.Login),
      mergeMap((options: { redirectUri: string }) =>
        this.authApiService.login(options).pipe(
          map(() => ({ type: AuthActionTypes.LoginSuccess })),
          catchError((error) => of(LoginFail(error)))
        )
      )
    )
  );

  logout$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActionTypes.Logout),
      mergeMap((options: { redirectUri: string }) =>
        this.authApiService.logout(options.redirectUri).pipe(
          map(() => ({ type: AuthActionTypes.LogoutSuccess })),
          catchError((error) => of(LogoutFail(error)))
        )
      )
    )
  );

  loginSuccess$ = createEffect(() =>
    this.actions$.pipe(
      ofType(IsLoggedInCheckSuccess),
      mergeMap(() => [
        FetchIsAdmin(),
        FetchIsSeniorOrJuniorAdmin(),
        setSsoSessionIdleTimeout(),
      ])
    )
  );

  /**
   * To load the menu permissions we need to know if the user is authenticated.
   */
  loadMenuScopePermissions$ = createEffect(() =>
    this.actions$.pipe(
      ofType(IsLoggedInCheckSuccess, IsLoggedInCheckFail),
      map(() => loadNavMenuPermissions())
    )
  );

  fetchIsAdmin$ = createEffect(() =>
    this.actions$.pipe(
      ofType(FetchIsAdmin),
      mergeMap(() =>
        this.authApiService
          .hasScope('urn:uk-ets-registry-api:actionForAnyAdmin')
          .pipe(
            map((response) => FetchIsAdminSuccess({ isAdmin: response })),
            catchError((error) => of(FetchIsAdminFail({ error })))
          )
      )
    )
  );

  fetchIsSeniorOrJuniorAdmin$ = createEffect(() =>
    this.actions$.pipe(
      ofType(FetchIsSeniorOrJuniorAdmin),
      mergeMap(() =>
        this.authApiService
          .hasScope('urn:uk-ets-registry-api:actionForSeniorAndJuniorAdmin')
          .pipe(
            map((response) =>
              FetchIsSeniorOrJuniorAdminSuccess({
                isSeniorOrJuniorAdmin: response,
              })
            ),
            catchError((error) => of(FetchIsSeniorOrJuniorAdminFail({ error })))
          )
      )
    )
  );

  extendSession$ = createEffect(() =>
    this.actions$.pipe(
      ofType(extendSession),
      switchMap(() => this.authApiService.extendSession()),
      mapTo(extendSessionSuccess())
    )
  );

  setSsoSessionIdleTimeout$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(setSsoSessionIdleTimeout),
      map(() => {
        return getSsoSessionIdleTimeout({
          ssoSessionIdleTimeout: this.userDetailsService.getSessionIdleTimeout(),
        });
      })
    );
  });
}
