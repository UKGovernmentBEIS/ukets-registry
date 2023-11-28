import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { select, Store } from '@ngrx/store';
import {
  selectUpdateRequestPayload,
  selectUpdateType
} from '@authorised-representatives/reducers';
import {
  catchError,
  exhaustMap,
  map,
  mergeMap,
  switchMap,
  withLatestFrom
} from 'rxjs/operators';
import { selectAccountId } from '@account-management/account/account-details/account.selector';
import { AuthorisedRepresentativesActions } from '@authorised-representatives/actions';
import { HttpErrorResponse } from '@angular/common/http';
import { of } from 'rxjs';
import { errors } from '@shared/shared.action';
import { AuthorisedRepresentativesApiService } from '@authorised-representatives/services/authorised-representatives-api.service';
import { ApiErrorHandlingService } from '@shared/services';
import {
  fetchAuthorisedRepresentativesOtherAccountsSuccess,
  replaceAuthorisedRepresentativeSuccess,
  selectAuthorisedRepresentativeSuccess
} from '@authorised-representatives/actions/authorised-representatives.actions';

@Injectable()
export class AuthorisedRepresentativesEffects {
  constructor(
    private apiErrorHandlingService: ApiErrorHandlingService,
    private actions$: Actions,
    private store: Store,
    private authorisedRepresentativesApiService: AuthorisedRepresentativesApiService
  ) {}

  fetchEligibleArs$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AuthorisedRepresentativesActions.fetchEligibleArs),
      withLatestFrom(
        this.store.pipe(select(selectAccountId)),
        this.store.pipe(select(selectUpdateType))
      ),
      switchMap(([, accountId, updateType]) => {
        return this.authorisedRepresentativesApiService
          .getAuthorisedRepresentatives(accountId, updateType)
          .pipe(
            map(eligibleArs => {
              return AuthorisedRepresentativesActions.fetchEligibleArsSuccess({
                eligibleArs
              });
            }),
            catchError((error: HttpErrorResponse) =>
              of(
                errors({
                  errorSummary: this.apiErrorHandlingService.transform(
                    error.error
                  )
                })
              )
            )
          );
      })
    );
  });

  cancelAuthorisedRepresentativesUpdateRequest$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(
        AuthorisedRepresentativesActions.cancelAuthorisedRepresentativesUpdateRequest
      ),
      withLatestFrom(this.store.pipe(select(selectAccountId))),
      mergeMap(([, accountId]) => [
        AuthorisedRepresentativesActions.clearAuthorisedRepresentativesUpdateRequest(),
        AuthorisedRepresentativesActions.navigateTo({
          route: `/account/${accountId}`
        })
      ])
    );
  });

  fetchAuthorisedRepresentativesOtherAccounts$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(
        AuthorisedRepresentativesActions.fetchAuthorisedRepresentativesOtherAccounts
      ),
      withLatestFrom(this.store.pipe(select(selectAccountId))),
      switchMap(([, accountId]) => {
        return this.authorisedRepresentativesApiService
          .getOtherAuthorisedRepresentativesOtherAccounts(accountId)
          .pipe(
            map(authorisedRepresentatives =>
              fetchAuthorisedRepresentativesOtherAccountsSuccess({
                authorisedRepresentatives
              })
            ),
            catchError(error =>
              of(
                errors({
                  errorSummary: this.apiErrorHandlingService.transform(
                    error.error
                  )
                })
              )
            )
          );
      })
    );
  });

  selectAuthorisedRepresentative$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AuthorisedRepresentativesActions.selectAuthorisedRepresentative),
      withLatestFrom(this.store.pipe(select(selectAccountId))),
      switchMap(([action, accountId]) => {
        return this.authorisedRepresentativesApiService
          .getAuthorisedRepresentativeCandidate(accountId, action.urid)
          .pipe(
            map(authorisedRepresentative =>
              selectAuthorisedRepresentativeSuccess({
                authorisedRepresentative
              })
            ),
            catchError(error =>
              of(
                errors({
                  errorSummary: this.apiErrorHandlingService.transform(
                    error.error
                  )
                })
              )
            )
          );
      })
    );
  });

  replaceAuthorisedRepresentative$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AuthorisedRepresentativesActions.replaceAuthorisedRepresentative),
      withLatestFrom(this.store.pipe(select(selectAccountId))),
      switchMap(([action, accountId]) => {
        return this.authorisedRepresentativesApiService
          .getAuthorisedRepresentativeCandidate(
            accountId,
            action.updateRequest.candidateUrid
          )
          .pipe(
            map(authorisedRepresentative =>
              replaceAuthorisedRepresentativeSuccess({
                candidateAr: authorisedRepresentative,
                existingArUrid: action.updateRequest.replaceeUrid
              })
            ),
            catchError(error =>
              of(
                errors({
                  errorSummary: this.apiErrorHandlingService.transform(
                    error.error
                  )
                })
              )
            )
          );
      })
    );
  });

  submitUpdateRequest$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AuthorisedRepresentativesActions.submitUpdateRequest),
      withLatestFrom(
        this.store.select(selectUpdateType),
        this.store.select(selectUpdateRequestPayload),
        this.store.select(selectAccountId)
      ),
      exhaustMap(([action, updateType, updateRequest, accountId]) => {
        updateRequest.comment = action.comment;
        return this.authorisedRepresentativesApiService
          .submitUpdateRequest(updateType, accountId, updateRequest)
          .pipe(
            map(data => {
              return AuthorisedRepresentativesActions.submitUpdateRequestSuccess(
                {
                  requestId: data
                }
              );
            }),
            catchError((error: HttpErrorResponse) =>
              of(
                errors({
                  errorSummary: this.apiErrorHandlingService.transform(
                    error.error
                  )
                })
              )
            )
          );
      })
    );
  });
}
