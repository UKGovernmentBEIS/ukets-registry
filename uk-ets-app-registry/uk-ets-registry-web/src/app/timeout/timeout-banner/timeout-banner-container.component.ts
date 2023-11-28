import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { selectSessionExpirationNotificationOffset } from '@shared/shared.selector';
import { selectIsTimeoutDialogVisible } from '@registry-web/timeout/store/timeout.selectors';
import { hideTimeoutDialog } from '@registry-web/timeout/store/timeout.actions';

@Component({
  selector: 'app-timeout-banner-container',
  template: ` <app-timeout-banner
    [offset]="offset$ | async"
    [isVisible]="isVisible$ | async"
    (extend)="extendSession()"
    (logout)="logoutNow()"
    (escKeyDown)="extendSession()"
  ></app-timeout-banner>`,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TimeoutBannerContainerComponent {
  offset$: Observable<number> = this.store.select(
    selectSessionExpirationNotificationOffset
  );

  isVisible$: Observable<boolean> = this.store.select(
    selectIsTimeoutDialogVisible
  );

  constructor(private readonly store: Store) {}

  logoutNow(): void {
    this.store.dispatch(hideTimeoutDialog({ shouldLogout: true }));
  }

  extendSession(): void {
    this.store.dispatch(hideTimeoutDialog({ shouldLogout: false }));
  }
}
