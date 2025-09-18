import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import {
  canGoBack,
  navigateTo,
  retrieveUserRecoveryInfoSet,
} from '@shared/shared.action';
import { filter, map, take, tap } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { RecoveryMethodsChangeRoutePaths } from '@user-management/recovery-methods-change/recovery-methods-change.models';
import { recoveryMethodsActions } from '@user-management/recovery-methods-change/store/recovery-methods-change.actions';
import { Router } from '@angular/router';
import { selectFeatureReady } from '@user-management/recovery-methods-change/store/recovery-methods-change.reducer';
import { selectShowRecoveryNotificationPage } from '@registry-web/dashboard/recovery-methods/reducers';
import { hideRecoveryNotificationPage } from '@registry-web/dashboard/recovery-methods/actions/recovery-methods.actions';

@Component({
  selector: 'app-recovery-methods-notification',
  templateUrl: './recovery-methods-notification.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RecoveryMethodsNotificationComponent implements OnInit {
  showPage$: Observable<boolean>;
  emailSet: boolean;
  phoneSet: boolean;
  dontShowAgain: boolean = false;
  constructor(
    private store: Store,
    private router: Router
  ) {}

  ngOnInit() {
    this.store.dispatch(canGoBack({ goBackRoute: null }));
    this.store.dispatch(retrieveUserRecoveryInfoSet());

    this.showPage$ = this.store.select(selectShowRecoveryNotificationPage).pipe(
      filter((data) => data !== undefined),
      tap((data) => {
        this.emailSet = data.emailSet;
        this.phoneSet = data.phoneSet;
      }),
      map((data) => !(data.phoneSet && data.emailSet))
    );

    this.store
      .select(selectShowRecoveryNotificationPage)
      .pipe(
        filter((data) => data !== undefined),
        take(1)
      )
      .subscribe((data) => {
        if (
          (data.phoneSet && data.emailSet) ||
          data.hideNotificationPage === true
        ) {
          this.store.dispatch(
            navigateTo({
              route: `/dashboard`,
            })
          );
        }
      });
  }

  onUpdateRecoveryPhone() {
    this.store
      .select(selectFeatureReady)
      .pipe(
        tap((state) => {
          if (!state) {
            this.store.dispatch(
              navigateTo({
                route: `/${RecoveryMethodsChangeRoutePaths.BASE_PATH}/${RecoveryMethodsChangeRoutePaths.UPDATE_RECOVERY_PHONE}`,
              })
            );
          }
        }),
        filter((state) => {
          return !!state;
        }),
        take(1)
      )
      .subscribe(() => {
        this.store.dispatch(
          recoveryMethodsActions.NAVIGATE_TO_UPDATE_RECOVERY_PHONE_WIZARD({
            caller: { route: this.router.url },
            recoveryCountryCode: null,
            recoveryPhoneNumber: null,
            workMobileCountryCode: null,
            workMobilePhoneNumber: null,
          })
        );
      });
  }

  onUpdateRecoveryEmail() {
    this.store
      .select(selectFeatureReady)
      .pipe(
        tap((state) => {
          if (!state) {
            this.store.dispatch(
              navigateTo({
                route: `/${RecoveryMethodsChangeRoutePaths.BASE_PATH}/${RecoveryMethodsChangeRoutePaths.UPDATE_RECOVERY_EMAIL}`,
              })
            );
          }
        }),
        filter((state) => {
          return !!state;
        }),
        take(1)
      )
      .subscribe(() => {
        this.store.dispatch(
          recoveryMethodsActions.NAVIGATE_TO_UPDATE_RECOVERY_EMAIL_WIZARD({
            caller: { route: this.router.url },
            recoveryEmailAddress: null,
          })
        );
      });
  }

  onSkip() {
    if (this.dontShowAgain) {
      this.store.dispatch(hideRecoveryNotificationPage());
    }
    this.store.dispatch(
      navigateTo({
        route: `/dashboard`,
      })
    );
  }

  toggle(checked: boolean) {
    this.dontShowAgain = checked;
  }
}
