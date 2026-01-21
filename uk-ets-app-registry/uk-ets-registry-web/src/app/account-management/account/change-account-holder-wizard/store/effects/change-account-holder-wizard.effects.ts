import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { Router } from '@angular/router';
import { catchError, exhaustMap, map, mergeMap, tap } from 'rxjs/operators';
import { concatLatestFrom } from '@ngrx/operators';
import { HttpErrorResponse } from '@angular/common/http';
import { of } from 'rxjs';
import { errors } from '@shared/shared.action';
import { getRouteFromArray } from '@shared/utils/router.utils';
import {
  CHANGE_ACCOUNT_HOLDER_BASE_PATH,
  ChangeAccountHolderWizardPaths,
} from '@change-account-holder-wizard/model/change-account-holder-wizard-paths.model';
import { ChangeAccountHolderWizardActions } from '@change-account-holder-wizard/store/actions';
import { ApiErrorHandlingService } from '@shared/services';
import { AccountHolderService } from '@account-opening/account-holder/account-holder.service';
import { AccountHolderContactService } from '@account-opening/account-holder-contact/account-holder-contact.service';
import { AccountHolderChangeService } from '@change-account-holder-wizard/service';
import {
  AccountHolderSelectionType,
  AccountHolderType,
} from '@shared/model/account';
import { selectAccountId } from '@account-management/account/account-details/account.selector';
import {
  selectAcquiringAccountHolder,
  selectAcquiringAccountHolderContact,
  selectAccountHolderChangeActionType,
  selectAcquiringAccountHolderType,
  selectAccountHolderDelete,
  selectIsAccountHolderOrphan,
} from '@change-account-holder-wizard/store/selectors';

@Injectable()
export class ChangeAccountHolderWizardEffects {
  constructor(
    private actions$: Actions,
    private store: Store,
    private router: Router,
    private apiErrorHandlingService: ApiErrorHandlingService,
    private accountHolderService: AccountHolderService,
    private accountHolderContactService: AccountHolderContactService,
    private accountHolderChangeService: AccountHolderChangeService
  ) {}

