import { Injectable } from '@angular/core';
import { Actions, concatLatestFrom, createEffect, ofType } from '@ngrx/effects';
import * as AccountDetailsActions from './account.actions';
import {
  cancelUpdateAccountDetails,
  clearAccountAllocation,
  fetchAccountHistory,
  fetchAccountHistorySuccess,
  loadAccountDetails,
  loadAccountHoldings,
  navigateToUpdateAccountAllocationWizard,
  prepareAccountAllocation,
  resetAccountHistory,
  resetAccountHolderFiles,
  retrieveAccountHolderFiles,
  submitAccountDetailsUpdate,
  submitAccountDetailsUpdateSuccess,
} from './account.actions';
import * as AccountTransactionsActions from './transactions/account-transactions.actions';
import {
  catchError,
  concatMap,
  exhaustMap,
  filter,
  map,
  mergeMap,
  switchMap,
  tap,
  withLatestFrom,
} from 'rxjs/operators';
import { AccountApiService } from '@account-management/service/account-api.service';
import { select, Store } from '@ngrx/store';
import { HttpErrorResponse } from '@angular/common/http';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { canGoBack, errors, navigateTo } from '@shared/shared.action';
import { ApiErrorHandlingService } from '@shared/services';
import { of } from 'rxjs';
import { MenuItemEnum } from '@registry-web/account-management/account/account-details/model/account-side-menu.model';
import { AllocationStatusRoutePathsModel } from '@allocation-status/model/allocation-status-route-paths.model';
import { AccountAllocationService } from '@account-management/service/account-allocation.service';
import { resetAllocationStatusState } from '@allocation-status/actions/allocation-status.actions';
import { ExportFileService } from '@shared/export-file/export-file.service';
import {
  selectAccount,
  selectAccountDetailsSameBillingAddress,
  selectTransactionListPageParameters,
  selectUpdateAccountDetails,
} from '@account-management/account/account-details/account.selector';
import { Router } from '@angular/router';
import {
  AccountActionError,
  AccountActionErrorResponse,
  apiErrorToBusinessError,
} from './model';
import { EmissionsSurrendersActions } from './store/actions';
import { AccountComplianceService } from './services';
import * as TrustedAccountActions from '@account-management/account/account-details/trusted-accounts/trusted-accounts.actions';
import { isAdmin } from '@registry-web/auth/auth.selector';
import { createReportRequestSuccess } from '@registry-web/reports/actions';
import * as SharedActions from '@shared/shared.action';
import { BusinessRules } from '@shared/business-rules';
import * as NotesActions from '@registry-web/notes/store/notes.actions';
import { NoteType } from '@registry-web/shared/model';

@Injectable()
export class AccountEffect {
  constructor(
    private accountService: AccountApiService,
    private allocationService: AccountAllocationService,
    private complianceService: AccountComplianceService,
    private actions$: Actions,
    private store: Store,
    private router: Router,
    private apiErrorHandlingService: ApiErrorHandlingService,
    private exportFileService: ExportFileService
  ) {}

