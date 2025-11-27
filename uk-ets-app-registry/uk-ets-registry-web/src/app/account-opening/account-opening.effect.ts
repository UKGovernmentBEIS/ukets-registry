import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { concatLatestFrom } from '@ngrx/operators';
import {
  catchError,
  exhaustMap,
  map,
  mergeMap,
  switchMap,
  tap,
  withLatestFrom,
} from 'rxjs/operators';
import {
  AccountOpeningActionTypes,
  completeRequest,
  CreateAccount,
  fetchAccountOpeningSummaryFile,
} from './account-opening.actions';
import {
  AccountOpeningService,
  AccountRepresentation,
} from './account-opening.service';
import { Store } from '@ngrx/store';
import { errors } from '@shared/shared.action';
import { MainWizardRoutes } from './main-wizard.routes';
import {
  AccountHolderActionTypes,
  FetchByID,
  FetchList,
  populateAccountHolder,
  populateAccountHolderContactInfo,
  populateAccountHolderList,
} from './account-holder/account-holder.actions';
import {
  AccountHolderService,
  FetchOneRequestRepresentation,
} from './account-holder/account-holder.service';
import { AccountHolderWizardRoutes } from './account-holder/account-holder-wizard-properties';
import {
  AccountHolderContactActionTypes,
  FetchAccountHolderContacts,
} from '@account-holder-contact/account-holder-contact.actions';
import { AccountHolderContactService } from '@account-holder-contact/account-holder-contact.service';
import { AccountOpeningOperatorActions } from '@account-opening/operator/actions';
import { HttpErrorResponse } from '@angular/common/http';
import { forkJoin, of, throwError } from 'rxjs';
import { OperatorWizardRoutes } from '@account-opening/operator/operator-wizard-properties';
import {
  selectAccountOpening,
  selectRequestID,
  selectTaskType,
  selectAccountDetailsSameBillingAddress,
} from '@account-opening/account-opening.selector';
import { OperatorService } from '@shared/services/operator-service';
import { ApiErrorHandlingService } from '@shared/services';
import { InstallationTransfer, OperatorType } from '@shared/model/account';
import { selectInitialPermitId } from '@account-opening/operator/operator.selector';
import { RequestType } from '@registry-web/task-management/model';
import { TaskService } from '@registry-web/shared/services/task-service';
import { ExportFileService } from '@registry-web/shared/export-file/export-file.service';
import { ErrorDetail } from '@shared/error-summary';

@Injectable()
export class AccountOpeningEffects {
  // Use of exhaustMap to skip requests while the previous one is being executed
  // https://rxjs.dev/api/operators/exhaustMap
  createAccount$ = createEffect(() => {
    return this.actions$.pipe(
      ofType<CreateAccount>(AccountOpeningActionTypes.CREATE_ACCOUNT),
      concatLatestFrom(() => [
        this.store.select(selectAccountOpening),
        this.store.select(selectInitialPermitId),
        this.store.select(selectAccountDetailsSameBillingAddress),
      ]),
      exhaustMap(
        ([
          action,
          accountOpeningState,
          initialPermitId,
          accountDetailsSameBillingAddress,
        ]) => {
          const accountDTO = new AccountRepresentation();
          accountDTO.accountType = accountOpeningState.accountType;
          accountDTO.accountHolder = accountOpeningState.accountHolder;
          accountDTO.accountHolderContactInfo =
            accountOpeningState.accountHolderContactInfo;
          accountDTO.accountDetails = {
            ...accountOpeningState.accountDetails,
            accountDetailsSameBillingAddress,
          };
          accountDTO.trustedAccountListRules =
            accountOpeningState.trustedAccountList;
          accountDTO.operator = accountOpeningState.operator;
          accountDTO.authorisedRepresentatives =
            accountOpeningState.authorisedRepresentatives;
          accountDTO.accountDetailsSameBillingAddress =
            accountDetailsSameBillingAddress;
          if (
            OperatorType.INSTALLATION_TRANSFER ===
            accountOpeningState?.operator?.type
          ) {
            const permitIdUnchanged =
              accountOpeningState?.operator?.['permit']?.id === initialPermitId;
            const operator = {
              ...accountOpeningState?.operator,
              permit: {
                id: accountOpeningState?.operator?.['permit']?.id,
                permitIdUnchanged,
              },
            };
            accountDTO.operator = operator;
            accountDTO.installationToBeTransferred =
              accountOpeningState.installationToBeTransferred;
          }
          return this.accountOpeningService.createAccount(accountDTO).pipe(
            map((result) => {
              this.router.navigate([MainWizardRoutes.CONFIRMATION], {
                skipLocationChange: true,
              });
              return completeRequest({
                requestID: result.requestId,
                taskType: RequestType[result.type],
              });
            }),
            catchError((httpError: HttpErrorResponse) => {
              console.log(httpError);
              return of(
                errors({
                  errorSummary: this.apiErrorHandlingService.transform(
                    httpError.error
                  ),
                })
              );
            })
          );
        }
      )
    );
  });

