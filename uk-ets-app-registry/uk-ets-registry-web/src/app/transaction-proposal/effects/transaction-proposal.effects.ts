import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { concatLatestFrom } from '@ngrx/operators';
import {
  catchError,
  exhaustMap,
  filter,
  map,
  mergeMap,
  switchMap,
  withLatestFrom,
} from 'rxjs/operators';
import { forkJoin, of } from 'rxjs';
import { select, Store } from '@ngrx/store';
import { HttpErrorResponse } from '@angular/common/http';
import {
  selectAllocationType,
  selectCalculatedExcessAmountPerAllocationAccount,
  selectedTransactionBlocks,
  selectExcessAmount,
  selectITLNotification,
  selectOtpCode,
  selectReturnExcessAllocationTransactionSummary,
  selectReturnExcessAllocationType,
  selectTotalQuantityOfSelectedTransactionBlocks,
  selectTransactionSummary,
  selectTransactionType,
  selectUserDefinedAccountIdentifier,
} from '@transaction-proposal/reducers';
import {
  SelectUnitTypesActions,
  SpecifyAcquiringAccountActions,
  TransactionProposalActions,
} from '@transaction-proposal/actions';
import { TransactionProposalService } from '@transaction-proposal/services';
import {
  fetchAllowedTransactionTypesSuccess,
  loadAllowedTransactionTypes,
} from '../actions/transaction-proposal.actions';
import { errors } from '@shared/shared.action';
import { selectAccountId } from '@account-management/account/account-details/account.selector';
import { ApiErrorHandlingService } from '@shared/services';
import { SigningActions } from '@signing/actions';
import { selectSignature } from '@signing/reducers/signing.selectors';
import { TransactionDetailsActions } from '@transaction-management/transaction-details/actions';
import { TransactionType } from '@shared/model/transaction';

@Injectable()
export class TransactionProposalEffects {
  constructor(
    private transactionProposalService: TransactionProposalService,
    private apiErrorHandlingService: ApiErrorHandlingService,
    private actions$: Actions,
    private store: Store
  ) {}

  fetchLoadAndShowAllowedTransactionTypes$ = createEffect(() =>
    this.actions$.pipe(
      ofType(
        TransactionProposalActions.fetchLoadAndShowAllowedTransactionTypes
      ),
      mergeMap((action) =>
        this.transactionProposalService
          .getAllowedTransactionTypes(action.accountId)
          .pipe(
            map((data) =>
              fetchAllowedTransactionTypesSuccess({
                transactionTypes: data,
              })
            ),
            catchError((error: HttpErrorResponse) =>
              of(
                errors({
                  errorSummary: this.apiErrorHandlingService.transform(
                    error.error
                  ),
                })
              )
            )
          )
      )
    )
  );

