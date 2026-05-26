import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { Router } from '@angular/router';
import { catchError, exhaustMap, map, mergeMap, tap } from 'rxjs/operators';
import { selectAccountId } from '@account-management/account/account-details/account.selector';
import { ApiErrorHandlingService } from '@shared/services';
import { getRouteFromArray } from '@shared/utils/router.utils';
import { AccountTransferService } from '@account-transfer/services';
import { AccountTransferActions } from '@account-transfer/store/actions';
import { AccountTransferPathsModel } from '@account-transfer/model';
import { HttpErrorResponse } from '@angular/common/http';
import { errors } from '@registry-web/shared/shared.action';
import { of } from 'rxjs';
import {
  fetchAcquiringAccountHolderContactsSuccess,
  fetchAcquiringAccountHolderSuccess,
  fetchLoadAndShowAcquiringAccountHolderContacts,
  fetchLoadAndShowPendingRegulatorNoticesTaskExists,
  fetchPendingRegulatorNoticesTaskExistsSuccess,
  loadAcquiringAccountHolder,
  loadAcquiringAccountHolderContacts,
} from '@account-transfer/store/actions/account-transfer.actions';
import { selectAccountTransferType } from '@account-transfer/store/reducers';
import { concatLatestFrom } from '@ngrx/operators';
import { RegulatorNoticeApiService } from '@regulator-notice-management/service';

@Injectable()
export class AccountTransferNavigationEffects {
  constructor(
    private actions$: Actions,
    private store: Store,
    private router: Router,
    private apiErrorHandlingService: ApiErrorHandlingService,
    private accountTransferService: AccountTransferService,
    private regulatorNoticeApiService: RegulatorNoticeApiService
  ) {}