  fetchAccountHolderList$ = createEffect(() =>
    this.actions$.pipe(
      ofType<FetchList>(AccountHolderActionTypes.FETCH_ACCOUNT_HOLDER_LIST),
      mergeMap(({ holderType }) =>
        this.accountHolderService.fetchList(holderType).pipe(
          map((result) => populateAccountHolderList({ list: result })),
          catchError((httpError: HttpErrorResponse) =>
            of(
              errors({
                errorSummary: this.apiErrorHandlingService.transform(
                  httpError.error
                ),
              })
            )
          )
        )
      )
    )
  );

  fetchAccountHolderById$ = createEffect(() =>
    this.actions$.pipe(
      ofType<FetchByID>(AccountHolderActionTypes.FETCH_ACCOUNT_HOLDER_BY_ID),
      mergeMap((action) => {
        const request = new FetchOneRequestRepresentation();
        request.identifier = action.id;
        return this.accountHolderService.fetchOne(request).pipe(
          map((result) => {
            this.router.navigate([AccountHolderWizardRoutes.OVERVIEW], {
              skipLocationChange: true,
            });
            return populateAccountHolder({ accountHolder: result });
          }),
          catchError((httpError: HttpErrorResponse) => {
            return of(
              errors({
                errorSummary: this.apiErrorHandlingService.transform(
                  httpError.error
                ),
              })
            );
          })
        );
      })
    )
  );