  navigateTo$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(ChangeAccountHolderWizardActions.NAVIGATE_TO),
        concatLatestFrom(() => this.store.select(selectAccountId)),
        tap(([action, accountId]) => {
          this.router.navigate(
            [
              getRouteFromArray([
                'account',
                accountId,
                CHANGE_ACCOUNT_HOLDER_BASE_PATH,
                action.step,
              ]),
            ],
            {
              skipLocationChange: true,
              ...(action.extras || {}),
            }
          );
        })
      );
    },
    { dispatch: false }
  );

  cancelClicked$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(ChangeAccountHolderWizardActions.CANCEL),
      map((action) =>
        ChangeAccountHolderWizardActions.NAVIGATE_TO({
          step: ChangeAccountHolderWizardPaths.CANCEL_REQUEST,
          extras: { queryParams: { goBackRoute: action.route } },
        })
      )
    );
  });

  cancelAccountHolderUpdateRequest$ = createEffect(() =>
    this.actions$.pipe(
      ofType(
        ChangeAccountHolderWizardActions.CANCEL_CHANGE_ACCOUNT_HOLDER_REQUEST
      ),
      map(() =>
        ChangeAccountHolderWizardActions.CLEAR_ACCOUNT_HOLDER_CHANGE_REQUEST()
      )
    )
  );

  navigateOnCancel$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(
          ChangeAccountHolderWizardActions.CANCEL_CHANGE_ACCOUNT_HOLDER_REQUEST
        ),
        concatLatestFrom(() => this.store.select(selectAccountId)),
        tap(([, accountId]) => {
          this.router.navigate([`/account/${accountId}`]);
        })
      );
    },
    { dispatch: false }
  );

  completeSelectTypeStep$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(ChangeAccountHolderWizardActions.SET_ACCOUNT_HOLDER_TYPE),
      map(() =>
        ChangeAccountHolderWizardActions.NAVIGATE_TO({
          step: ChangeAccountHolderWizardPaths.SELECT_EXISTING_OR_ADD_NEW,
        })
      )
    );
  });

  setAccountHolderSelectionType$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(
        ChangeAccountHolderWizardActions.SET_ACCOUNT_HOLDER_SELECTION_TYPE
      ),
      concatLatestFrom(() =>
        this.store.select(selectAcquiringAccountHolderType)
      ),
      map(([action, accountHolderType]) => {
        if (action.selectionType === AccountHolderSelectionType.NEW) {
          let step;
          if (accountHolderType === AccountHolderType.INDIVIDUAL) {
            step = ChangeAccountHolderWizardPaths.INDIVIDUAL_DETAILS;
          } else if (accountHolderType === AccountHolderType.ORGANISATION) {
            step = ChangeAccountHolderWizardPaths.ORGANISATION_DETAILS;
          }
          return ChangeAccountHolderWizardActions.NAVIGATE_TO({ step });
        }

        return ChangeAccountHolderWizardActions.FETCH_ACCOUNT_HOLDER_BY_ID({
          id: action.id,
        });
      })
    );
  });

  navigateToIndividualContactDetailsAddressStep$ = createEffect(() =>
    this.actions$.pipe(
      ofType(
        ChangeAccountHolderWizardActions.SET_ACCOUNT_HOLDER_INDIVIDUAL_DETAILS
      ),
      map(() =>
        ChangeAccountHolderWizardActions.NAVIGATE_TO({
          step: ChangeAccountHolderWizardPaths.INDIVIDUAL_CONTACT,
        })
      )
    )
  );

  navigateToOrganisationAddressStep$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(
        ChangeAccountHolderWizardActions.SET_ACCOUNT_HOLDER_ORGANISATION_DETAILS
      ),
      map(() =>
        ChangeAccountHolderWizardActions.NAVIGATE_TO({
          step: ChangeAccountHolderWizardPaths.ORGANISATION_ADDRESS,
        })
      )
    );
  });

  navigateToPrimaryContactStep$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(
        ChangeAccountHolderWizardActions.SET_ACCOUNT_HOLDER_INDIVIDUAL_CONTACT_DETAILS,
        ChangeAccountHolderWizardActions.SET_ACCOUNT_HOLDER_ORGANISATION_ADDRESS
      ),
      map(() =>
        ChangeAccountHolderWizardActions.NAVIGATE_TO({
          step: ChangeAccountHolderWizardPaths.PRIMARY_CONTACT,
        })
      )
    );
  });

  navigateToPrimaryContactWorkStep$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(
        ChangeAccountHolderWizardActions.SET_ACCOUNT_HOLDER_CONTACT_DETAILS
      ),
      map(() =>
        ChangeAccountHolderWizardActions.NAVIGATE_TO({
          step: ChangeAccountHolderWizardPaths.PRIMARY_CONTACT_WORK,
        })
      )
    );
  });

  fetchAccountHolderById$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ChangeAccountHolderWizardActions.FETCH_ACCOUNT_HOLDER_BY_ID),
      mergeMap((action) => {
        return this.accountHolderService
          .fetchOne({ identifier: action.id })
          .pipe(
            map((result) => {
              return ChangeAccountHolderWizardActions.POPULATE_ACQUIRING_ACCOUNT_HOLDER(
                { accountHolder: result }
              );
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

  fetchAccountHolderPrimaryContact$ = createEffect(() =>
    this.actions$.pipe(
      ofType(
        ChangeAccountHolderWizardActions.POPULATE_ACQUIRING_ACCOUNT_HOLDER
      ),
      mergeMap((action) => {
        return this.accountHolderContactService
          .fetchAccountHolderContacts(action.accountHolder.id.toString())
          .pipe(
            map((result) => {
              return ChangeAccountHolderWizardActions.POPULATE_ACQUIRING_ACCOUNT_HOLDER_PRIMARY_CONTACT(
                { accountHolderContact: result.primaryContact }
              );
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

  completeAccountHolderContact$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(
        ChangeAccountHolderWizardActions.POPULATE_ACQUIRING_ACCOUNT_HOLDER_PRIMARY_CONTACT,
        ChangeAccountHolderWizardActions.SET_ACCOUNT_HOLDER_CONTACT_ADDRESS
      ),
      concatLatestFrom(() => this.store.select(selectIsAccountHolderOrphan)),
      map(([, isAccountHolderOrphan]) =>
        ChangeAccountHolderWizardActions.NAVIGATE_TO({
          step: isAccountHolderOrphan
            ? ChangeAccountHolderWizardPaths.DELETE_ORPHAN_ACCOUNT_HOLDER
            : ChangeAccountHolderWizardPaths.OVERVIEW,
        })
      )
    );
  });

  completeDeleteAccountHolderStep$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ChangeAccountHolderWizardActions.SET_DELETE_ORPHAN_ACCOUNT_HOLDER),
      map(() =>
        ChangeAccountHolderWizardActions.NAVIGATE_TO({
          step: ChangeAccountHolderWizardPaths.OVERVIEW,
        })
      )
    )
  );

  submitChangeAccountHolderRequest$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(
        ChangeAccountHolderWizardActions.SUBMIT_CHANGE_ACCOUNT_HOLDER_REQUEST
      ),
      concatLatestFrom(() => [
        this.store.select(selectAccountId),
        this.store.select(selectAccountHolderChangeActionType),
        this.store.select(selectAcquiringAccountHolder),
        this.store.select(selectAcquiringAccountHolderContact),
        this.store.select(selectAccountHolderDelete),
      ]),
      exhaustMap(
        ([
          ,
          accountIdentifier,
          accountHolderChangeActionType,
          acquiringAccountHolder,
          acquiringAccountHolderContactInfo,
          accountHolderDelete,
        ]) => {
          return this.accountHolderChangeService
            .submitChangeAccountHolderRequest({
              accountIdentifier,
              accountHolderChangeActionType,
              acquiringAccountHolder,
              acquiringAccountHolderContactInfo,
              accountHolderDelete,
            })
            .pipe(
              map((data) =>
                ChangeAccountHolderWizardActions.SUBMIT_CHANGE_ACCOUNT_HOLDER_REQUEST_SUCCESS(
                  { requestId: data }
                )
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
        }
      )
    );
  });

  navigateToRequestSubmitted$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(
        ChangeAccountHolderWizardActions.SUBMIT_CHANGE_ACCOUNT_HOLDER_REQUEST_SUCCESS
      ),
      map(() =>
        ChangeAccountHolderWizardActions.NAVIGATE_TO({
          step: ChangeAccountHolderWizardPaths.REQUEST_SUBMITTED,
        })
      )
    );
  });

  fetchAccountHolderList$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ChangeAccountHolderWizardActions.FETCH_ACCOUNT_HOLDER_LIST),
      mergeMap(({ holderType }) =>
        this.accountHolderService.fetchList(holderType).pipe(
          map((result) =>
            ChangeAccountHolderWizardActions.POPULATE_ACCOUNT_HOLDER_LIST({
              list: result,
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
        )
      )
    )
  );
}
