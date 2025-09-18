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
import { ChangeAccountHolderWizardPathsModel } from '@change-account-holder-wizard/model/change-account-holder-wizard-paths.model';
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
  selectChangeAccountHolderType,
} from '@change-account-holder-wizard/store/reducers';

@Injectable()
export class ChangeAccountHolderWizardNavigationEffects {
  constructor(
    private actions$: Actions,
    private store: Store,
    private router: Router,
    private apiErrorHandlingService: ApiErrorHandlingService,
    private accountHolderService: AccountHolderService,
    private accountHolderContactService: AccountHolderContactService,
    private accountHolderChangeService: AccountHolderChangeService
  ) {}

  cancelClicked$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(ChangeAccountHolderWizardActions.cancelClicked),
      concatLatestFrom(() => this.store.select(selectAccountId)),
      map(([action, accountId]) =>
        ChangeAccountHolderWizardActions.navigateTo({
          route: getRouteFromArray([
            'account',
            accountId,
            ChangeAccountHolderWizardPathsModel.BASE_PATH,
            ChangeAccountHolderWizardPathsModel.CANCEL_CHANGE_ACCOUNT_HOLDER_REQUEST,
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
      ofType(ChangeAccountHolderWizardActions.cancelChangeAccountHolderRequest),
      concatLatestFrom(() => this.store.select(selectAccountId)),
      mergeMap(([, accountId]) => [
        ChangeAccountHolderWizardActions.clearAccountHolderChangeRequest(),
        ChangeAccountHolderWizardActions.navigateTo({
          route: `/account/${accountId}`,
        }),
      ])
    );
  });

  navigateTo$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(ChangeAccountHolderWizardActions.navigateTo),
        tap((action) => {
          this.router.navigate([action.route], action.extras);
        })
      );
    },
    { dispatch: false }
  );

  navigateToAccountHolderTypeWizard$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(ChangeAccountHolderWizardActions.setAccountHolderType),
      concatLatestFrom(() => this.store.select(selectAccountId)),
      map(([, accountId]) => {
        return ChangeAccountHolderWizardActions.navigateTo({
          route: getRouteFromArray([
            'account',
            accountId,
            ChangeAccountHolderWizardPathsModel.BASE_PATH,
            ChangeAccountHolderWizardPathsModel.ACCOUNT_HOLDER_SELECTION,
          ]),
          extras: {
            skipLocationChange: true,
          },
        });
      })
    );
  });

  navigateToAccountHolderDetails$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(ChangeAccountHolderWizardActions.navigateToAccountHolderDetails),
      concatLatestFrom(() => [
        this.store.select(selectAccountId),
        this.store.select(selectChangeAccountHolderType),
      ]),
      map(([, accountId, accountHolderType]) => {
        let detailsPath;
        if (accountHolderType === AccountHolderType.INDIVIDUAL) {
          detailsPath = ChangeAccountHolderWizardPathsModel.INDIVIDUAL_DETAILS;
        } else if (accountHolderType === AccountHolderType.ORGANISATION) {
          detailsPath =
            ChangeAccountHolderWizardPathsModel.ORGANISATION_DETAILS;
        }
        return ChangeAccountHolderWizardActions.navigateTo({
          route: getRouteFromArray([
            'account',
            accountId,
            ChangeAccountHolderWizardPathsModel.BASE_PATH,
            detailsPath,
          ]),
          extras: {
            skipLocationChange: true,
          },
        });
      })
    );
  });

  setAccountHolderSelectionType$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(ChangeAccountHolderWizardActions.setAccountHolderSelectionType),
      concatLatestFrom(() => [
        this.store.select(selectAccountId),
        this.store.select(selectChangeAccountHolderType),
      ]),
      map(([accountHolderSelection, accountId, accountHolderType]) => {
        if (
          accountHolderSelection.selectionType ===
          AccountHolderSelectionType.NEW
        ) {
          let detailsPath;
          if (accountHolderType === AccountHolderType.INDIVIDUAL) {
            detailsPath =
              ChangeAccountHolderWizardPathsModel.INDIVIDUAL_DETAILS;
          } else if (accountHolderType === AccountHolderType.ORGANISATION) {
            detailsPath =
              ChangeAccountHolderWizardPathsModel.ORGANISATION_DETAILS;
          }
          return ChangeAccountHolderWizardActions.navigateTo({
            route: getRouteFromArray([
              'account',
              accountId,
              ChangeAccountHolderWizardPathsModel.BASE_PATH,
              detailsPath,
            ]),
            extras: {
              skipLocationChange: true,
            },
          });
        } else {
          return ChangeAccountHolderWizardActions.fetchAccountHolderById({
            id: accountHolderSelection.id,
          });
        }
      })
    );
  });

  navigateToAccountHolderIndividualContactDetailsAdressWizard$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(
          ChangeAccountHolderWizardActions.setAccountHolderIndividualDetails
        ),
        concatLatestFrom(() => [this.store.select(selectAccountId)]),
        map(([, accountId]) => {
          return ChangeAccountHolderWizardActions.navigateTo({
            route: getRouteFromArray([
              'account',
              accountId,
              ChangeAccountHolderWizardPathsModel.BASE_PATH,
              ChangeAccountHolderWizardPathsModel.INDIVIDUAL_CONTACT_DETAILS,
            ]),
            extras: {
              skipLocationChange: true,
            },
          });
        })
      );
    }
  );

  navigateToAccountHolderOrganisationAdressWizard$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(
        ChangeAccountHolderWizardActions.setAccountHolderOrganisationDetails
      ),
      concatLatestFrom(() => [this.store.select(selectAccountId)]),
      map(([, accountId]) => {
        return ChangeAccountHolderWizardActions.navigateTo({
          route: getRouteFromArray([
            'account',
            accountId,
            ChangeAccountHolderWizardPathsModel.BASE_PATH,
            ChangeAccountHolderWizardPathsModel.ORGANISATION_ADDRESS_DETAILS,
          ]),
          extras: {
            skipLocationChange: true,
          },
        });
      })
    );
  });

  navigateToAccountHolderContactWizard$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(
        ChangeAccountHolderWizardActions.setAccountHolderIndividualContactDetails,
        ChangeAccountHolderWizardActions.setAccountHolderOrganisationAddress
      ),
      concatLatestFrom(() => [this.store.select(selectAccountId)]),
      map(([, accountId]) => {
        return ChangeAccountHolderWizardActions.navigateTo({
          route: getRouteFromArray([
            'account',
            accountId,
            ChangeAccountHolderWizardPathsModel.BASE_PATH,
            ChangeAccountHolderWizardPathsModel.PRIMARY_CONTACT,
          ]),
          extras: {
            skipLocationChange: true,
          },
        });
      })
    );
  });

  navigateToAccountHolderContactAddressWizard$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(ChangeAccountHolderWizardActions.setAccountHolderContactDetails),
      concatLatestFrom(() => [this.store.select(selectAccountId)]),
      map(([, accountId]) => {
        return ChangeAccountHolderWizardActions.navigateTo({
          route: getRouteFromArray([
            'account',
            accountId,
            ChangeAccountHolderWizardPathsModel.BASE_PATH,
            ChangeAccountHolderWizardPathsModel.PRIMARY_CONTACT_WORK,
          ]),
          extras: {
            skipLocationChange: true,
          },
        });
      })
    );
  });

  fetchAccountHolderById$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ChangeAccountHolderWizardActions.fetchAccountHolderById),
      mergeMap((action) => {
        return this.accountHolderService
          .fetchOne({ identifier: action.id })
          .pipe(
            map((result) => {
              return ChangeAccountHolderWizardActions.populateAcquiringAccountHolder(
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
      ofType(ChangeAccountHolderWizardActions.populateAcquiringAccountHolder),
      mergeMap((action) => {
        return this.accountHolderContactService
          .fetchAccountHolderContacts(action.accountHolder.id.toString())
          .pipe(
            map((result) => {
              return ChangeAccountHolderWizardActions.populateAcquiringAccountHolderPrimaryContact(
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

  navigateToCheckChangeAccountHolderPage$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(
        ChangeAccountHolderWizardActions.populateAcquiringAccountHolderPrimaryContact,
        ChangeAccountHolderWizardActions.setAccountHolderContactAddress
      ),
      concatLatestFrom(() => this.store.select(selectAccountId)),
      map(([, accountId]) => {
        return ChangeAccountHolderWizardActions.navigateTo({
          route: getRouteFromArray([
            'account',
            accountId,
            ChangeAccountHolderWizardPathsModel.BASE_PATH,
            ChangeAccountHolderWizardPathsModel.CHECK_CHANGE_ACCOUNT_HOLDER,
          ]),
          extras: {
            skipLocationChange: true,
          },
        });
      })
    );
  });

  submitChangeAccountHolderRequest$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(ChangeAccountHolderWizardActions.submitChangeAccountHolderRequest),
      concatLatestFrom(() => [
        this.store.select(selectAccountId),
        this.store.select(selectAcquiringAccountHolder),
        this.store.select(selectAcquiringAccountHolderContact),
      ]),
      exhaustMap(
        ([, accountIdentifier, accountHolder, accountHolderContact]) => {
          return this.accountHolderChangeService
            .submitChangeAccountHolderRequest({
              accountIdentifier: accountIdentifier,
              acquiringAccountHolder: accountHolder,
              acquiringAccountHolderContactInfo: accountHolderContact,
            })
            .pipe(
              map((data) => {
                return ChangeAccountHolderWizardActions.submitChangeAccountHolderRequestSuccess();
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
        }
      )
    );
  });

  navigateToRequestSubmitted$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(
        ChangeAccountHolderWizardActions.submitChangeAccountHolderRequestSuccess
      ),
      concatLatestFrom(() => this.store.select(selectAccountId)),
      map(([, accountId]) => {
        return ChangeAccountHolderWizardActions.navigateTo({
          route: getRouteFromArray([
            'account',
            accountId,
            ChangeAccountHolderWizardPathsModel.BASE_PATH,
            ChangeAccountHolderWizardPathsModel.REQUEST_SUBMITTED,
          ]),
          extras: {
            skipLocationChange: true,
          },
        });
      })
    );
  });

  fetchAccountHolderList$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ChangeAccountHolderWizardActions.fetchAccountHolderList),
      mergeMap(({ holderType }) =>
        this.accountHolderService.fetchList(holderType).pipe(
          map((result) =>
            ChangeAccountHolderWizardActions.populateAccountHolderList({
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