  fetchExistsMonitoringPlan$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AccountOpeningOperatorActions.fetchExistsMonitoringPlan),
      switchMap((action) => {
        return this.operatorService
          .fetchExistsAircraftMonitoringPlanId(
            action.operator?.monitoringPlan.id
          )
          .pipe(
            map((data) =>
              AccountOpeningOperatorActions.fetchExistsMonitoringPlanSuccess({
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

  fetchExistsMonitoringPlanAndImo$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AccountOpeningOperatorActions.fetchExistsImoAndMonitoringPlan),
      switchMap((action) => {
        return forkJoin({
          monitoringPlan: this.operatorService
            .fetchExistsMaritimeMonitoringPlanId(
              action.operator?.monitoringPlan.id
            )
            .pipe(
              catchError((httpError: HttpErrorResponse) => of(httpError.error))
            ),
          imo: this.operatorService
            .fetchExistsImo(action.operator.imo)
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
              return AccountOpeningOperatorActions.fetchExistsMonitoringPlanAndImoFailure(
                {
                  errorSummaries: errors,
                }
              );
            }

            return AccountOpeningOperatorActions.fetchExistsMonitoringPlanAndImoSuccess(
              {
                operator: action.operator,
              }
            );
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

  fetchExistsImoSuccess$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(
        AccountOpeningOperatorActions.fetchExistsMonitoringPlanAndImoSuccess
      ),
      map((action) =>
        AccountOpeningOperatorActions.setOperator({
          operator: action.operator,
        })
      )
    );
  });

  fetchExistsImoAndMonitoringPlanFailure$ = createEffect(() =>
    this.actions$.pipe(
      ofType(
        AccountOpeningOperatorActions.fetchExistsMonitoringPlanAndImoFailure
      ),
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

  validateOperator = createEffect(() =>
    this.actions$.pipe(
      ofType(AccountOpeningOperatorActions.validateInstallationTransfer),
      withLatestFrom(this.store.select(selectInitialPermitId)),
      switchMap(([action, permitID]) => {
        const isPermitUnchanged =
          action.installationTransfer.permit.id === permitID;
        const acquiringAccountHolderIdentifier =
          action.acquiringAccountHolderIdentifier;
        const installation: InstallationTransfer = {
          ...action.installationTransfer,
          permit: {
            ...action.installationTransfer.permit,
            permitIdUnchanged: isPermitUnchanged,
          },
          acquiringAccountHolderIdentifier: acquiringAccountHolderIdentifier,
        };
        return this.operatorService
          .validateInstallationTransfer(installation)
          .pipe(
            map((data) =>
              AccountOpeningOperatorActions.validateInstallationTransferSuccess(
                { installationToBeTransferred: data }
              )
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

  navigateToOperatorOverviewPage$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(
          AccountOpeningOperatorActions.fetchExistsMonitoringPlanSuccess,
          AccountOpeningOperatorActions.fetchExistsMonitoringPlanAndImoSuccess,
          AccountOpeningOperatorActions.fetchExistsInstallationPermitIdSuccess,
          AccountOpeningOperatorActions.validateInstallationTransferSuccess
        ),
        tap((action) => {
          this.router.navigate([OperatorWizardRoutes.OVERVIEW], {
            skipLocationChange: true,
          });
        })
      );
    },
    { dispatch: false }
  );

  navigateToMainWizard$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(AccountOpeningOperatorActions.navigateToMainWizard),
        tap((action) => {
          this.router.navigate([MainWizardRoutes.TASK_LIST], {
            skipLocationChange: true,
          });
        })
      );
    },
    { dispatch: false }
  );

  fetchMonitoringPlanIDsSuccess$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AccountOpeningOperatorActions.fetchExistsMonitoringPlanSuccess),
      map((action) =>
        AccountOpeningOperatorActions.setOperator({
          operator: action.operator,
        })
      )
    );
  });

  fetchAccountHolderContacts = createEffect(() =>
    this.actions$.pipe(
      ofType<FetchAccountHolderContacts>(
        AccountHolderContactActionTypes.FETCH_ACCOUNT_HOLDER_CONTACTS
      ),
      mergeMap((action) => {
        return this.accountHolderContactService
          .fetchAccountHolderContacts(action.accountHolderId)
          .pipe(
            map((result) => {
              return populateAccountHolderContactInfo({
                accountHolderContactInfo: result,
              });
            })
          );
      })
    )
  );

  fetchExistsInstallationPermiId$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AccountOpeningOperatorActions.fetchExistsInstallationPermiId),
      switchMap((action) => {
        return this.operatorService
          .fetchExistsInstallationPermitId(action.installation.permit.id)
          .pipe(
            map((data) =>
              AccountOpeningOperatorActions.fetchExistsInstallationPermitIdSuccess(
                {
                  installation: action.installation,
                }
              )
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

  fetchInstallationPermitIdSuccess$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(
        AccountOpeningOperatorActions.fetchExistsInstallationPermitIdSuccess
      ),
      map((action) =>
        AccountOpeningOperatorActions.setOperator({
          operator: action.installation,
        })
      )
    );
  });

  fetchTaskFile$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(fetchAccountOpeningSummaryFile),
        concatLatestFrom(() => [
          this.store.select(selectRequestID),
          this.store.select(selectTaskType),
        ]),
        mergeMap(([, requestID, taskType]) => {
          return this.taskService
            .fetchRequestedFile({
              taskRequestId: String(requestID),
              taskType: taskType,
            })
            .pipe(
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

  constructor(
    private router: Router,
    private actions$: Actions,
    private accountOpeningService: AccountOpeningService,
    private accountHolderService: AccountHolderService,
    private accountHolderContactService: AccountHolderContactService,
    private operatorService: OperatorService,
    private store: Store,
    private apiErrorHandlingService: ApiErrorHandlingService,
    private taskService: TaskService,
    private exportFileService: ExportFileService
  ) {}
}
