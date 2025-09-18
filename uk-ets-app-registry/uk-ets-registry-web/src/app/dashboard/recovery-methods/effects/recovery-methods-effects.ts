import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, map, switchMap, withLatestFrom } from 'rxjs/operators';
import {
  retrieveUserRecoveryInfoSet,
  retrieveUserRecoveryInfoSetError,
} from '@shared/shared.action';
import { of } from 'rxjs';
import { Store } from '@ngrx/store';
import { UserDetailService } from '@user-management/service';
import { RecoveryMethodsChangeService } from '@user-management/recovery-methods-change/recovery-methods-change.service';
import {
  hideRecoveryNotificationPage,
  hideRecoveryNotificationPageSuccess,
  retrieveUserRecoveryInfoSetSuccess,
} from '@registry-web/dashboard/recovery-methods/actions/recovery-methods.actions';
import { selectUrid } from '@registry-web/auth/auth.selector';

@Injectable()
export class RecoveryMethodsEffects {
  constructor(
    private actions$: Actions,
    private store: Store,
    private userDetailsService: UserDetailService,
    private recoveryMethodsService: RecoveryMethodsChangeService
  ) {}

  retrieveUserRecoveryInfoHasBeenSet$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(retrieveUserRecoveryInfoSet),
      withLatestFrom(this.store.select(selectUrid)),
      switchMap(([_, urid]) => {
        return this.userDetailsService.getUserDetail(urid).pipe(
          map((user) => {
            if (!user) {
              return null;
            }
            let hideNotificationPage = false;
            if (user.attributes.hideRecoveryMethodsNotification?.length > 0) {
              hideNotificationPage =
                user.attributes.hideRecoveryMethodsNotification[0] === 'true';
            }
            return retrieveUserRecoveryInfoSetSuccess({
              phoneSet: !!user.attributes.recoveryPhoneNumber,
              emailSet: !!user.attributes.recoveryEmailAddress,
              hideRecoveryMethodsNotification: hideNotificationPage,
            });
          })
        );
      }),
      catchError((err) => of(retrieveUserRecoveryInfoSetError(err)))
    );
  });

  hideNotificationPage$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(hideRecoveryNotificationPage),
      switchMap(() => {
        return this.recoveryMethodsService.hideRecoveryMethods().pipe(
          map(() => {
            return hideRecoveryNotificationPageSuccess({
              hideRecoveryNotificationPage: true,
            });
          })
        );
      }),
      catchError((err) => of(retrieveUserRecoveryInfoSetError(err)))
    );
  });
}