  prepareNavigationToAccount$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AccountDetailsActions.prepareNavigationToAccount),
      concatMap((action: { accountId: string }) => {
        return [
          AccountDetailsActions.fetchAccount(action),
          AccountDetailsActions.fetchAccountHoldings(action),
        ];
      })
    );
  });

  fetchAccountDetails$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AccountDetailsActions.fetchAccount),
      switchMap((action: { accountId: string }) =>
        this.accountService.fetchAccount(action.accountId).pipe(
          mergeMap((result) => [
            loadAccountDetails({
              account: result,
              accountId: action.accountId,
            }),
            resetAccountHistory(),
            resetAccountHolderFiles(),
            retrieveAccountHolderFiles({
              accountIdentifier: action.accountId,
            }),
            fetchAccountHistory({
              identifier: action.accountId,
            }),
            clearAccountAllocation(),
            prepareAccountAllocation({ accountId: action.accountId }),
            EmissionsSurrendersActions.resetEmissionsAndSurrendersStatusState(),
            EmissionsSurrendersActions.fetchComplianceOverview({
              accountIdentifier: +action.accountId,
            }),
            EmissionsSurrendersActions.fetchVerifiedEmissions({
              compliantEntityId: result.operator?.identifier,
            }),
            EmissionsSurrendersActions.fetchComplianceStatusHistory({
              compliantEntityId: result.operator?.identifier,
            }),
            EmissionsSurrendersActions.fetchComplianceHistoryAndComments({
              compliantEntityId: +result.operator?.identifier,
            }),
            AccountDetailsActions.setSideMenu(),
            AccountDetailsActions.fetchAccountOperatorPendingApproval({
              account: result,
            }),
            TrustedAccountActions.fetchTAL(),
            NotesActions.fetchNotes({
              domainId: action.accountId,
            }),
          ]),
          catchError((httpError) => this.handleHttpError(httpError))
        )
      )
    )
  );

  fetchOperatorUpdatePendingApproval$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AccountDetailsActions.fetchAccountOperatorPendingApproval),
      mergeMap((action) => {
        return this.accountService.fetchOpenOperatorTasks(action.account).pipe(
          map((result) =>
            AccountDetailsActions.fetchAccountOperatorPendingApprovalSuccess({
              hasOperatorUpdatePendingApproval: result.totalResults > 0,
            })
          ),
          catchError((httpError) => this.handleHttpError(httpError))
        );
      })
    )
  );

  fetchAccountHistory$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AccountDetailsActions.fetchAccountHistory),
      mergeMap((action: { identifier: string }) =>
        this.accountService.accountHistory(action.identifier).pipe(
          map((results) => fetchAccountHistorySuccess({ results })),
          catchError((httpError) => this.handleHttpError(httpError))
        )
      )
    )
  );

  fetchAccountHoldings$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AccountDetailsActions.fetchAccountHoldings),
      mergeMap((action: { accountId: string }) => {
        return this.accountService.fetchAccountHoldings(action.accountId).pipe(
          switchMap((result) => [
            loadAccountHoldings({ accountHoldingsResult: result }),
            AccountDetailsActions.setSideMenu(),
          ]),
          catchError((httpError) => this.handleHttpError(httpError))
        );
      })
    );
  });

  fetchAccountTransactions$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AccountTransactionsActions.fetchAccountTransactions),
      concatLatestFrom(() =>
        this.store.select(selectTransactionListPageParameters)
      ),
      concatMap(([action, storedPageParameters]) => {
        const pageParameters = action.loadPageParametersFromState
          ? storedPageParameters
          : action.pageParameters;
        return this.accountService
          .fetchAccountTransactions(
            action.fullIdentifier,
            pageParameters,
            action.sortParameters
          )
          .pipe(
            switchMap((result) => [
              AccountTransactionsActions.loadAccountTransactions({
                transactions: result,
                pageParameters: pageParameters,
              }),
              AccountDetailsActions.setSideMenu(),
            ]),
            catchError((httpError) => this.handleHttpError(httpError))
          );
      })
    );
  });

  fetchAccountTransactionsReport$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AccountTransactionsActions.fetchAccountTransactionsReport),
      mergeMap((action: { fullIdentifier: string }) => {
        return this.accountService
          .fetchAccountTransactionsReport(action.fullIdentifier)
          .pipe(
            switchMap((reportId) => [
              createReportRequestSuccess({ response: { reportId } }),
            ]),
            catchError((httpError) => this.handleHttpError(httpError))
          );
      })
    );
  });

  fetchAccountAllocation$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AccountDetailsActions.fetchAccountAllocation),
      switchMap((action: { accountId: string }) => {
        return this.allocationService.fetchAllocation(action.accountId).pipe(
          map((result) => {
            return AccountDetailsActions.fetchAccountAllocationSuccess({
              allocation: result,
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

  fetchComplianceOverview$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(EmissionsSurrendersActions.fetchComplianceOverview),
      switchMap((action: { accountIdentifier: number }) => {
        return this.complianceService
          .fetchAccountComplianceOverview(action.accountIdentifier)
          .pipe(
            map((result) => {
              return EmissionsSurrendersActions.fetchComplianceOverviewSuccess({
                result,
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

  fetchVerifiedEmissions$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(EmissionsSurrendersActions.fetchVerifiedEmissions),
      filter((action) => !!action.compliantEntityId),
      switchMap((action: { compliantEntityId: number }) => {
        return this.complianceService
          .fetchAccountVerifiedEmissions(action.compliantEntityId)
          .pipe(
            map((results) => {
              return EmissionsSurrendersActions.fetchVerifiedEmissionsSuccess({
                results,
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

  fetchComplianceStatusHistory$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(EmissionsSurrendersActions.fetchComplianceStatusHistory),
      filter((action) => !!action.compliantEntityId),
      switchMap((action: { compliantEntityId: number }) => {
        return this.complianceService
          .fetchAccountComplianceStatusHistory(action.compliantEntityId)
          .pipe(
            map((results) => {
              return EmissionsSurrendersActions.fetchComplianceStatusHistorySuccess(
                {
                  results,
                }
              );
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

  fetchAccountComplianceHistoryAndComments$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(EmissionsSurrendersActions.fetchComplianceHistoryAndComments),
      withLatestFrom(this.store.pipe(select(isAdmin))),
      filter(([action, isAdmin]) => !!action.compliantEntityId && isAdmin),
      switchMap(([action, isAdmin]) => {
        return this.complianceService
          .fetchAccountComplianceHistoryAndComments(action.compliantEntityId)
          .pipe(
            map((results) => {
              return EmissionsSurrendersActions.fetchComplianceHistoryAndCommentsSuccess(
                {
                  results,
                }
              );
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

  navigateToUpdateAccountAllocationWizard$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(navigateToUpdateAccountAllocationWizard),
      concatMap((action) => [
        canGoBack({
          goBackRoute: `/account/${action.accountId}`,
          extras: {
            queryParams: {
              selectedSideMenu: MenuItemEnum.ALLOCATION,
            },
          },
        }),
        navigateTo({
          route: `/account/${action.accountId}/${AllocationStatusRoutePathsModel.ALLOCATION_STATUS}`,
        }),
      ])
    );
  });

  prepareAccountAllocation$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(prepareAccountAllocation),
      concatMap((action) => [
        resetAllocationStatusState(),
        AccountDetailsActions.fetchAccountAllocation(action),
      ])
    );
  });

  retrieveAccountHolderFiles$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AccountDetailsActions.retrieveAccountHolderFiles),
      switchMap((action: { accountIdentifier: string }) => {
        return this.accountService
          .getAccountHolderFiles(action.accountIdentifier)
          .pipe(
            map((accountHolderFiles) => {
              return AccountDetailsActions.retrieveAccountHolderFilesSuccess({
                accountHolderFiles,
              });
            })
          );
      }),
      catchError((err) =>
        of(AccountDetailsActions.retrieveAccountHolderFilesError(err))
      )
    );
  });

  fetchAccountHolderFile$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(AccountDetailsActions.fetchAccountHolderFile),
        mergeMap((action: { fileId: number }) => {
          return this.accountService.getAccountHolderFile(action.fileId).pipe(
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

  navigateToAccountDetails$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(
          AccountDetailsActions.cancelUpdateAccountDetailsConfirm,
          AccountDetailsActions.cancelExcludeBillingConfirm
        ),
        concatLatestFrom(() => this.store.select(selectAccount)),
        tap(([, account]) =>
          this.router.navigate([`/account/${account.identifier}`], {
            skipLocationChange: true,
          })
        )
      );
    },
    { dispatch: false }
  );

  navigateToExcludeBillingCancel$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(AccountDetailsActions.cancelExcludeBilling),
        concatLatestFrom(() => this.store.select(selectAccount)),
        tap(([, account]) =>
          this.router.navigate(
            [`/account/${account.identifier}/cancel-exclude-billing`],
            {
              skipLocationChange: true,
            }
          )
        )
      );
    },
    { dispatch: false }
  );

  navigateToExcludeBillingSuccess$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(AccountDetailsActions.submitExcludeBillingSuccess),
        concatLatestFrom(() => this.store.select(selectAccount)),
        tap(([, account]) =>
          this.router.navigate(
            [`/account/${account.identifier}/exclude-billing-success`],
            {
              skipLocationChange: true,
            }
          )
        )
      );
    },
    { dispatch: false }
  );

  navigateToAccountDetailsCancel$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(cancelUpdateAccountDetails),
        withLatestFrom(this.store.pipe(select(selectAccount))),
        tap(([action, account]) =>
          this.router.navigate(
            [`/account/${account.identifier}/cancel-update`],
            {
              skipLocationChange: true,
              queryParams: {
                goBackRoute: action.currentRoute,
              },
            }
          )
        )
      ),
    { dispatch: false }
  );

  submitAccountDetailsUpdate$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(submitAccountDetailsUpdate),
      concatLatestFrom(() => [
        this.store.select(selectAccount),
        this.store.select(selectUpdateAccountDetails),
        this.store.select(selectAccountDetailsSameBillingAddress),
      ]),
      exhaustMap(
        ([, account, updatedDetails, accountDetailsSameBillingAddress]) => {
          return this.accountService
            .updateDetails(account.identifier, {
              ...updatedDetails,
              accountDetailsSameBillingAddress,
            })
            .pipe(
              map((account) => submitAccountDetailsUpdateSuccess({ account })),
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
        }
      )
    );
  });

  submitExcludeBilling$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AccountDetailsActions.submitExcludeBilling),
      concatLatestFrom(() => this.store.select(selectAccount)),
      exhaustMap(([action, account]) =>
        this.accountService
          .excludeBilling(account.identifier, action.remarks)
          .pipe(
            map((response) =>
              AccountDetailsActions.submitExcludeBillingSuccess({ response })
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

  submitIncludeBilling$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AccountDetailsActions.submitIncludeBilling),
      concatLatestFrom(() => this.store.select(selectAccount)),
      exhaustMap(([, account]) =>
        this.accountService.includeBilling(account.identifier).pipe(
          map((response) =>
            AccountDetailsActions.submitIncludeBillingSuccess({ response })
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

  reloadAccountAfterIncludeBillingSuccess$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AccountDetailsActions.submitIncludeBillingSuccess),
      concatLatestFrom(() => this.store.select(selectAccount)),
      map(([, account]) =>
        AccountDetailsActions.fetchAccount({
          accountId: String(account.identifier),
        })
      )
    );
  });

  navigateToAccountDetailsUpdated$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(submitAccountDetailsUpdateSuccess),
        withLatestFrom(this.store.pipe(select(selectAccount))),
        tap(([, account]) =>
          this.router.navigate([`/account/${account.identifier}/updated`], {
            skipLocationChange: true,
          })
        )
      ),
    { dispatch: false }
  );

  private handleHttpError(httpError: HttpErrorResponse) {
    if (httpError.status === 400) {
      const errorDetails = [];
      const errorResponse: AccountActionErrorResponse = apiErrorToBusinessError(
        httpError.error
      );
      const errorMsg: string = errorResponse.message;
      if (errorMsg) {
        errorDetails.push(new ErrorDetail(null, errorMsg));
      }
      errorDetails.push(this.buildErrorDetail(errorResponse.error));
      return of(errors({ errorSummary: { errors: errorDetails } }));
    }
    const errorSummary = this.apiErrorHandlingService.transform(
      httpError.error
    );
    const errPath = this.getPathBasedOnError(errorSummary);
    this.store.dispatch(
      SharedActions.setGoBackToErrorBasedPath({
        goBackToErrorBasedPath: errPath,
      })
    );

    return of(
      errors({
        errorSummary: errorSummary,
      })
    );
  }

  buildErrorDetail(accountActionError: AccountActionError): ErrorDetail {
    const message = accountActionError.message;

    return new ErrorDetail(null, message);
  }

  getPathBasedOnError(errorSummary: ErrorSummary): string {
    if (errorSummary.errors.length > 0) {
      if (
        errorSummary.errors[0].errorMessage ===
        BusinessRules.ARsCanViewAccountWhenAccountHasSpecificStatus
      ) {
        return '/account-list';
      }
    }
    return null;
  }
}
