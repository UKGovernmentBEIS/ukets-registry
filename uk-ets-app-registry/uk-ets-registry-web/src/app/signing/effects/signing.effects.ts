import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { concatLatestFrom } from '@ngrx/operators';
import { errors } from '@shared/shared.action';
import { catchError, map, switchMap, withLatestFrom } from 'rxjs/operators';
import { HttpErrorResponse } from '@angular/common/http';
import { of } from 'rxjs';
import { SigningActions, SigningApiActions } from '@signing/actions';
import { Router } from '@angular/router';
import { select, Store } from '@ngrx/store';
import { SigningService } from '@signing/services';
import { ApiErrorHandlingService } from '@shared/services';
import * as IssueKpUnitActions from '../../kp-administration/issue-kp-units/actions/issue-kp-units.actions';
import { IssueAllowanceActions } from '@issue-allowances/actions';
import { TransactionProposalActions } from '@transaction-proposal/actions';
import { selectRegistryConfigurationProperty } from '@shared/shared.selector';
import { selectSignedTransactionType } from '@signing/reducers/signing.selectors';
import { SignedDataType } from '@signing/model';

@Injectable()
export class SigningEffects {
  constructor(
    private signingService: SigningService,
    private apiErrorHandlingService: ApiErrorHandlingService,
    private actions$: Actions,
    private _router: Router,
    private store: Store
  ) {}

  singTransaction$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(
        SigningActions.signTransaction,
        SigningActions.signEtsTransaction,
        SigningActions.signKpTransaction
      ),
      map((action) =>
        SigningApiActions.signData({
          signingRequestInfo: {
            otpCode: action.transactionSignInfo.otpCode,
            data: JSON.stringify(action.transactionSignInfo.transactionSummary),
          },
        })
      )
    );
  });

  signReturnExcessAllocationTransaction$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(SigningActions.signReturnExcessAllocationTransaction),
      map((action) =>
        SigningApiActions.signData({
          signingRequestInfo: {
            otpCode: action.transactionSignInfo.otpCode,
            data: JSON.stringify(
              action.transactionSignInfo
                .returnExcessAllocationTransactionSummary
            ),
          },
        })
      )
    );
  });

  singDataAction$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(SigningApiActions.signData),
      concatLatestFrom(() =>
        this.store.pipe(
          select(selectRegistryConfigurationProperty, {
            property: 'signing.enabled',
          })
        )
      ),
      switchMap(([action, signingEnabled]) =>
        this.signingService
          .signData(action.signingRequestInfo, signingEnabled)
          .pipe(
            map((result) => {
              return SigningApiActions.signDataSuccess({
                signature: result,
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
    );
  });

  signDataSuccess$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(SigningApiActions.signDataSuccess),
      concatLatestFrom(() => this.store.select(selectSignedTransactionType)),
      map(([action, signedDataType]) => {
        const type: SignedDataType = signedDataType;
        switch (type) {
          case SignedDataType.ETS_WIZARD:
            return SigningActions.signEtsTransactionSuccess({
              signature: action.signature,
            });
          case SignedDataType.MAIN_TRANSACTION_WIZARD:
            return SigningActions.signTransactionSuccess({
              signature: action.signature,
            });
          case SignedDataType.KP_WIZARD:
            return SigningActions.signKpTransactionSuccess({
              signature: action.signature,
            });
          case SignedDataType.RETURN_EXCESS_ALLOCATION_WIZARD:
            return SigningActions.signReturnExcessAllocationTransactionSuccess({
              signature: action.signature,
            });
        }
      })
    );
  });

  clearProposal = createEffect(() => {
    return this.actions$.pipe(
      ofType(
        IssueKpUnitActions.issuanceTransactionProposeSuccess,
        IssueAllowanceActions.submitAllowanceProposalSuccess,
        TransactionProposalActions.submitProposalSuccess,
        TransactionProposalActions.submitReturnExcessAllocationProposalSuccess
      ),
      map(() => SigningActions.clearSignature())
    );
  });
}
