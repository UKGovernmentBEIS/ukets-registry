import { ApiErrorHandlingService } from '@shared/services';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { select, Store } from '@ngrx/store';
import { Router } from '@angular/router';
import {
  catchError,
  exhaustMap,
  map,
  mergeMap,
  switchMap,
  tap,
  withLatestFrom,
} from 'rxjs/operators';
import { UserDetailsUpdateActions } from '@user-update/action';
import { Injectable } from '@angular/core';
import { getRouteFromArray } from '@shared/utils/router.utils';
import {
  UserDetailsUpdateWizardPathsModel,
  UserUpdateDetailsType,
} from '@user-update/model';
import { HttpErrorResponse } from '@angular/common/http';
import { of } from 'rxjs';
import { errors } from '@shared/shared.action';
import { UserDetailsUpdateApiService } from '@user-update/services';
import {
  fetchCurrentUserDetailsInfo,
  setCurrentUserDetailsInfoSuccess,
} from '@user-update/action/user-details-update.action';
import {
  selectCurrentUserDetailsInfo,
  selectIsLoadedFromMyProfilePage,
} from '@user-update/reducers';
import { selectUserDetails } from '@user-management/user-details/store/reducers';

@Injectable()
export class UserDetailsUpdateEffects {
  constructor(
    private apiErrorHandlingService: ApiErrorHandlingService,
    private actions$: Actions,
    private store: Store,
    private router: Router,
    private userDetailsUpdateApiService: UserDetailsUpdateApiService
  ) {}

