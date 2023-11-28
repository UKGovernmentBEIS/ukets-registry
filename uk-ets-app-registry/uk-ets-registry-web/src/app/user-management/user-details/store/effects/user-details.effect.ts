import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import * as UserDetailsActions from '../actions';
import {
  retrieveARsInAccount,
  retrieveEnrolmentKeyDetails,
  retrieveUser,
  retrieveUserFiles,
  retrieveUserHistory,
} from '../actions';
import {
  catchError,
  concatMap,
  filter,
  map,
  mergeMap,
  switchMap,
  tap,
  withLatestFrom,
} from 'rxjs/operators';
import { of } from 'rxjs';
import { ApiErrorBody } from '@shared/api-error/api-error';
import { UserDetailService } from '@user-management/service';
import { canGoBack, errors } from '@shared/shared.action';
import { HttpErrorResponse } from '@angular/common/http';
import { ExportFileService } from '@shared/export-file/export-file.service';
import { ApiErrorHandlingService } from '@shared/services';
import { select, Store } from '@ngrx/store';
import { Router } from '@angular/router';
import {
  AccountOpeningTaskDetails,
  AuthoriseRepresentativeTaskDetails,
  RequestType,
  TaskOutcome,
} from '@task-management/model';
import { selectUrid } from '@user-management/user-details/store/reducers';
import { TaskDetailsApiActions } from '@task-details/actions';

@Injectable()
export class UserDetailsEffect {
  constructor(
    private userDetailService: UserDetailService,
    private exportFileService: ExportFileService,
    private apiErrorHandlingService: ApiErrorHandlingService,
    private actions$: Actions,
    private store: Store,
    private router: Router
  ) {}

