import { ApiErrorHandlingService } from '@shared/services';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { select, Store } from '@ngrx/store';
import { Router } from '@angular/router';
import { catchError, map, mergeMap, tap, withLatestFrom } from 'rxjs/operators';
import { Injectable } from '@angular/core';
import { ExclusionStatusUpdateWizardService } from '@exclusion-status-update-wizard/services';
import { UpdateExclusionStatusActions } from '@registry-web/account-management/account/exclusion-status-update-wizard/actions';
import { errors } from '@registry-web/shared/shared.action';
import { of } from 'rxjs';
import {
  fetchCurrentAccountEmissionDetailsInfo,
  setCurrentAccountEmissionDetailsSuccess,
  submitUpdateSuccess,
} from '../actions/update-exclusion-status.action';
import { HttpErrorResponse } from '@angular/common/http';
import { UpdateExclusionStatusPathsModel } from '../model';
import { selectAccountId } from '../../account-details/account.selector';
import { getRouteFromArray } from '@registry-web/shared/utils/router.utils';

@Injectable()
export class UpdateExclusionStatusEffects {
  constructor(
    private apiErrorHandlingService: ApiErrorHandlingService,
    private actions$: Actions,
    private store: Store,
    private router: Router,
    private exclusionStatusUpdateWizardService: ExclusionStatusUpdateWizardService
  ) {}

  navigateTo$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(UpdateExclusionStatusActions.navigateTo),
        tap((action) => {
          this.router.navigate([action.route], action.extras);
        })
      );
    },
    { dispatch: false }
  );

  fetchCurrentAccountEmissionDetailsInfo$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(fetchCurrentAccountEmissionDetailsInfo),
      mergeMap((action) =>
        this.exclusionStatusUpdateWizardService
          .getEmissions(action.compliantEntityIdentifier)
          .pipe(
            map((result) =>
              setCurrentAccountEmissionDetailsSuccess({
                entries: result.verifiedEmissions,
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
          )
      )
    );
  });

  setExclusionYear$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(UpdateExclusionStatusActions.setExclusionYear),
      withLatestFrom(this.store.pipe(select(selectAccountId))),
      map(([action, accountIdentifier]) => {
        return UpdateExclusionStatusActions.navigateTo({
          route: `/account/${accountIdentifier}/${UpdateExclusionStatusPathsModel.BASE_PATH}/${UpdateExclusionStatusPathsModel.SELECT_EXCLUSION_STATUS}`,
          extras: {
            skipLocationChange: true,
          },
        });
      })
    );
  });

  setExclusionStatus$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(UpdateExclusionStatusActions.setExclusionStatus),
      withLatestFrom(this.store.pipe(select(selectAccountId))),
      map(([action, accountIdentifier]) => {
        return UpdateExclusionStatusActions.navigateTo({
          route: `/account/${accountIdentifier}/${UpdateExclusionStatusPathsModel.BASE_PATH}/${UpdateExclusionStatusPathsModel.CHECK_UPDATE_STATUS}`,
          extras: {
            skipLocationChange: true,
          },
        });
      })
    );
  });

  submitUpdate$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(UpdateExclusionStatusActions.submitUpdate),
      withLatestFrom(this.store.select(selectAccountId)),
      mergeMap(([action]) => {
        return this.exclusionStatusUpdateWizardService
          .submitUpdate(action.accountIdentifier, action.exclusionStatus)
          .pipe(
            map((result) => submitUpdateSuccess()),
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

  navigateToUpdateSubmitted$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(UpdateExclusionStatusActions.submitUpdateSuccess),
      withLatestFrom(this.store.pipe(select(selectAccountId))),
      map(([, accountIdentifier]) =>
        UpdateExclusionStatusActions.navigateTo({
          route: `/account/${accountIdentifier}/${UpdateExclusionStatusPathsModel.BASE_PATH}/${UpdateExclusionStatusPathsModel.REQUEST_SUBMITTED}`,
          extras: {
            skipLocationChange: true,
          },
        })
      )
    );
  });

  cancelClicked$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(UpdateExclusionStatusActions.cancelClicked),
      withLatestFrom(this.store.pipe(select(selectAccountId))),
      map(([action, accountIdentifier]) =>
        UpdateExclusionStatusActions.navigateTo({
          route: `/account/${accountIdentifier}/${UpdateExclusionStatusPathsModel.BASE_PATH}/${UpdateExclusionStatusPathsModel.CANCEL_UPDATE_EXCLUSION_STATUS}`,
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
      ofType(UpdateExclusionStatusActions.cancelUpdateExclusionStatus),
      withLatestFrom(this.store.pipe(select(selectAccountId))),
      mergeMap(([, accountIdentifier]) => [
        UpdateExclusionStatusActions.clearUpdateExclusionStatus(),
        UpdateExclusionStatusActions.navigateTo({
          route: `/account/${accountIdentifier}`,
        }),
      ])
    );
  });
}
