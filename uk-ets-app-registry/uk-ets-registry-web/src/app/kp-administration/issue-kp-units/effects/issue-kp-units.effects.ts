import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import {
  catchError,
  exhaustMap,
  map,
  mergeMap,
  switchMap,
  withLatestFrom,
} from 'rxjs/operators';
import { IssueKpUnitsService } from '@issue-kp-units/services';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { of } from 'rxjs';
import { errors } from '@shared/shared.action';
import { select, Store } from '@ngrx/store';
import {
  selectEnrichedTransactionSummaryReadyForSigning,
  selectSelectedCommitmentPeriod,
  selectTransactionSummary,
} from '../reducers';
import { HttpErrorResponse } from '@angular/common/http';
import {
  apiErrorToBusinessError,
  apiErrorToStringMessage,
  BusinessCheckErrorResult,
  RegistryLevelType,
} from '@shared/model/transaction';
import { SigningActions } from '@signing/actions';
import { selectSignature } from '@signing/reducers/signing.selectors';
import {
  IssueKpUnitsActions,
  IssueKpUnitsApiActions,
  IssueKpUnitsNavigationActions,
} from '@issue-kp-units/actions';

@Injectable()
export class IssueKpUnitsEffects {
  constructor(
    private issueKpUnitsService: IssueKpUnitsService,
    private actions$: Actions,
    private store: Store
  ) {}

  // TODO : Use the /shared/services/api-error-handling.service.ts

  /**
   * lookup accounts for selected commitment period
   */
  loadAccountsForCommitmentPeriod$ = createEffect(() =>
    this.actions$.pipe(
      ofType(IssueKpUnitsActions.loadAccountsForCommitmentPeriod),
      mergeMap(({ commitmentPeriod }) =>
        this.issueKpUnitsService
          .getAccountsForCommitmentPeriod(commitmentPeriod)
          .pipe(
            map((acquiringAccountInfoList) =>
              IssueKpUnitsActions.loadAccountsForCommitmentPeriodSuccess({
                acquiringAccountInfoList,
              })
            )
          )
      ),
      catchError((error) =>
        of(errors({ errorSummary: this.translateError(error) }))
      )
    )
  );

  loadRegistryLevels$ = createEffect(() =>
    this.actions$.pipe(
      ofType(IssueKpUnitsActions.loadRegistryLevels),
      withLatestFrom(this.store.pipe(select(selectSelectedCommitmentPeriod))),
      map(([, commitmentPediod]: any) =>
        IssueKpUnitsActions.loadRegistryLevelsForCommitmentPeriodAndType({
          commitmentPeriod: commitmentPediod,
          registryLevelType: RegistryLevelType.ISSUANCE_KYOTO_LEVEL,
        })
      )
    )
  );

  /**
   * lookup registy level table
   */
  loadRegistryLevelsForCommitmentPeriodAndLevelType$ = createEffect(() =>
    this.actions$.pipe(
      ofType(IssueKpUnitsActions.loadRegistryLevelsForCommitmentPeriodAndType),
      mergeMap(({ commitmentPeriod, registryLevelType }) =>
        this.issueKpUnitsService
          .getAccountsForCommitmentPeriodAndType(
            commitmentPeriod,
            registryLevelType
          )
          .pipe(
            map((registryLevelResult) => {
              return IssueKpUnitsActions.loadRegistryLevelsForCommitmentPeriodAndTypeSuccess(
                { registryLevelResult }
              );
            })
          )
      )
    )
  );

  selectAccountDone$ = createEffect(() =>
    this.actions$.pipe(
      ofType(IssueKpUnitsActions.selectAcquiringAccount),
      map(() => {
        return IssueKpUnitsNavigationActions.navigateToSelectUnitTypeAndQuantity();
      })
    )
  );

  // click to validate issuance transaction
  selectUnitDone$ = createEffect(() =>
    this.actions$.pipe(
      ofType(IssueKpUnitsActions.selectRegistryLevelAndQuantity),
      withLatestFrom(this.store.pipe(select(selectTransactionSummary))),
      map(([, transactionSummary]: any) =>
        IssueKpUnitsApiActions.issuanceTransactionValidate({
          transactionSummary,
        })
      )
    )
  );

  validateProposal$ = createEffect(() =>
    this.actions$.pipe(
      ofType(IssueKpUnitsApiActions.issuanceTransactionValidate),
      switchMap(({ transactionSummary }) =>
        this.issueKpUnitsService.validate(transactionSummary).pipe(
          map((businessCheckResult) => {
            return IssueKpUnitsApiActions.issuanceTransactionValidateSuccess({
              businessCheckResult,
            });
          }),
          catchError((error: HttpErrorResponse) => {
            if (
              this.isBusinessCheckErrorResult(
                apiErrorToBusinessError(error.error)
              )
            ) {
              return of(
                errors({
                  errorSummary: this.translateBusinessCheckErrorError(
                    apiErrorToBusinessError(error.error)
                  ),
                })
              );
            } else {
              return of(
                errors({
                  errorSummary: this.translateError(error.error),
                })
              );
            }
          })
        )
      )
    )
  );