  navigateTo$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(UserDetailsUpdateActions.navigateTo),
        tap((action) => {
          this.router.navigate([action.route], action.extras);
        })
      );
    },
    { dispatch: false }
  );

  fetchCurrentUserDetailsInfo$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(fetchCurrentUserDetailsInfo),
      switchMap((action) => {
        return this.userDetailsUpdateApiService
          .fetchUserDetailsInfo(action.urid)
          .pipe(
            map((data) =>
              setCurrentUserDetailsInfoSuccess({
                userDetails:
                  this.userDetailsUpdateApiService.transformToUserObject(data),
              })
            ),
            catchError((error: HttpErrorResponse) => {
              return of(
                errors({
                  errorSummary: this.apiErrorHandlingService.transform(
                    error.error
                  ),
                })
              );
            })
          );
      })
    );
  });

  setRequestUpdateType$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(UserDetailsUpdateActions.setRequestUpdateType),
      withLatestFrom(this.store.pipe(select(selectCurrentUserDetailsInfo))),
      exhaustMap(([action, userDetails]) => {
        return this.userDetailsUpdateApiService
          .validateUserUpdateRequest(action.updateType, userDetails.urid)
          .pipe(
            map(() => {
              switch (action.updateType) {
                case UserUpdateDetailsType.UPDATE_USER_DETAILS:
                  return UserDetailsUpdateActions.navigateTo({
                    route: getRouteFromArray([
                      'user-details',
                      userDetails.urid,
                      UserDetailsUpdateWizardPathsModel.BASE_PATH,
                      UserDetailsUpdateWizardPathsModel.PERSONAL_DETAILS,
                    ]),
                    extras: {
                      skipLocationChange: true,
                    },
                  });
                case UserUpdateDetailsType.DEACTIVATE_USER:
                  return UserDetailsUpdateActions.navigateTo({
                    route: getRouteFromArray([
                      'user-details',
                      userDetails.urid,
                      UserDetailsUpdateWizardPathsModel.BASE_PATH,
                      UserDetailsUpdateWizardPathsModel.DEACTIVATION_COMMENT,
                    ]),
                    extras: {
                      skipLocationChange: true,
                    },
                  });
              }
            }),
            catchError((error: HttpErrorResponse) => {
              return of(
                errors({
                  errorSummary: this.apiErrorHandlingService.transform(
                    error.error
                  ),
                })
              );
            })
          );
      })
    );
  });

  setPersonalDetails$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(UserDetailsUpdateActions.setPersonalDetailsRequest),
      withLatestFrom(this.store.pipe(select(selectCurrentUserDetailsInfo))),
      map(([action, userDetails]) => {
        return UserDetailsUpdateActions.navigateTo({
          route: getRouteFromArray([
            'user-details',
            userDetails.urid,
            UserDetailsUpdateWizardPathsModel.BASE_PATH,
            UserDetailsUpdateWizardPathsModel.WORK_DETAILS,
          ]),
          extras: {
            skipLocationChange: true,
          },
        });
      })
    );
  });

  setWorkDetails$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(UserDetailsUpdateActions.setWorkDetailsRequest),
      withLatestFrom(
        this.store.pipe(select(selectCurrentUserDetailsInfo)),
        this.store.select(selectIsLoadedFromMyProfilePage)
      ),
      map(([action, userDetails, isLoadedFromMyProfilePage]) => {
        if (!isLoadedFromMyProfilePage) {
          return UserDetailsUpdateActions.navigateTo({
            route: getRouteFromArray([
              'user-details',
              userDetails.urid,
              UserDetailsUpdateWizardPathsModel.BASE_PATH,
              UserDetailsUpdateWizardPathsModel.MEMORABLE_PHRASE,
            ]),
            extras: {
              skipLocationChange: true,
            },
          });
        } else {
          return UserDetailsUpdateActions.navigateTo({
            route: getRouteFromArray([
              'user-details',
              userDetails.urid,
              UserDetailsUpdateWizardPathsModel.BASE_PATH,
              UserDetailsUpdateWizardPathsModel.CHECK_USER_DETAILS_UPDATE_REQUEST,
            ]),
            extras: {
              skipLocationChange: true,
            },
          });
        }
      })
    );
  });

  navigateToCheckDeactivationDetails$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(UserDetailsUpdateActions.setDeactivationComment),
      withLatestFrom(this.store.select(selectCurrentUserDetailsInfo)),
      map(([, currentUserDetails]) => {
        return UserDetailsUpdateActions.navigateTo({
          route: getRouteFromArray([
            'user-details',
            currentUserDetails.urid,
            UserDetailsUpdateWizardPathsModel.BASE_PATH,
            UserDetailsUpdateWizardPathsModel.CHECK_DEACTIVATION_DETAILS_REQUEST,
          ]),
          extras: {
            skipLocationChange: true,
          },
        });
      })
    );
  });

  navigateToCheckAndSubmitPage$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(UserDetailsUpdateActions.setNewUserDetailsInfoSuccess),
      withLatestFrom(this.store.select(selectCurrentUserDetailsInfo)),
      map(([action, currentUserDetails]) => {
        return UserDetailsUpdateActions.navigateTo({
          route: getRouteFromArray([
            'user-details',
            currentUserDetails.urid,
            UserDetailsUpdateWizardPathsModel.BASE_PATH,
            UserDetailsUpdateWizardPathsModel.CHECK_USER_DETAILS_UPDATE_REQUEST,
          ]),
          extras: {
            skipLocationChange: true,
          },
        });
      })
    );
  });

  submitUpdateRequest$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(UserDetailsUpdateActions.submitUpdateRequest),
      withLatestFrom(this.store.select(selectCurrentUserDetailsInfo)),
      exhaustMap(([action, currentUserDetails]) => {
        return this.userDetailsUpdateApiService
          .submitRequest(
            currentUserDetails.urid,
            action.userDetails,
            currentUserDetails
          )
          .pipe(
            map((data) => {
              return UserDetailsUpdateActions.submitUpdateRequestSuccess({
                requestId: data,
              });
            }),
            catchError((error: HttpErrorResponse) => {
              return of(
                errors({
                  errorSummary: this.apiErrorHandlingService.transform(
                    error.error
                  ),
                })
              );
            })
          );
      })
    );
  });

  submitDeactivationRequest$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(UserDetailsUpdateActions.submitDeactivationRequest),
      withLatestFrom(this.store.select(selectUserDetails)),
      exhaustMap(([action, userDetails]) => {
        return this.userDetailsUpdateApiService
          .deactivateRequest(
            userDetails.attributes.urid[0],
            action.deactivationComment
          )
          .pipe(
            map((data) => {
              return UserDetailsUpdateActions.submitUpdateRequestSuccess({
                requestId: data,
              });
            }),
            catchError((error: HttpErrorResponse) => {
              return of(
                errors({
                  errorSummary: this.apiErrorHandlingService.transform(
                    error.error
                  ),
                })
              );
            })
          );
      })
    );
  });

  navigateToRequestSubmitted$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(UserDetailsUpdateActions.submitUpdateRequestSuccess),
      withLatestFrom(this.store.pipe(select(selectCurrentUserDetailsInfo))),
      map(([, userDetails]) =>
        UserDetailsUpdateActions.navigateTo({
          route: `/user-details/${userDetails.urid}/${UserDetailsUpdateWizardPathsModel.BASE_PATH}/${UserDetailsUpdateWizardPathsModel.REQUEST_SUBMITTED}`,
          extras: {
            skipLocationChange: true,
          },
        })
      )
    );
  });

  cancelClicked$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(UserDetailsUpdateActions.cancelClicked),
      withLatestFrom(this.store.pipe(select(selectCurrentUserDetailsInfo))),
      map(([action, userDetails]) =>
        UserDetailsUpdateActions.navigateTo({
          route: `/user-details/${userDetails.urid}/${UserDetailsUpdateWizardPathsModel.BASE_PATH}/${UserDetailsUpdateWizardPathsModel.CANCEL_UPDATE_REQUEST}`,
          extras: {
            queryParams: { goBackRoute: action.route },
            skipLocationChange: true,
          },
        })
      )
    );
  });

  cancelUserUpdateRequest$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(UserDetailsUpdateActions.cancelUserUpdateRequest),
      withLatestFrom(
        this.store.pipe(select(selectCurrentUserDetailsInfo)),
        this.store.pipe(select(selectIsLoadedFromMyProfilePage))
      ),
      mergeMap(([, userDetails, isLoadedFromMyProfilePage]) => [
        UserDetailsUpdateActions.clearUserDetailsUpdateRequest(),
        UserDetailsUpdateActions.navigateTo({
          route: isLoadedFromMyProfilePage
            ? `/user-details/my-profile`
            : `/user-details/${userDetails.urid}`,
        }),
      ])
    );
  });
}
