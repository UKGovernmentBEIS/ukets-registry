import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { map, mapTo, switchMap, withLatestFrom } from 'rxjs/operators';
import { selectSessionExpirationNotificationOffset } from '@shared/shared.selector';
import { KeycloakEventType } from 'keycloak-angular';
import { EMPTY, timer } from 'rxjs';
import { extendSession, Logout } from '@registry-web/auth/auth.actions';
import { Store } from '@ngrx/store';
import {
  hideTimeoutDialog,
  showTimeoutDialog,
  triggerTimeoutTimer,
} from '@registry-web/timeout/store/timeout.actions';
import { TimeoutBannerService } from '@registry-web/timeout/service/timeout-banner.service';
import { selectIsTimeoutDialogVisible } from '@registry-web/timeout/store/timeout.selectors';
import { selectSsoSessionIdleTimeout } from '@registry-web/auth/auth.selector';

@Injectable()
export class TimeoutEffects {
  triggerTimeoutTimer$ = createEffect(() =>
    this.actions$.pipe(
      ofType(triggerTimeoutTimer),
      map((action) => action.eventType),
      withLatestFrom(
        this.store.select(selectSessionExpirationNotificationOffset)
      ),
      map(([eventType, offset]: [KeycloakEventType, string]) => [
        eventType,
        Number(offset) * 1000,
      ]),
      switchMap(([eventType, offset]: [KeycloakEventType, number]) =>
        eventType === KeycloakEventType.OnTokenExpired
          ? timer(
              this.timeoutBannerService.getExpirationPeriod() - offset
            ).pipe(mapTo(showTimeoutDialog()))
          : EMPTY
      )
    )
  );

  countdownToAutoLogout$ = createEffect(() =>
    this.actions$.pipe(
      ofType(showTimeoutDialog, hideTimeoutDialog),
      withLatestFrom(
        this.store.select(selectSessionExpirationNotificationOffset),
        this.store.select(selectIsTimeoutDialogVisible),
        this.store.select(selectSsoSessionIdleTimeout)
      ),
      map(([, offset, isVisible, ssoSessionIdle]) => ({
        offset: offset * 1000,
        isVisible,
        ssoSessionIdle,
      })),
      switchMap((res) =>
        res.isVisible
          ? timer(res.offset).pipe(
              mapTo(
                Logout({
                  redirectUri:
                    location.origin + '/timed-out?idle=' + res.ssoSessionIdle,
                })
              )
            )
          : EMPTY
      )
    )
  );

  hideTimeoutDialog$ = createEffect(() =>
    this.actions$.pipe(
      ofType(hideTimeoutDialog),
      map((payload) => payload.shouldLogout),
      map((shouldLogout) =>
        shouldLogout
          ? Logout({
              redirectUri: location.origin + '/timed-out',
            })
          : extendSession()
      )
    )
  );

  constructor(
    private actions$: Actions,
    private readonly store: Store,
    private readonly timeoutBannerService: TimeoutBannerService
  ) {}
}