  validateProposalDone$ = createEffect(() =>
    this.actions$.pipe(
      ofType(IssueKpUnitsApiActions.issuanceTransactionValidateSuccess),
      withLatestFrom(
        this.store.pipe(select(selectEnrichedTransactionSummaryReadyForSigning))
      ),
      map(() => {
        return IssueKpUnitsNavigationActions.navigateToSetTransactionReference();
      })
    )
  );

  setTransactionReference$ = createEffect(() =>
    this.actions$.pipe(
      ofType(IssueKpUnitsActions.setTransactionReference),
      withLatestFrom(
        this.store.pipe(select(selectEnrichedTransactionSummaryReadyForSigning))
      ),
      map(([, transactionSummary]) => {
        if (transactionSummary.identifier) {
          return IssueKpUnitsNavigationActions.navigateToCheckTransactionAndSign();
        } else {
          return IssueKpUnitsApiActions.issuanceTransactionEnrichProposalForSigning();
        }
      })
    )
  );

  enrichProposal$ = createEffect(() =>
    this.actions$.pipe(
      ofType(
        IssueKpUnitsApiActions.issuanceTransactionEnrichProposalForSigning
      ),
      withLatestFrom(this.store.pipe(select(selectTransactionSummary))),
      switchMap(([, summary]) =>
        this.issueKpUnitsService
          .enrichTransactionSummaryForSigning(summary)
          .pipe(
            map((transactionSummary) => {
              return IssueKpUnitsApiActions.issuanceTransactionEnrichProposalForSigningSuccess(
                {
                  transactionSummary,
                }
              );
            }),
            catchError((error: HttpErrorResponse) => {
              return of(
                errors({
                  errorSummary: this.translateError(error.error),
                })
              );
            })
          )
      )
    )
  );

  enrichIssuanceDone$ = createEffect(() =>
    this.actions$.pipe(
      ofType(
        IssueKpUnitsApiActions.issuanceTransactionEnrichProposalForSigningSuccess
      ),
      map(() =>
        IssueKpUnitsNavigationActions.navigateToCheckTransactionAndSign()
      )
    )
  );

  otpCodeSubmitted$ = createEffect(() =>
    this.actions$.pipe(
      ofType(IssueKpUnitsActions.submitOtpCode),
      withLatestFrom(this.store.pipe(select(selectTransactionSummary))),
      map(([action, transactionSummary]) => {
        return SigningActions.signKpTransaction({
          transactionSignInfo: { otpCode: action.otpCode, transactionSummary },
        });
      })
    )
  );

  transactionSigned$ = createEffect(() =>
    this.actions$.pipe(
      ofType(SigningActions.signKpTransactionSuccess),
      map(() => {
        return IssueKpUnitsActions.submitIssuanceProposal();
      })
    )
  );

  // clicked event to submit issuance transaction
  submitProposalClicked$ = createEffect(() =>
    this.actions$.pipe(
      ofType(IssueKpUnitsActions.submitIssuanceProposal),
      withLatestFrom(
        this.store.pipe(select(selectTransactionSummary)),
        this.store.pipe(select(selectSignature))
      ),
      map(([, transactionSummary, signature]: any) => {
        return IssueKpUnitsActions.issuanceTransactionPropose({
          signedTransactionSummary: {
            ...transactionSummary,
            signatureInfo: {
              signature: signature.signature,
              data: JSON.stringify(transactionSummary),
            },
          },
        });
      })
    )
  );

  /**
   *  sends an issueance proposal request to server
   */
  submitProposal$ = createEffect(() =>
    this.actions$.pipe(
      ofType(IssueKpUnitsActions.issuanceTransactionPropose),
      exhaustMap(({ signedTransactionSummary }) =>
        this.issueKpUnitsService.proposeIssuance(signedTransactionSummary).pipe(
          map((businessCheckResult) => {
            return IssueKpUnitsActions.issuanceTransactionProposeSuccess({
              businessCheckResult,
            });
          }),
          catchError((error: HttpErrorResponse) => {
            if (
              this.isBusinessCheckErrorResult(
                apiErrorToBusinessError(error.error)
              )
            ) {
              return of(
                errors({
                  errorSummary: this.translateBusinessCheckErrorError(
                    apiErrorToBusinessError(error.error)
                  ),
                })
              );
            } else {
              return of(
                errors({
                  errorSummary: this.translateError(
                    apiErrorToStringMessage(error.error)
                  ),
                })
              );
            }
          })
        )
      )
    )
  );

  transactionProposedSuccess$ = createEffect(() =>
    this.actions$.pipe(
      ofType(IssueKpUnitsActions.issuanceTransactionProposeSuccess),
      map(() => {
        return IssueKpUnitsNavigationActions.navigateToProposalSubmitted();
      })
    )
  );

  isBusinessCheckErrorResult(object: any): object is BusinessCheckErrorResult {
    return 'errors' in object;
  }

  private translateError(error): ErrorSummary {
    return {
      errors: [
        {
          componentId: '',
          errorMessage: JSON.stringify(apiErrorToStringMessage(error)),
        },
      ],
    };
  }

  private translateBusinessCheckErrorError(
    error: BusinessCheckErrorResult
  ): ErrorSummary {
    const errorDetails: ErrorDetail[] = [];
    if (error && error.errors) {
      error.errors.forEach((e) =>
        errorDetails.push({
          errorMessage: e.message,
          componentId: 'c',
        })
      );
      return {
        errors: errorDetails,
      };
    }
  }
}
