import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { select, Store } from '@ngrx/store';
import { Router } from '@angular/router';
import {
  catchError,
  exhaustMap,
  map,
  mergeMap,
  tap,
  withLatestFrom,
} from 'rxjs/operators';
import { selectAccountId } from '@account-management/account/account-details/account.selector';
import { AccountHolderDetailsWizardActions } from '@account-management/account/account-holder-details-wizard/actions';
import {
  AccountHolderDetailsType,
  AccountHolderDetailsWizardPathsModel,
} from '@account-management/account/account-holder-details-wizard/model';
import { HttpErrorResponse } from '@angular/common/http';
import { of } from 'rxjs';
import { errors } from '@shared/shared.action';
import { ApiErrorHandlingService } from '@shared/services';
import { AccountHolderUpdateService } from '@account-management/account/account-holder-details-wizard/services';
import { getRouteFromArray } from '@shared/utils/router.utils';
import { ContactType } from '@shared/model/account-holder-contact-type';

@Injectable()
export class AccountHolderDetailsWizardNavigationEffects {
  constructor(
    private actions$: Actions,
    private store: Store,
    private router: Router,
    private apiErrorHandlingService: ApiErrorHandlingService,
    private accountHolderUpdateService: AccountHolderUpdateService
  ) {}

  cancelClicked$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AccountHolderDetailsWizardActions.cancelClicked),
      withLatestFrom(this.store.pipe(select(selectAccountId))),
      map(([action, accountId]) =>
        AccountHolderDetailsWizardActions.navigateTo({
          route: getRouteFromArray([
            'account',
            accountId,
            AccountHolderDetailsWizardPathsModel.BASE_PATH,
            AccountHolderDetailsWizardPathsModel.CANCEL_UPDATE_REQUEST,
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
      ofType(
        AccountHolderDetailsWizardActions.cancelAccountHolderDetailsUpdateRequest
      ),
      withLatestFrom(this.store.pipe(select(selectAccountId))),
      mergeMap(([, accountId]) => [
        AccountHolderDetailsWizardActions.clearAccountHolderDetailsUpdateRequest(),
        AccountHolderDetailsWizardActions.navigateTo({
          route: `/account/${accountId}`,
        }),
      ])
    );
  });

  navigateTo$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(AccountHolderDetailsWizardActions.navigateTo),
        tap((action) => {
          this.router.navigate([action.route], action.extras);
        })
      );
    },
    { dispatch: false }
  );

  navigateToActionWizard$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AccountHolderDetailsWizardActions.setRequestUpdateType),
      withLatestFrom(this.store.pipe(select(selectAccountId))),
      map(([action, accountId]) => {
        let path = '';
        switch (action.updateType) {
          case AccountHolderDetailsType.ACCOUNT_HOLDER_UPDATE_DETAILS:
            path = AccountHolderDetailsWizardPathsModel.UPDATE_ACCOUNT_HOLDER;
            break;
          case AccountHolderDetailsType.ACCOUNT_HOLDER_PRIMARY_CONTACT_DETAILS:
            path = AccountHolderDetailsWizardPathsModel.UPDATE_PRIMARY_CONTACT;
            break;
          case AccountHolderDetailsType.ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_UPDATE:
          case AccountHolderDetailsType.ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_ADD:
            path =
              AccountHolderDetailsWizardPathsModel.UPDATE_ALTERNATIVE_PRIMARY_CONTACT;
            break;
          case AccountHolderDetailsType.ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_DELETE:
            path = AccountHolderDetailsWizardPathsModel.CHECK_UPDATE_REQUEST;
            break;
        }
        return AccountHolderDetailsWizardActions.navigateTo({
          route: getRouteFromArray([
            'account',
            accountId,
            AccountHolderDetailsWizardPathsModel.BASE_PATH,
            path,
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
      ofType(AccountHolderDetailsWizardActions.setAccountHolderDetails),
      withLatestFrom(this.store.pipe(select(selectAccountId))),
      map(([, accountId]) => {
        return AccountHolderDetailsWizardActions.navigateTo({
          route: getRouteFromArray([
            'account',
            accountId,
            AccountHolderDetailsWizardPathsModel.BASE_PATH,
            AccountHolderDetailsWizardPathsModel.UPDATE_AH_ADDRESS,
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
      ofType(AccountHolderDetailsWizardActions.setAccountHolderAddress),
      withLatestFrom(this.store.pipe(select(selectAccountId))),
      map(([, accountId]) => {
        return AccountHolderDetailsWizardActions.navigateTo({
          route: getRouteFromArray([
            'account',
            accountId,
            AccountHolderDetailsWizardPathsModel.BASE_PATH,
            AccountHolderDetailsWizardPathsModel.CHECK_UPDATE_REQUEST,
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
      ofType(AccountHolderDetailsWizardActions.setAccountHolderContactDetails),
      withLatestFrom(this.store.pipe(select(selectAccountId))),
      map(([action, accountId]) => {
        let path = '';
        if (action.contactType === ContactType.PRIMARY) {
          path =
            AccountHolderDetailsWizardPathsModel.UPDATE_PRIMARY_CONTACT_WORK;
        } else {
          path =
            AccountHolderDetailsWizardPathsModel.UPDATE_ALTERNATIVE_PRIMARY_CONTACT_WORK;
        }
        return AccountHolderDetailsWizardActions.navigateTo({
          route: getRouteFromArray([
            'account',
            accountId,
            AccountHolderDetailsWizardPathsModel.BASE_PATH,
            path,
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
        AccountHolderDetailsWizardActions.setAccountHolderContactWorkDetails
      ),
      withLatestFrom(this.store.pipe(select(selectAccountId))),
      map(([, accountId]) => {
        return AccountHolderDetailsWizardActions.navigateTo({
          route: getRouteFromArray([
            'account',
            accountId,
            AccountHolderDetailsWizardPathsModel.BASE_PATH,
            AccountHolderDetailsWizardPathsModel.CHECK_UPDATE_REQUEST,
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
      ofType(AccountHolderDetailsWizardActions.submitUpdateRequest),
      exhaustMap((action) => {
        return this.accountHolderUpdateService
          .submitRequest(action.accountHolderUpdateValues)
          .pipe(
            map((data) => {
              return AccountHolderDetailsWizardActions.submitUpdateRequestSuccess(
                {
                  requestId: data,
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

  navigateToRequestSubmitted$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AccountHolderDetailsWizardActions.submitUpdateRequestSuccess),
      withLatestFrom(this.store.pipe(select(selectAccountId))),
      map(([, accountId]) => {
        return AccountHolderDetailsWizardActions.navigateTo({
          route: getRouteFromArray([
            'account',
            accountId,
            AccountHolderDetailsWizardPathsModel.BASE_PATH,
            AccountHolderDetailsWizardPathsModel.REQUEST_SUBMITTED,
          ]),
          extras: {
            skipLocationChange: true,
          },
        });
      })
    );
  });
}
