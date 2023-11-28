import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { select, Store } from '@ngrx/store';
import { map, tap, withLatestFrom } from 'rxjs/operators';
import { Router } from '@angular/router';
import { selectUpdateType } from '@authorised-representatives/reducers';
import { AuthorisedRepresentativesActions } from '@authorised-representatives/actions';
import { selectAccountId } from '@account-management/account/account-details/account.selector';
import { AuthorisedRepresentativesUpdateType } from '@authorised-representatives/model';
import { AuthorisedRepresentativesRoutePaths } from '@authorised-representatives/model/authorised-representatives-route-paths.model';

@Injectable()
export class AuthorisedRepresentativesNavigationEffects {
  constructor(
    private actions$: Actions,
    private store: Store,
    private router: Router
  ) {}

  navigateToActionWizard$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AuthorisedRepresentativesActions.setRequestUpdateType),
      withLatestFrom(this.store.pipe(select(selectAccountId))),
      map(([action, accountId]) => {
        switch (action.updateType) {
          case AuthorisedRepresentativesUpdateType.ADD:
            return AuthorisedRepresentativesActions.navigateTo({
              route: `/account/${accountId}/authorised-representatives/${AuthorisedRepresentativesRoutePaths.ADD_REPRESENTATIVE}`,
              extras: {
                skipLocationChange: true
              }
            });
          case AuthorisedRepresentativesUpdateType.REMOVE:
            return AuthorisedRepresentativesActions.fetchEligibleArs();
          case AuthorisedRepresentativesUpdateType.REPLACE:
            return AuthorisedRepresentativesActions.fetchEligibleArs();
          case AuthorisedRepresentativesUpdateType.CHANGE_ACCESS_RIGHTS:
            return AuthorisedRepresentativesActions.fetchEligibleArs();
          case AuthorisedRepresentativesUpdateType.SUSPEND:
            return AuthorisedRepresentativesActions.fetchEligibleArs();
          case AuthorisedRepresentativesUpdateType.RESTORE:
            return AuthorisedRepresentativesActions.fetchEligibleArs();
        }
      })
    );
  });

  fetchEligibleArsSuccess$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AuthorisedRepresentativesActions.fetchEligibleArsSuccess),
      withLatestFrom(
        this.store.pipe(select(selectAccountId)),
        this.store.pipe(select(selectUpdateType))
      ),
      map(([, accountId, updateType]) => {
        switch (updateType) {
          case AuthorisedRepresentativesUpdateType.REPLACE:
            return AuthorisedRepresentativesActions.navigateTo({
              route: `/account/${accountId}/authorised-representatives/${AuthorisedRepresentativesRoutePaths.REPLACE_REPRESENTATIVE}`,
              extras: {
                skipLocationChange: true
              }
            });
          default:
            return AuthorisedRepresentativesActions.navigateTo({
              route: `/account/${accountId}/authorised-representatives/${AuthorisedRepresentativesRoutePaths.SELECT_AR_TABLE}`,
              extras: {
                skipLocationChange: true
              }
            });
        }
      })
    );
  });

  navigateFromSelectAccessRightsPage$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AuthorisedRepresentativesActions.setNewAccessRights),
      withLatestFrom(this.store.pipe(select(selectAccountId))),
      map(([, accountId]) => {
        return AuthorisedRepresentativesActions.navigateTo({
          route: `/account/${accountId}/authorised-representatives/${AuthorisedRepresentativesRoutePaths.CHECK_UPDATE_REQUEST}`,
          extras: {
            skipLocationChange: true
          }
        });
      })
    );
  });

  navigateFromSelectFromTablePage$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AuthorisedRepresentativesActions.setSelectedArFromTable),
      withLatestFrom(
        this.store.pipe(select(selectAccountId)),
        this.store.pipe(select(selectUpdateType))
      ),
      map(([, accountId, updateType]) => {
        switch (updateType) {
          case AuthorisedRepresentativesUpdateType.REMOVE:
          case AuthorisedRepresentativesUpdateType.SUSPEND:
          case AuthorisedRepresentativesUpdateType.RESTORE:
            return AuthorisedRepresentativesActions.navigateTo({
              route: `/account/${accountId}/authorised-representatives/${AuthorisedRepresentativesRoutePaths.CHECK_UPDATE_REQUEST}`,
              extras: {
                skipLocationChange: true
              }
            });
          case AuthorisedRepresentativesUpdateType.CHANGE_ACCESS_RIGHTS:
            return AuthorisedRepresentativesActions.navigateTo({
              route: `/account/${accountId}/authorised-representatives/${AuthorisedRepresentativesRoutePaths.SELECT_ACCESS_RIGHTS}`,
              extras: {
                skipLocationChange: true
              }
            });
        }
      })
    );
  });

  selectAuthorisedRepresentativeSuccess$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(
        AuthorisedRepresentativesActions.selectAuthorisedRepresentativeSuccess
      ),
      withLatestFrom(this.store.pipe(select(selectAccountId))),
      map(([, accountId]) =>
        AuthorisedRepresentativesActions.navigateTo({
          route: `/account/${accountId}/authorised-representatives/${AuthorisedRepresentativesRoutePaths.SELECT_ACCESS_RIGHTS}`,
          extras: {
            skipLocationChange: true
          }
        })
      )
    );
  });

  replaceAuthorisedRepresentativeSuccess$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(
        AuthorisedRepresentativesActions.replaceAuthorisedRepresentativeSuccess
      ),
      withLatestFrom(this.store.pipe(select(selectAccountId))),
      map(([, accountId]) =>
        AuthorisedRepresentativesActions.navigateTo({
          route: `/account/${accountId}/authorised-representatives/${AuthorisedRepresentativesRoutePaths.SELECT_ACCESS_RIGHTS}`,
          extras: {
            skipLocationChange: true
          }
        })
      )
    );
  });

  navigateToRequestSubmitted$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AuthorisedRepresentativesActions.submitUpdateRequestSuccess),
      withLatestFrom(this.store.pipe(select(selectAccountId))),
      map(([, accountId]) =>
        AuthorisedRepresentativesActions.navigateTo({
          route: `/account/${accountId}/authorised-representatives/${AuthorisedRepresentativesRoutePaths.REQUEST_SUBMITTED}`,
          extras: {
            skipLocationChange: true
          }
        })
      )
    );
  });

  cancelClicked$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AuthorisedRepresentativesActions.cancelClicked),
      withLatestFrom(this.store.pipe(select(selectAccountId))),
      map(([action, accountId]) =>
        AuthorisedRepresentativesActions.navigateTo({
          route: `/account/${accountId}/authorised-representatives/${AuthorisedRepresentativesRoutePaths.CANCEL_UPDATE_REQUEST}`,
          extras: {
            queryParams: { goBackRoute: action.route },
            skipLocationChange: true
          }
        })
      )
    );
  });

  navigateTo$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(AuthorisedRepresentativesActions.navigateTo),
        tap(action => {
          this.router.navigate([action.route], action.extras);
        })
      );
    },
    { dispatch: false }
  );
}
