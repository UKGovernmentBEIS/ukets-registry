import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, concatMap, exhaustMap } from 'rxjs/operators';
import { canGoBack, errors, navigateTo } from '@shared/shared.action';
import {
  cancelAuthoritySetting,
  fetchEnrolledUser,
  fetchEnrolledUserSuccess,
  navigateToCancellation,
  removeUserFromAuthorityUsers,
  removeUserFromAuthorityUsersSuccess,
  setUserAsAuthority,
  setUserAsAuthoritySuccess,
  startAuthoritySettingWizard
} from '@authority-setting/action';
import { AuthoritySettingRoutePathsModel } from '@authority-setting/model/authority-setting-route-paths.model';
import { ActivatedRoute } from '@angular/router';
import { AuthoritySettingService } from '@authority-setting/service';
import { ApiErrorHandlingService } from '@shared/services';
import { HttpErrorResponse } from '@angular/common/http';
import { of } from 'rxjs';
import { getRouteFromArray } from '@shared/utils/router.utils';

@Injectable()
export class AuthoritySettingEffect {
  constructor(
    private actions$: Actions,
    private activatedRoute: ActivatedRoute,
    private service: AuthoritySettingService,
    private apiErrorHandlingService: ApiErrorHandlingService
  ) {}

  startAuthoritySettingWizard$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(startAuthoritySettingWizard),
      concatMap(action => {
        return [
          navigateTo({
            route: `/${AuthoritySettingRoutePathsModel.REGISTRY_ADMINISTRATION}/${AuthoritySettingRoutePathsModel.BASE_PAGE}`
          })
        ];
      })
    );
  });

  fetchEnrolledUser$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(fetchEnrolledUser),
      exhaustMap(action =>
        this.service.fetchEnrolledUser(action.urid).pipe(
          concatMap(result => {
            return [
              fetchEnrolledUserSuccess({
                enrolledUser: result
              })
            ];
          }),
          catchError((error: HttpErrorResponse) => {
            return of(
              errors({
                errorSummary: this.apiErrorHandlingService.transform(
                  error.error
                )
              })
            );
          })
        )
      )
    );
  });

  fetchEnrolledUserSuccess$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(fetchEnrolledUserSuccess),
      concatMap(action => [
        navigateTo({
          route: getRouteFromArray([
            AuthoritySettingRoutePathsModel.REGISTRY_ADMINISTRATION,
            AuthoritySettingRoutePathsModel.BASE_PAGE,
            AuthoritySettingRoutePathsModel.CHECK_UPDATE_REQUEST
          ])
        })
      ])
    );
  });

  setUserAsAuthority$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(setUserAsAuthority),
      exhaustMap(action =>
        this.service.setUserAsAuthority(action.urid).pipe(
          concatMap(() => {
            return [setUserAsAuthoritySuccess()];
          }),
          catchError((error: HttpErrorResponse) => {
            return of(
              errors({
                errorSummary: this.apiErrorHandlingService.transform(
                  error.error
                )
              })
            );
          })
        )
      )
    );
  });

  removeUserFromAuthorityUsers$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(removeUserFromAuthorityUsers),
      exhaustMap(action =>
        this.service.removeUserFromAuthorityUsers(action.urid).pipe(
          concatMap(() => {
            return [removeUserFromAuthorityUsersSuccess()];
          }),
          catchError((error: HttpErrorResponse) => {
            return of(
              errors({
                errorSummary: this.apiErrorHandlingService.transform(
                  error.error
                )
              })
            );
          })
        )
      )
    );
  });

  setUserAsAuthoritySuccess$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(setUserAsAuthoritySuccess),
      concatMap(() => [
        canGoBack({
          goBackRoute: null
        }),
        navigateTo({
          route: getRouteFromArray([
            AuthoritySettingRoutePathsModel.REGISTRY_ADMINISTRATION,
            AuthoritySettingRoutePathsModel.BASE_PAGE,
            AuthoritySettingRoutePathsModel.UPDATE_REQUEST_SUCCESS
          ])
        })
      ])
    );
  });

  removeUserFromAuthorityUsersSuccess$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(removeUserFromAuthorityUsersSuccess),
      concatMap(() => [
        canGoBack({
          goBackRoute: null
        }),
        navigateTo({
          route: getRouteFromArray([
            AuthoritySettingRoutePathsModel.REGISTRY_ADMINISTRATION,
            AuthoritySettingRoutePathsModel.BASE_PAGE,
            AuthoritySettingRoutePathsModel.UPDATE_REQUEST_SUCCESS
          ])
        })
      ])
    );
  });

  navigateToCancellation$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(navigateToCancellation),
      concatMap(() => [
        canGoBack({
          goBackRoute: this.activatedRoute.snapshot['_routerState'].url
        }),
        navigateTo({
          route: getRouteFromArray([
            AuthoritySettingRoutePathsModel.REGISTRY_ADMINISTRATION,
            AuthoritySettingRoutePathsModel.BASE_PAGE,
            AuthoritySettingRoutePathsModel.CANCEL_UPDATE_REQUEST
          ])
        })
      ])
    );
  });

  cancelAuthoritySetting$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(cancelAuthoritySetting),
      concatMap(() => [
        navigateTo({
          route: `/${AuthoritySettingRoutePathsModel.REGISTRY_ADMINISTRATION}`
        })
      ])
    );
  });
}
