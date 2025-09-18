import { Actions, createEffect, ofType } from '@ngrx/effects';
import {
  catchError,
  exhaustMap,
  map,
  mergeMap,
  switchMap,
  tap,
  withLatestFrom,
} from 'rxjs/operators';
import { HttpErrorResponse } from '@angular/common/http';
import { forkJoin, of } from 'rxjs';
import { errors } from '@shared/shared.action';
import { ApiErrorHandlingService } from '@shared/services';
import { select, Store } from '@ngrx/store';
import { Injectable } from '@angular/core';
import { selectAccountId } from '@account-management/account/account-details/account.selector';
import { Router } from '@angular/router';
import {
  AircraftOperator,
  Installation,
  MaritimeOperator,
  OperatorType,
} from '@shared/model/account';
import { selectCurrentOperatorInfo } from '@operator-update/reducers';
import {
  checkIfExistsAircraftMonitoringPlanId,
  checkIfExistsMaritimeImoAndMonitorinfPlan,
  checkIfExistsMaritimeImoAndMonitoringPlanFailure,
  fetchCurrentOperatorInfo,
  setCurrentOperatorInfoSuccess,
  setNewOperatorInfoSuccess,
} from '@operator-update/actions/operator-update.actions';
import { OperatorUpdateActions } from '@operator-update/actions';
import { OperatorUpdateWizardPathsModel } from '@operator-update/model/operator-update-wizard-paths.model';
import { OperatorUpdateApiService } from '@operator-update/services';
import { ErrorDetail } from '@shared/error-summary';

@Injectable()
export class OperatorUpdateEffects {
  constructor(
    private operatorUpdateApiService: OperatorUpdateApiService,
    private apiErrorHandlingService: ApiErrorHandlingService,
    private actions$: Actions,
    private store: Store,
    private router: Router
  ) {}

  fetchOperatorInfo$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(fetchCurrentOperatorInfo),
      switchMap((action) => {
        return this.operatorUpdateApiService
          .fetchAccountOperatorInfo(action.accountId)
          .pipe(
            map((data) =>
              setCurrentOperatorInfoSuccess({
                operator: data,
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

  cancelOperatorUpdateRequest$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(OperatorUpdateActions.cancelOperatorUpdateRequest),
      withLatestFrom(this.store.pipe(select(selectAccountId))),
      mergeMap(([, accountId]) => [
        OperatorUpdateActions.clearOperatorUpdateRequest(),
        OperatorUpdateActions.navigateTo({
          route: `/account/${accountId}`,
        }),
      ])
    );
  });

  cancelClicked$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(OperatorUpdateActions.cancelClicked),
      withLatestFrom(this.store.pipe(select(selectAccountId))),
      map(([action, accountId]) =>
        OperatorUpdateActions.navigateTo({
          route: `/account/${accountId}/${OperatorUpdateWizardPathsModel.BASE_PATH}/${OperatorUpdateWizardPathsModel.CANCEL_UPDATE_REQUEST}`,
          extras: {
            queryParams: { goBackRoute: action.route },
            skipLocationChange: true,
          },
        })
      )
    );
  });

