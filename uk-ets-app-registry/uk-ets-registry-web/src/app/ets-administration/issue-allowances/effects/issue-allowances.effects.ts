import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import {
  catchError,
  exhaustMap,
  flatMap,
  map,
  mergeMap,
  switchMap,
  tap,
  withLatestFrom,
} from 'rxjs/operators';
import { of } from 'rxjs';
import { errors } from '@shared/shared.action';
import { Router } from '@angular/router';
import { select, Store } from '@ngrx/store';
import {
  selectAllowanceTransaction,
  selectEnrichedAllowanceTransactionSummaryReadyForSigning,
} from '../reducers';
import { IssueAllowancesService } from '@issue-allowances/services';
import { HttpErrorResponse } from '@angular/common/http';
import { ApiErrorHandlingService } from '@shared/services';
import { SigningActions } from '@signing/actions';
import { IssueAllowanceActions } from '@issue-allowances/actions';
import { selectSignature } from '@signing/reducers/signing.selectors';

@Injectable()
export class IssueAllowancesEffects {
  constructor(
    private issueAllowancesService: IssueAllowancesService,
    private apiErrorHandlingService: ApiErrorHandlingService,
    private actions$: Actions,
    private router: Router,
    private store: Store
  ) {}

  loadWizardData$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(IssueAllowanceActions.loadAllowanceWizardData),
      flatMap(() => [
        IssueAllowanceActions.loadAllowances(),
        IssueAllowanceActions.loadAcquiringAccount(),
      ])
    );
  });

  /**
   * fetch transaction Summaries
   */
  loadTransactionSummaries$ = createEffect(() =>
    this.actions$.pipe(
      ofType(IssueAllowanceActions.loadAllowances),
      mergeMap(() =>
        this.issueAllowancesService.getAllowanceBlockSummaries().pipe(
          map((transactionBlockSummaryResult) =>
            IssueAllowanceActions.loadAllowancesSuccess({
              result: transactionBlockSummaryResult,
            })
          ),
          catchError((error) =>
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

  loadAcquiringAccount$ = createEffect(() =>
    this.actions$.pipe(
      ofType(IssueAllowanceActions.loadAcquiringAccount),
      mergeMap(() =>
        this.issueAllowancesService.populateAllowanceIdentifier().pipe(
          map((accountInfo) =>
            IssueAllowanceActions.loadAcquiringAccountSuccess({
              accountInfo,
            })
          ),
          catchError((httpErrorResponse: HttpErrorResponse) =>
            of(
              errors({
                errorSummary: this.apiErrorHandlingService.transform(
                  httpErrorResponse.error
                ),
              })
            )
          )
        )
      )
    )
  );

  otpCodeSubmitted$ = createEffect(() =>
    this.actions$.pipe(
      ofType(IssueAllowanceActions.submitOtpCode),
      withLatestFrom(this.store.pipe(select(selectAllowanceTransaction))),
      map(([action, transactionSummary]) => {
        return SigningActions.signEtsTransaction({
          transactionSignInfo: {
            otpCode: action.otpCode,
            transactionSummary,
          },
        });
      })
    )
  );

  transactionSigned$ = createEffect(() =>
    this.actions$.pipe(
      ofType(SigningActions.signEtsTransactionSuccess),
      map(() => {
        return IssueAllowanceActions.submitAllowanceProposal();
      })
    )
  );

  proposeAllowance$ = createEffect(() =>
    this.actions$.pipe(
      ofType(IssueAllowanceActions.submitAllowanceProposal),
      withLatestFrom(
        this.store.pipe(select(selectAllowanceTransaction)),
        this.store.pipe(select(selectSignature))
      ),
      exhaustMap(([, allowance, signature]) =>
        this.issueAllowancesService
          .propose({
            ...allowance,
            signatureInfo: {
              signature: signature.signature,
              data: JSON.stringify(allowance),
            },
          })
          .pipe(
            map((businessCheckResult) =>
              IssueAllowanceActions.submitAllowanceProposalSuccess({
                businessCheckResult,
              })
            ),
            catchError((httpErrorResponse: HttpErrorResponse) =>
              of(
                errors({
                  errorSummary: this.apiErrorHandlingService.transform(
                    httpErrorResponse.error
                  ),
                })
              )
            )
          )
      )
    )
  );

  setAllowanceQuantityDone$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(IssueAllowanceActions.setAllowanceQuantity),
        tap(() => {
          this.router.navigate(
            ['/ets-administration/issue-allowances/set-transaction-reference'],
            { skipLocationChange: true }
          );
        })
      );
    },
    { dispatch: false }
  );

  setTransactionReferenceDone$ = createEffect(() =>
    this.actions$.pipe(
      ofType(IssueAllowanceActions.setTransactionReference),
      withLatestFrom(
        this.store.pipe(
          select(selectEnrichedAllowanceTransactionSummaryReadyForSigning)
        )
      ),
      map(([, transactionSummary]) => {
        if (transactionSummary.identifier) {
          return IssueAllowanceActions.navigateToCheckAllowanceAndSignAction();
        } else {
          return IssueAllowanceActions.enrichAllowanceForSigning();
        }
      })
    )
  );

  enrichedSummaryForSigning$ = createEffect(() =>
    this.actions$.pipe(
      ofType(IssueAllowanceActions.enrichAllowanceForSigningSuccess),
      map(() => {
        return IssueAllowanceActions.navigateToCheckAllowanceAndSignAction();
      })
    )
  );

  enrichProposal$ = createEffect(() =>
    this.actions$.pipe(
      ofType(IssueAllowanceActions.enrichAllowanceForSigning),
      withLatestFrom(this.store.pipe(select(selectAllowanceTransaction))),
      switchMap(([, summary]) =>
        this.issueAllowancesService
          .enrichTransactionSummaryForSigning(summary)
          .pipe(
            map((transactionSummary) => {
              return IssueAllowanceActions.enrichAllowanceForSigningSuccess({
                transactionSummary,
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
          )
      )
    )
  );

  navigateToCheckAndSign$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(IssueAllowanceActions.navigateToCheckAllowanceAndSignAction),
        tap(() => {
          this.router.navigate(
            ['/ets-administration/issue-allowances/check-request-and-sign'],
            { skipLocationChange: true }
          );
        })
      );
    },
    { dispatch: false }
  );

  navigateToAllowanceSubmitted$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(IssueAllowanceActions.submitAllowanceProposalSuccess),
        tap(() => {
          this.router.navigate(
            ['/ets-administration/issue-allowances/proposal-submitted'],
            { skipLocationChange: true }
          );
        })
      );
    },
    { dispatch: false }
  );

  cancelIssuanceOfAllowancesProposalRequested$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(IssueAllowanceActions.cancelAllowanceProposalRequested),
        tap(() => {
          this.router.navigate(
            ['/ets-administration/issue-allowances/cancel-proposal'],
            { skipLocationChange: true }
          );
        })
      );
    },
    { dispatch: false }
  );

  cancelIssuanceOfAllowancesProposal$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(IssueAllowanceActions.cancelAllowanceProposalConfirmed),
        tap(() => {
          this.router.navigate(['/ets-administration/issue-allowances'], {
            skipLocationChange: true,
          });
        })
      );
    },
    { dispatch: false }
  );
}