  // noinspection JSUnusedGlobalSymbols
  fetchAllowedTransactionTypesSuccess$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TransactionProposalActions.fetchAllowedTransactionTypesSuccess),
      mergeMap((action) =>
        // TODO Remove this check and throw an error in the server side.
        action.transactionTypes.result.length > 0
          ? [
              TransactionProposalActions.clearTransactionProposal(),
              loadAllowedTransactionTypes({
                transactionTypes: action.transactionTypes,
              }),
            ]
          : [
              errors({
                errorSummary: this.apiErrorHandlingService.transform({
                  errorDetails: [
                    {
                      message:
                        'You do not hold enough units to propose a transaction.',
                    },
                  ],
                }),
              }),
            ]
      )
    );
  });

  selectUnitDone$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(SelectUnitTypesActions.setSelectedBlockSummaries),
      concatLatestFrom(() => [this.store.select(selectTransactionSummary)]),
      map(([, transactionSummary]) => {
        //Other cases apart from ExcessAllocation
        if (transactionSummary.type !== TransactionType.ExcessAllocation) {
          return SelectUnitTypesActions.validateSelectedBlockSummaries({
            transactionSummary,
          });
        } else {
          //Case for ReturnExcessAllocation
          return SelectUnitTypesActions.setCalculatedReturnExcessAllocationQuantitiesAndType();
        }
      })
    );
  });

  calculatedReturnExcessAllocationQuantitiesAndType$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(
        SelectUnitTypesActions.setCalculatedReturnExcessAllocationQuantitiesAndType
      ),
      concatLatestFrom(() => [
        this.store.select(selectExcessAmount),
        this.store.select(selectTotalQuantityOfSelectedTransactionBlocks),
        this.store.select(selectReturnExcessAllocationType),
      ]),
      map(([, excessAmount, quantity, returnExcessAllocationType]) => {
        if (
          excessAmount.returnToAllocationAccountAmount > 0 &&
          excessAmount.returnToNewEntrantsReserveAccount > 0 &&
          quantity > excessAmount.returnToNewEntrantsReserveAccount
        ) {
          return SelectUnitTypesActions.setReturnExcessAmountsAndType({
            selectedExcessAmounts: {
              returnToAllocationAccountAmount:
                quantity - excessAmount.returnToNewEntrantsReserveAccount,
              returnToNewEntrantsReserveAccount:
                excessAmount.returnToNewEntrantsReserveAccount,
            },
            returnExcessAllocationType: 'NAT_AND_NER',
          });
        } else if (excessAmount.returnToNewEntrantsReserveAccount > 0) {
          return SelectUnitTypesActions.setReturnExcessAmountsAndType({
            selectedExcessAmounts: {
              returnToAllocationAccountAmount: 0,
              returnToNewEntrantsReserveAccount: quantity,
            },
            returnExcessAllocationType: 'NER',
          });
        } else {
          return SelectUnitTypesActions.setReturnExcessAmountsAndType({
            selectedExcessAmounts: {
              returnToAllocationAccountAmount: quantity,
              returnToNewEntrantsReserveAccount: 0,
            },
            returnExcessAllocationType: returnExcessAllocationType,
          });
        }
      })
    );
  });

  setSelectedExcessAmounts$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(SelectUnitTypesActions.setReturnExcessAmountsAndType),
      concatLatestFrom(() => [
        this.store.select(selectReturnExcessAllocationType),
        this.store.select(selectTransactionSummary),
        this.store.select(selectReturnExcessAllocationTransactionSummary),
      ]),
      map(
        ([
          ,
          returnExcessAllocationType,
          transactionSummary,
          returnExcessAllocationTransactionSummary,
        ]) => {
          if (returnExcessAllocationType != 'NAT_AND_NER') {
            return SelectUnitTypesActions.validateSelectedBlockSummaries({
              transactionSummary,
            });
          } else {
            return SelectUnitTypesActions.validateSelectedBlockSummariesNatAndNer(
              {
                returnExcessAllocationTransactionSummary,
              }
            );
          }
        }
      )
    );
  });

  validateSelectedBlockSummaries$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(SelectUnitTypesActions.validateSelectedBlockSummaries),
      switchMap(({ transactionSummary }) =>
        this.transactionProposalService
          .validate(transactionSummary, 'UNITS')
          .pipe(
            map((businessCheckResult) => {
              return SelectUnitTypesActions.validateSelectedBlockSummariesSuccess(
                {
                  businessCheckResult,
                }
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
          )
      )
    );
  });

  validateSelectedBlockSummariesNatAndNer$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(SelectUnitTypesActions.validateSelectedBlockSummariesNatAndNer),
      switchMap(({ returnExcessAllocationTransactionSummary }) =>
        this.transactionProposalService
          .validateNatAndNer(returnExcessAllocationTransactionSummary, 'UNITS')
          .pipe(
            map((businessCheckResult) => {
              return SelectUnitTypesActions.validateSelectedBlockSummariesNatAndNerSuccess(
                {
                  businessCheckResult,
                }
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
          )
      )
    );
  });

  selectTrustedAccountDone$ = createEffect(() =>
    this.actions$.pipe(
      ofType(
        SpecifyAcquiringAccountActions.setAcquiringAccount,
        SpecifyAcquiringAccountActions.setUserDefinedAcquiringAccount
      ),
      withLatestFrom(this.store.pipe(select(selectTransactionSummary))),
      map(([, transactionSummary]: any) =>
        SpecifyAcquiringAccountActions.validateTrustedAccount({
          transactionSummary,
        })
      )
    )
  );

  validateTrustedAccount$ = createEffect(() =>
    this.actions$.pipe(
      ofType(SpecifyAcquiringAccountActions.validateTrustedAccount),
      switchMap(({ transactionSummary }) =>
        this.transactionProposalService
          .validate(transactionSummary, 'ACCOUNT')
          .pipe(
            map((businessCheckResult) =>
              SpecifyAcquiringAccountActions.validateTrustedAccountSuccess({
                businessCheckResult,
              })
            ),
            catchError((error: HttpErrorResponse) =>
              of(
                errors({
                  errorSummary: this.apiErrorHandlingService.transform(
                    error.error
                  ),
                })
              )
            )
          )
      )
    )
  );

  validateITLNotificationId$ = createEffect(() =>
    this.actions$.pipe(
      ofType(TransactionProposalActions.validateITLNotificationId),
      switchMap(({ itlNotificationId, transactionType }) => {
        return this.transactionProposalService
          .validateITLNotificationId(itlNotificationId, transactionType)
          .pipe(
            mergeMap((itlNotification) => {
              return [
                TransactionProposalActions.validateITLNotificationSuccess({
                  itlNotification: {
                    notificationIdentifier:
                      itlNotification.notificationIdentifier,
                    quantity: itlNotification.targetValue,
                    commitPeriod: itlNotification.commitPeriod,
                    targetDate: itlNotification.targetDate,
                    projectNumber: itlNotification.projectNumber,
                    type: itlNotification.type,
                  },
                }),
                TransactionProposalActions.navigateToSelectUnitTypesAndQuantity(),
              ];
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
    )
  );

  /*
    After successful account validation enrich only if user selected a custom account
 */
  validateTrustedAccountSuccess$ = createEffect(() =>
    this.actions$.pipe(
      ofType(SpecifyAcquiringAccountActions.validateTrustedAccountSuccess),
      withLatestFrom(
        //TODO: Remove account selectors from transaction proposal (UKETS-4581)
        this.store.pipe(select(selectAccountId)),
        this.store.pipe(select(selectUserDefinedAccountIdentifier))
      ),
      filter(
        ([, , userDefinedAcquiringIdentifier]) =>
          userDefinedAcquiringIdentifier != null
      ),
      map(
        ([
          ,
          tranferringAccountIdentifier,
          userDefinedAcquiringIdentifier,
        ]: any) =>
          SpecifyAcquiringAccountActions.enrichUserDefinedAcquiringAccount({
            fullIdentifier: userDefinedAcquiringIdentifier,
            transferringAccountId: tranferringAccountIdentifier,
          })
      )
    )
  );

  populateAcquiringAccountAccountWhenSkip$ = createEffect(() =>
    this.actions$.pipe(
      ofType(SpecifyAcquiringAccountActions.populateAcquiringAccount),
      withLatestFrom(
        //TODO: Remove account selectors from transaction proposal (UKETS-4581)
        this.store.pipe(select(selectAccountId)),
        this.store.pipe(select(selectTransactionType)),
        this.store.pipe(select(selectedTransactionBlocks)),
        this.store.pipe(select(selectITLNotification)),
        this.store.pipe(select(selectAllocationType))
      ),
      switchMap(
        ([
          ,
          transferringAccountIdentifier,
          transactionType,
          transactionBlocks,
          itlNotification,
          allocationType,
        ]) =>
          this.transactionProposalService
            .populateAcquiringAccountIdentifier(
              +transferringAccountIdentifier,
              transactionType.type,
              transactionBlocks ? transactionBlocks[0]?.applicablePeriod : null,
              itlNotification?.notificationIdentifier,
              allocationType
            )
            .pipe(
              map((acquiringAccountInfo) =>
                SpecifyAcquiringAccountActions.populateAcquiringAccountSuccess({
                  acquiringAccountInfo,
                })
              ),
              catchError((error: HttpErrorResponse) =>
                of(
                  errors({
                    errorSummary: this.apiErrorHandlingService.transform(
                      error.error
                    ),
                  })
                )
              )
            )
      )
    )
  );

  populateExcessAllocationAcquiringAccountAccountsWhenSkip$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(
          SpecifyAcquiringAccountActions.populateExcessAllocationAcquiringAccounts
        ),
        concatLatestFrom(() => [
          //TODO: Remove account selectors from transaction proposal (UKETS-4581)
          this.store.select(selectAccountId),
          this.store.select(selectTransactionType),
          this.store.select(selectedTransactionBlocks),
          this.store.select(selectAllocationType),
        ]),
        switchMap(
          ([
            ,
            transferringAccountIdentifier,
            transactionType,
            transactionBlocks,
            allocationType,
          ]) =>
            forkJoin({
              natAcquiringAccount:
                this.transactionProposalService.populateAcquiringAccountIdentifier(
                  +transferringAccountIdentifier,
                  transactionType.type,
                  transactionBlocks
                    ? transactionBlocks[0]?.applicablePeriod
                    : null,
                  null,
                  'NAT'
                ),
              nerAcquiringAccount:
                this.transactionProposalService.populateAcquiringAccountIdentifier(
                  +transferringAccountIdentifier,
                  transactionType.type,
                  transactionBlocks
                    ? transactionBlocks[0]?.applicablePeriod
                    : null,
                  null,
                  'NER'
                ),
            }).pipe(
              map(({ natAcquiringAccount, nerAcquiringAccount }) =>
                SpecifyAcquiringAccountActions.populateExcessAllocationAcquiringAccountsSuccess(
                  {
                    natAcquiringAccount,
                    nerAcquiringAccount,
                  }
                )
              ),
              catchError((error: HttpErrorResponse) =>
                of(
                  errors({
                    errorSummary: this.apiErrorHandlingService.transform(
                      error.error
                    ),
                  })
                )
              )
            )
        )
      );
    }
  );

  enrichUserDefinedAccount$ = createEffect(() =>
    this.actions$.pipe(
      ofType(SpecifyAcquiringAccountActions.enrichUserDefinedAcquiringAccount),
      switchMap(({ transferringAccountId, fullIdentifier }) =>
        this.transactionProposalService
          .getUserDefinedAcquiringAccount(transferringAccountId, fullIdentifier)
          .pipe(
            map((acquiringAccount) =>
              SpecifyAcquiringAccountActions.enrichUserDefinedAcquiringAccountSuccess(
                {
                  acquiringAccount,
                }
              )
            ),
            catchError((error: HttpErrorResponse) =>
              of(
                errors({
                  errorSummary: this.apiErrorHandlingService.transform(
                    error.error
                  ),
                })
              )
            )
          )
      )
    )
  );

  populateAccountsForReversal$ = createEffect(() =>
    this.actions$.pipe(
      ofType(
        TransactionDetailsActions.prepareTransactionProposalStateForReversal
      ),
      switchMap((action) =>
        this.transactionProposalService
          .populateAccountsForReversal(
            action.transferringAccountIdentifier,
            action.acquiringAccountIdentifier
          )
          .pipe(
            map((reversedAccountInfo) =>
              TransactionDetailsActions.prepareTransactionProposalStateForReversalSuccess(
                {
                  reversedAccountInfo,
                }
              )
            ),
            catchError((error: HttpErrorResponse) =>
              of(
                errors({
                  errorSummary: this.apiErrorHandlingService.transform(
                    error.error
                  ),
                })
              )
            )
          )
      )
    )
  );

  enrichProposal$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TransactionProposalActions.enrichProposalForSigning),
      concatLatestFrom(() => this.store.pipe(select(selectTransactionSummary))),
      mergeMap(([, transactionSummary]) =>
        this.transactionProposalService
          .enrichTransactionSummaryForSigning(transactionSummary)
          .pipe(
            map((transactionSummaryForSigning) => {
              return TransactionProposalActions.enrichProposalForSigningSuccess(
                {
                  transactionSummary: transactionSummaryForSigning,
                }
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
          )
      )
    );
  });

  enrichReturnExcessAllocationProposal$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(
        TransactionProposalActions.enrichReturnExcessAllocationProposalForSigning
      ),
      concatLatestFrom(() =>
        this.store.pipe(select(selectReturnExcessAllocationTransactionSummary))
      ),
      mergeMap(([, transactionSummary]) =>
        this.transactionProposalService
          .enrichReturnExcessAllocationTransactionSummaryForSigning(
            transactionSummary
          )
          .pipe(
            map((transactionSummaryForSigning) => {
              return TransactionProposalActions.enrichReturnExcessAllocationProposalForSigningSuccess(
                {
                  transactionSummary: transactionSummaryForSigning,
                }
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
          )
      )
    );
  });

  setCommentAndOtpCodeDone$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TransactionProposalActions.setCommentAndOtpCode),
      withLatestFrom(
        this.store.select(selectCalculatedExcessAmountPerAllocationAccount),
        this.store.select(selectTransactionType)
      ),
      map(([, excessAmount, proposedTransactionType]) => {
        if (
          proposedTransactionType.type === TransactionType.ExcessAllocation &&
          excessAmount != null &&
          excessAmount.returnToNewEntrantsReserveAccount != null &&
          excessAmount.returnToAllocationAccountAmount != null &&
          excessAmount.returnToAllocationAccountAmount > 0 &&
          excessAmount.returnToNewEntrantsReserveAccount > 0
        ) {
          return TransactionProposalActions.validateReturnExcessAllocationProposal();
        } else {
          return TransactionProposalActions.validateProposal();
        }
      })
    );
  });

  validateProposal$ = createEffect(() =>
    this.actions$.pipe(
      ofType(TransactionProposalActions.validateProposal),
      withLatestFrom(this.store.pipe(select(selectTransactionSummary))),
      exhaustMap(([, transactionSummary]) =>
        this.transactionProposalService.validate(transactionSummary).pipe(
          map((businessCheckResult) => {
            return TransactionProposalActions.validateProposalSuccess({
              businessCheckResult,
            });
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
        )
      )
    )
  );

  validateReturnExcessAllocationProposal$ = createEffect(() =>
    this.actions$.pipe(
      ofType(TransactionProposalActions.validateReturnExcessAllocationProposal),
      withLatestFrom(
        this.store.pipe(select(selectReturnExcessAllocationTransactionSummary))
      ),
      exhaustMap(([, returnExcessAllocationTransactionSummary]) =>
        this.transactionProposalService
          .validateNatAndNer(returnExcessAllocationTransactionSummary)
          .pipe(
            map((businessCheckResult) => {
              return TransactionProposalActions.validateReturnExcessAllocationProposalSuccess(
                {
                  businessCheckResult,
                }
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
          )
      )
    )
  );

  validateProposalDone$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TransactionProposalActions.validateProposalSuccess),
      withLatestFrom(
        this.store.pipe(select(selectOtpCode)),
        this.store.pipe(select(selectTransactionSummary))
      ),
      map(([, otpCode, transactionSummary]) =>
        SigningActions.signTransaction({
          transactionSignInfo: {
            transactionSummary,
            otpCode,
          },
        })
      )
    );
  });

  validateReturnExcessAllocationProposalDone$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(
        TransactionProposalActions.validateReturnExcessAllocationProposalSuccess
      ),
      withLatestFrom(
        this.store.pipe(select(selectOtpCode)),
        this.store.pipe(select(selectReturnExcessAllocationTransactionSummary))
      ),
      map(([, otpCode, returnExcessAllocationTransactionSummary]) =>
        SigningActions.signReturnExcessAllocationTransaction({
          transactionSignInfo: {
            returnExcessAllocationTransactionSummary,
            otpCode,
          },
        })
      )
    );
  });

  transactionSigned$ = createEffect(() =>
    this.actions$.pipe(
      ofType(SigningActions.signTransactionSuccess),
      map(() => {
        return TransactionProposalActions.submitProposal();
      })
    )
  );

  transactionSignedReturnExcessAllocation$ = createEffect(() =>
    this.actions$.pipe(
      ofType(SigningActions.signReturnExcessAllocationTransactionSuccess),
      map(() => {
        return TransactionProposalActions.submitReturnExcessAllocationProposal();
      })
    )
  );

  submitProposalAction$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TransactionProposalActions.submitProposal),
      withLatestFrom(
        this.store.pipe(select(selectTransactionSummary)),
        this.store.pipe(select(selectITLNotification)),
        this.store.pipe(select(selectSignature))
      ),
      mergeMap(([, transactionSummary, itlNotification, signature]) => {
        let itlNotificationObj = {};
        if (itlNotification) {
          itlNotificationObj = {
            itlNotification: { ...itlNotification },
            attributes: JSON.stringify(itlNotification),
          };
        }
        return this.transactionProposalService
          .propose({
            ...transactionSummary,
            ...itlNotificationObj,
            signatureInfo: {
              signature: signature.signature,
              data: JSON.stringify(transactionSummary),
            },
          })
          .pipe(
            map((businessCheckResult) =>
              TransactionProposalActions.submitProposalSuccess({
                businessCheckResult,
              })
            ),
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
  });

  submitReturnExcessAllocationProposalAction$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TransactionProposalActions.submitReturnExcessAllocationProposal),
      withLatestFrom(
        this.store.pipe(select(selectReturnExcessAllocationTransactionSummary)),
        this.store.pipe(select(selectITLNotification)),
        this.store.pipe(select(selectSignature))
      ),
      mergeMap(([, transactionSummary, itlNotification, signature]) => {
        let itlNotificationObj = {};
        if (itlNotification) {
          itlNotificationObj = {
            itlNotification: { ...itlNotification },
            attributes: JSON.stringify(itlNotification),
          };
        }
        return this.transactionProposalService
          .proposeReturnExcessAllocation({
            ...transactionSummary,
            ...itlNotificationObj,
            signatureInfo: {
              signature: signature.signature,
              data: JSON.stringify(transactionSummary),
            },
          })
          .pipe(
            map((businessCheckResult) =>
              TransactionProposalActions.submitReturnExcessAllocationProposalSuccess(
                {
                  businessCheckResult,
                }
              )
            ),
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
  });

  /**
   * fetch transaction Summaries
   */
  loadTransactionSummaries$ = createEffect(() =>
    this.actions$.pipe(
      ofType(SelectUnitTypesActions.getTransactionBlockSummariesResult),
      switchMap((action) =>
        this.transactionProposalService
          .getTransactionBlockSummaries(
            action.accountId,
            action.transactionType,
            action.itlNotificationIdentifier
          )
          .pipe(
            map((transactionBlockSummaryResult) =>
              SelectUnitTypesActions.getTransactionBlockSummaryResultSuccess({
                transactionBlockSummaryResult,
              })
            ),
            catchError((error: HttpErrorResponse) =>
              of(
                errors({
                  errorSummary: this.apiErrorHandlingService.transform(
                    error.error
                  ),
                })
              )
            )
          )
      )
    )
  );

  /**
   * fetch trusted accounts
   */
  loadTrustedAccounts$ = createEffect(() =>
    this.actions$.pipe(
      ofType(SpecifyAcquiringAccountActions.getTrustedAccounts),
      withLatestFrom(this.store.pipe(select(selectTransactionType))),
      mergeMap(([action, transactionType]) =>
        this.transactionProposalService
          .getTrustedAcquiringAccounts(action.accountId, transactionType.type)
          .pipe(
            map((trustedAccountsResult) =>
              SpecifyAcquiringAccountActions.getTrustedAccountsSuccess({
                trustedAccountsResult,
              })
            ),
            catchError((error: HttpErrorResponse) =>
              of(
                errors({
                  errorSummary: this.apiErrorHandlingService.transform(
                    error.error
                  ),
                })
              )
            )
          )
      )
    )
  );
}