  refreshCachedUserDetails$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TaskDetailsApiActions.completeTaskWithApprovalSuccess),
      withLatestFrom(this.store.pipe(select(selectUrid))),
      filter(([action, currentUrid]) => {
        return (
          (action.taskCompleteResponse?.taskDetailsDTO?.taskType ===
            RequestType.ACCOUNT_OPENING_REQUEST ||
            action.taskCompleteResponse?.taskDetailsDTO?.taskType ===
              RequestType.USER_DETAILS_UPDATE_REQUEST ||
            action.taskCompleteResponse?.taskDetailsDTO?.taskType ===
              RequestType.ACCOUNT_OPENING_INSTALLATION_TRANSFER_REQUEST ||
            action.taskCompleteResponse?.taskDetailsDTO?.taskType ===
              RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST ||
            action.taskCompleteResponse?.taskDetailsDTO?.taskType ===
              RequestType.AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST) &&
          action.taskCompleteResponse?.taskDetailsDTO?.requestStatus ===
            TaskOutcome.APPROVED &&
          currentUrid != null &&
          action.taskCompleteResponse?.taskDetailsDTO?.userDetails &&
          action.taskCompleteResponse.taskDetailsDTO.userDetails.length > 0 &&
          action.taskCompleteResponse.taskDetailsDTO.userDetails.filter(
            (ud) => ud.attributes.urid[0] === currentUrid
          ).length > 0
        );
      }),
      mergeMap(([action, currentUrid]) => {
        const taskDetails = action.taskCompleteResponse.taskDetailsDTO as
          | AuthoriseRepresentativeTaskDetails
          | AccountOpeningTaskDetails;
        const keycloakUsers = taskDetails.userDetails.filter(
          (ud) => ud.attributes.urid[0] === currentUrid
        );
        return [
          UserDetailsActions.retrieveUserSuccess({
            user: keycloakUsers[0],
          }),
          retrieveARsInAccount({
            urid: keycloakUsers[0].attributes.urid[0],
          }),
          retrieveUserHistory({
            urid: keycloakUsers[0].attributes.urid[0],
          }),
          retrieveUserFiles({
            urid: keycloakUsers[0].attributes.urid[0],
          }),
          retrieveEnrolmentKeyDetails({
            urid: keycloakUsers[0].attributes.urid[0],
          }),
        ];
      })
    );
  });

  prepareNavigationToUserDetails$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(UserDetailsActions.prepareNavigationToUserDetails),
      withLatestFrom(this.store.pipe(select(selectUrid))),
      concatMap(([action, currentUrid]) => {
        // in case the user is trying to access the my profile page,
        // the urid for all requests should be the current logged in urid.
        if (!action.urid) {
          action = { ...action, urid: currentUrid };
        }
        return [
          retrieveUser(action),
          retrieveARsInAccount(action),
          retrieveUserHistory(action),
          retrieveUserFiles(action),
          retrieveEnrolmentKeyDetails(action),
          canGoBack({ goBackRoute: action.backRoute }),
        ];
      })
    );
  });

  retrieveUser$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(UserDetailsActions.retrieveUser),
      switchMap((action: { urid: string }) => {
        return this.userDetailService
          .getUserDetail(action.urid)
          .pipe(
            map((user) => UserDetailsActions.retrieveUserSuccess({ user }))
          );
      }),
      catchError((err) => of(UserDetailsActions.retrieveUserError(err)))
    );
  });

  fetchUserDetailsUpdatePendingApproval$ = createEffect(() =>
    this.actions$.pipe(
      ofType(UserDetailsActions.fetchAUserDetailsUpdatePendingApproval),
      mergeMap((action) => {
        return this.userDetailService
          .fetchOpenUserDetailsTask(action.updateType, action.urid)
          .pipe(
            map((result) =>
              UserDetailsActions.fetchAUserDetailsUpdatePendingApprovalSuccess({
                hasUserDetailsUpdatePendingApproval: result.totalResults > 0,
              })
            ),
            catchError((err) => {
              return of(
                UserDetailsActions.retrieveUserDetailsOpenTaskError(err)
              );
            })
          );
      })
    )
  );

  retrieveARsInAccount$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(UserDetailsActions.retrieveARsInAccount),
      switchMap((action: { urid: string }) => {
        return this.userDetailService.getArsInAccount(action.urid).pipe(
          map((ARs) => {
            return UserDetailsActions.retrieveARsInAccountSuccess({ ARs });
          })
        );
      }),
      catchError((err) => of(UserDetailsActions.retrieveARsInAccountError(err)))
    );
  });

  retrieveUserHistory$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(UserDetailsActions.retrieveUserHistory),
      switchMap((action: { urid: string }) => {
        return this.userDetailService.getUserHistory(action.urid).pipe(
          map((userHistory) => {
            return UserDetailsActions.retrieveUserHistorySuccess({
              userHistory,
            });
          })
        );
      }),
      catchError((err) => of(UserDetailsActions.retrieveUserHistoryError(err)))
    );
  });

  retrieveUserFiles$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(UserDetailsActions.retrieveUserFiles),
      switchMap((action: { urid: string }) => {
        return this.userDetailService.getUserFiles(action.urid).pipe(
          map((userFiles) => {
            return UserDetailsActions.retrieveUserFilesSuccess({
              userFiles,
            });
          })
        );
      }),
      catchError((err) => of(UserDetailsActions.retrieveUserFilesError(err)))
    );
  });

  fetchUserFile$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(UserDetailsActions.fetchUserFile),
        mergeMap((action: { fileId: number }) => {
          return this.userDetailService.getUserFile(action.fileId).pipe(
            map((result) => {
              this.exportFileService.export(
                result.body,
                this.exportFileService.getContentDispositionFilename(
                  result.headers.get('Content-Disposition')
                )
              );
            }),
            catchError((error: HttpErrorResponse) =>
              of(
                errors({
                  errorSummary: this.apiErrorHandlingService.transform(
                    error.error
                  ),
                })
              )
            )
          );
        })
      );
    },
    { dispatch: false }
  );

  retrieveEnrolmentKeyDetails$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(UserDetailsActions.retrieveEnrolmentKeyDetails),
      switchMap((action: { urid: string }) => {
        return this.userDetailService.getEnrolmentKeyDetails(action.urid).pipe(
          map((enrolmentKeyDetails) => {
            return UserDetailsActions.retrieveEnrolmentKeyDetailsSuccess({
              enrolmentKeyDetails,
            });
          })
        );
      }),
      catchError((err) =>
        of(UserDetailsActions.retrieveEnrolmentKeyDetailsError(err))
      )
    );
  });

  navigateTo$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(UserDetailsActions.navigateTo),
        tap((action) => {
          this.router.navigate([action.route], action.extras);
        })
      );
    },
    { dispatch: false }
  );

  apiErrorToBusinessError(apiError: ApiErrorBody): { error } {
    return {
      error: apiError.errorDetails[0].message,
    };
  }
}