  navigateTo$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(OperatorUpdateActions.navigateTo),
        tap((action) => {
          this.router.navigate([action.route], action.extras);
        })
      );
    },
    { dispatch: false }
  );

  navigateToCheckAndSubmitPage$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(OperatorUpdateActions.setNewOperatorInfoSuccess),
      withLatestFrom(this.store.pipe(select(selectAccountId))),
      map(([action, accountId]) =>
        OperatorUpdateActions.navigateTo({
          route: `/account/${accountId}/${OperatorUpdateWizardPathsModel.BASE_PATH}/${OperatorUpdateWizardPathsModel.CHECK_UPDATE_REQUEST}`,
          extras: {
            skipLocationChange: true,
          },
        })
      )
    );
  });

  submitUpdateRequest$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(OperatorUpdateActions.submitUpdateRequest),
      withLatestFrom(
        this.store.select(selectAccountId),
        this.store.select(selectCurrentOperatorInfo)
      ),
      exhaustMap(([action, accountId, currentOperatorInfo]) => {
        return this.operatorUpdateApiService
          .submitRequest(accountId, action.operatorUpdate, currentOperatorInfo)
          .pipe(
            map((data) => {
              return OperatorUpdateActions.submitUpdateRequestSuccess({
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
      ofType(OperatorUpdateActions.submitUpdateRequestSuccess),
      withLatestFrom(this.store.pipe(select(selectAccountId))),
      map(([, accountId]) =>
        OperatorUpdateActions.navigateTo({
          route: `/account/${accountId}/${OperatorUpdateWizardPathsModel.BASE_PATH}/${OperatorUpdateWizardPathsModel.REQUEST_SUBMITTED}`,
          extras: {
            skipLocationChange: true,
          },
        })
      )
    );
  });

  fetchExistsInstallationPermiId$ = createEffect(() =>
    this.actions$.pipe(
      ofType(OperatorUpdateActions.checkIfExistsInstallationPermitId),
      withLatestFrom(this.store.pipe(select(selectCurrentOperatorInfo))),
      switchMap(([action, currentOperatorInfo]) => {
        const currentInstallation = currentOperatorInfo as Installation;
        return this.operatorUpdateApiService
          .fetchExistsInstallationPermitId(
            action.operator.permit.id,
            currentInstallation?.permit?.id
          )
          .pipe(
            map((data) =>
              setNewOperatorInfoSuccess({
                operator: action.operator,
              })
            ),
            catchError((httpError: HttpErrorResponse) =>
              of(
                errors({
                  errorSummary: this.apiErrorHandlingService.transform(
                    httpError.error
                  ),
                })
              )
            )
          );
      })
    )
  );

  fetchExistsAircraftMonitoringPlan$ = createEffect(() =>
    this.actions$.pipe(
      ofType(checkIfExistsAircraftMonitoringPlanId),
      withLatestFrom(this.store.pipe(select(selectCurrentOperatorInfo))),
      switchMap(([action, currentOperatorInfo]) => {
        const currentAircraft = currentOperatorInfo as AircraftOperator;
        return this.operatorUpdateApiService
          .fetchExistsMonitoringPlanId(
            action.operator?.monitoringPlan.id,
            currentAircraft?.monitoringPlan?.id,
            OperatorType.AIRCRAFT_OPERATOR
          )
          .pipe(
            map((data) =>
              setNewOperatorInfoSuccess({
                operator: action.operator,
              })
            ),
            catchError((httpError: HttpErrorResponse) =>
              of(
                errors({
                  errorSummary: this.apiErrorHandlingService.transform(
                    httpError.error
                  ),
                })
              )
            )
          );
      })
    )
  );

  fetchExistsMaritimeMonitoringPlanAndImo$ = createEffect(() =>
    this.actions$.pipe(
      ofType(checkIfExistsMaritimeImoAndMonitorinfPlan),
      withLatestFrom(this.store.pipe(select(selectCurrentOperatorInfo))),
      switchMap(([action, currentOperatorInfo]) => {
        return forkJoin({
          monitoringPlan: this.operatorUpdateApiService
            .fetchExistsMonitoringPlanId(
              action.operator?.monitoringPlan.id,
              (currentOperatorInfo as MaritimeOperator)?.monitoringPlan?.id,
              OperatorType.MARITIME_OPERATOR
            )
            .pipe(
              catchError((httpError: HttpErrorResponse) => of(httpError.error))
            ),
          imo: this.operatorUpdateApiService
            .fetchExistsImo(
              action.operator?.imo,
              (currentOperatorInfo as MaritimeOperator)?.imo
            )
            .pipe(
              catchError((httpError: HttpErrorResponse) => of(httpError.error))
            ),
        }).pipe(
          map(({ monitoringPlan, imo }) => {
            const errors: ErrorDetail[] = [];

            if (monitoringPlan && typeof monitoringPlan !== 'boolean') {
              errors.push(...monitoringPlan.errorDetails);
            }

            if (imo && typeof imo !== 'boolean') {
              errors.push(...imo.errorDetails);
            }

            if (errors.length > 0) {
              return checkIfExistsMaritimeImoAndMonitoringPlanFailure({
                errorSummaries: errors,
              });
            }
            return setNewOperatorInfoSuccess({
              operator: action.operator,
            });
          }),
          catchError((httpError) =>
            of(
              errors({
                errorSummary: this.apiErrorHandlingService.transform(
                  httpError.error
                ),
              })
            )
          )
        );
      })
    )
  );

  fetchExistsImoAndMonitoringPlanFailure$ = createEffect(() =>
    this.actions$.pipe(
      ofType(checkIfExistsMaritimeImoAndMonitoringPlanFailure),
      switchMap((httpError) => {
        return of(
          errors({
            errorSummary: this.apiErrorHandlingService.transform({
              errorDetails: httpError.errorSummaries,
            }),
          })
        );
      })
    )
  );
}
