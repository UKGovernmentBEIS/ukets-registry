import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { concatMap } from 'rxjs/operators';
import { UserAgentActions } from '@user-agent/store/actions';
import { navigateTo } from '@shared/shared.action';
import { concatLatestFrom } from '@ngrx/operators';
import { Store } from '@ngrx/store';
import {
  selectCallerExtras,
  selectCallerRoute,
} from '@user-crc/store/selectors';
import { UserCrcActions } from '@user-crc/store/actions';

@Injectable()
export class UserCrcNavigationEffects {
  constructor(
    private actions$: Actions,
    private store: Store
  ) {}

  navigateToUserDetails$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(UserCrcActions.requestUserCrcUpdateSuccess),
      concatLatestFrom(() => [
        this.store.select(selectCallerRoute),
        this.store.select(selectCallerExtras),
      ]),
      concatMap(([action, callerRoute, extras]) => {
        return [
          navigateTo({
            route: callerRoute,
            extras,
          }),
        ];
      })
    );
  });
}