  fetchLoadAndShowAcquiringAccountHolder$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AccountTransferActions.fetchLoadAndShowAcquiringAccountHolder),
      mergeMap((action) => {
        return this.accountTransferService
          .fetchAccountHolder(action.identifier)
          .pipe(
            map((result) =>
              fetchAcquiringAccountHolderSuccess({
                accountHolder: result,
              })
            ),
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
    );
  });

  fetchLoadAndShowAcquiringAccountHolderContacts$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(
        AccountTransferActions.fetchLoadAndShowAcquiringAccountHolderContacts
      ),
      mergeMap((action) => {
        return this.accountTransferService
          .fetchAccountHolderContacts(action.identifier)
          .pipe(
            map((result) =>
              fetchAcquiringAccountHolderContactsSuccess({
                accountHolderContactInfo: result,
              })
            ),
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
    );
  });

  fetchLoadAndShowPendingRegulatorNoticesTaskExists$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(
        AccountTransferActions.fetchLoadAndShowPendingRegulatorNoticesTaskExists
      ),
      concatLatestFrom(() => this.store.select(selectAccountId)),
      mergeMap(([action, accountId]) => {
        return this.regulatorNoticeApiService
          .fetchPendingRegulatorNoticeTaskExists(accountId)
          .pipe(
            map((result) =>
              fetchPendingRegulatorNoticesTaskExistsSuccess({
                pendingRegulatorNoticesTaskExists: result,
              })
            ),
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
    );
  });

  fetchAcquiringAccountHolderSuccess$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AccountTransferActions.fetchAcquiringAccountHolderSuccess),
      mergeMap((action) => [
        loadAcquiringAccountHolder({
          accountHolder: action.accountHolder,
        }),
        fetchLoadAndShowPendingRegulatorNoticesTaskExists(),
        fetchLoadAndShowAcquiringAccountHolderContacts({
          identifier: action.accountHolder.id,
        }),
      ])
    );
  });

  fetchAcquiringAccountHolderContactsSuccess$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AccountTransferActions.fetchAcquiringAccountHolderContactsSuccess),
      concatLatestFrom(() => this.store.select(selectAccountId)),
      mergeMap(([action, accountId]) => [
        loadAcquiringAccountHolderContacts({
          accountHolderContactInfo: action.accountHolderContactInfo,
        }),
        AccountTransferActions.navigateTo({
          route: getRouteFromArray([
            'account',
            accountId,
            AccountTransferPathsModel.BASE_PATH,
            AccountTransferPathsModel.SET_EMITTER_ID,
          ]),
          extras: {
            skipLocationChange: true,
          },
        }),
      ])
    );
  });

  cancelClicked$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AccountTransferActions.cancelClicked),
      concatLatestFrom(() => this.store.select(selectAccountId)),
      map(([action, accountId]) =>
        AccountTransferActions.navigateTo({
          route: getRouteFromArray([
            'account',
            accountId,
            AccountTransferPathsModel.BASE_PATH,
            AccountTransferPathsModel.CANCEL_ACCOUNT_TRANSFER_REQUEST,
          ]),
          extras: {
            queryParams: { goBackRoute: action.route },
            skipLocationChange: true,
          },
        })
      )
    );
  });

  cancelAccountHolderUpdateRequest$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AccountTransferActions.cancelAccountTransferRequest),
      concatLatestFrom(() => this.store.select(selectAccountId)),
      mergeMap(([, accountId]) => [
        AccountTransferActions.clearAccountTransferRequest(),
        AccountTransferActions.navigateTo({
          route: `/account/${accountId}`,
        }),
      ])
    );
  });

  navigateTo$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(AccountTransferActions.navigateTo),
        tap((action) => {
          this.router.navigate([action.route], action.extras);
        })
      );
    },
    { dispatch: false }
  );

  navigateToActionWizard$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AccountTransferActions.setAccountTransferType),
      concatLatestFrom(() => this.store.select(selectAccountId)),
      map(([action, accountId]) => {
        switch (action.selectedAccountTransferType.selectedUpdateType) {
          case 'ACCOUNT_TRANSFER_TO_EXISTING_HOLDER':
            return AccountTransferActions.fetchLoadAndShowAcquiringAccountHolder(
              {
                identifier:
                  action.selectedAccountTransferType
                    .selectedExistingAccountHolder.identifier,
              }
            );
          case 'ACCOUNT_TRANSFER_TO_CREATED_HOLDER':
            return AccountTransferActions.navigateTo({
              route: getRouteFromArray([
                'account',
                accountId,
                AccountTransferPathsModel.BASE_PATH,
                AccountTransferPathsModel.UPDATE_ACCOUNT_HOLDER,
              ]),
              extras: {
                skipLocationChange: true,
              },
            });
        }
      })
    );
  });

  navigateToCheckAccountTransferWizard$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AccountTransferActions.setAcquiringEmitterId),
      concatLatestFrom(() => this.store.select(selectAccountId)),
      map(([action, accountId]) => {
        return AccountTransferActions.navigateTo({
          route: getRouteFromArray([
            'account',
            accountId,
            AccountTransferPathsModel.BASE_PATH,
            AccountTransferPathsModel.CHECK_ACCOUNT_TRANSFER,
          ]),
          extras: {
            skipLocationChange: true,
          },
        });
      })
    );
  });

  navigateFromAccountHolderDetailsPage$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AccountTransferActions.setAcquiringAccountHolderDetails),
      concatLatestFrom(() => this.store.select(selectAccountId)),
      map(([, accountId]) => {
        return AccountTransferActions.navigateTo({
          route: getRouteFromArray([
            'account',
            accountId,
            AccountTransferPathsModel.BASE_PATH,
            AccountTransferPathsModel.UPDATE_AH_ADDRESS,
          ]),
          extras: {
            skipLocationChange: true,
          },
        });
      })
    );
  });

  navigateFromAccountHolderAddressPage$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AccountTransferActions.setAcquiringAccountHolderAddress),
      concatLatestFrom(() => this.store.select(selectAccountId)),
      map(([, accountId]) => {
        return AccountTransferActions.navigateTo({
          route: getRouteFromArray([
            'account',
            accountId,
            AccountTransferPathsModel.BASE_PATH,
            AccountTransferPathsModel.UPDATE_PRIMARY_CONTACT,
          ]),
          extras: {
            skipLocationChange: true,
          },
        });
      })
    );
  });

  navigateFromAccountHolderContactPage$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(
        AccountTransferActions.setAcquiringAccountHolderPrimaryContactDetails
      ),
      concatLatestFrom(() => this.store.select(selectAccountId)),
      map(([, accountId]) => {
        return AccountTransferActions.navigateTo({
          route: getRouteFromArray([
            'account',
            accountId,
            AccountTransferPathsModel.BASE_PATH,
            AccountTransferPathsModel.UPDATE_PRIMARY_CONTACT_WORK,
          ]),
          extras: {
            skipLocationChange: true,
          },
        });
      })
    );
  });

  navigateFromAccountHolderContactWorkPage$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(
        AccountTransferActions.setAcquiringAccountHolderPrimaryContactWorkDetails
      ),
      concatLatestFrom(() => this.store.select(selectAccountId)),
      map(([, accountId]) => {
        return AccountTransferActions.navigateTo({
          route: getRouteFromArray([
            'account',
            accountId,
            AccountTransferPathsModel.BASE_PATH,
            AccountTransferPathsModel.SET_EMITTER_ID,
          ]),
          extras: {
            skipLocationChange: true,
          },
        });
      })
    );
  });

  submitUpdateRequest$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AccountTransferActions.submitUpdateRequest),
      concatLatestFrom(() => [
        this.store.select(selectAccountTransferType),
        this.store.select(selectAccountId),
      ]),
      exhaustMap(([action, accountTransferType, acountIdentifier]) => {
        return this.accountTransferService
          .submitRequest({
            accountTransferType: accountTransferType,
            accountIdentifier: acountIdentifier,
            existingAcquiringAccountHolderIdentifier:
              action.acquiringAccountTransferInfo
                .existingAcquiringAccountHolderIdentifier,
            acquiringAccountHolder:
              action.acquiringAccountTransferInfo.acquiringAccountHolder,
            acquiringAccountHolderContactInfo:
              action.acquiringAccountTransferInfo
                .acquiringAccountHolderContactInfo,
            acquiringEmitterId:
              action.acquiringAccountTransferInfo.acquiringEmitterId,
          })
          .pipe(
            map((data) => {
              return AccountTransferActions.submitUpdateRequestSuccess({
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
      ofType(AccountTransferActions.submitUpdateRequestSuccess),
      concatLatestFrom(() => this.store.select(selectAccountId)),
      map(([, accountId]) => {
        return AccountTransferActions.navigateTo({
          route: getRouteFromArray([
            'account',
            accountId,
            AccountTransferPathsModel.BASE_PATH,
            AccountTransferPathsModel.REQUEST_SUBMITTED,
          ]),
          extras: {
            skipLocationChange: true,
          },
        });
      })
    );
  });
}
