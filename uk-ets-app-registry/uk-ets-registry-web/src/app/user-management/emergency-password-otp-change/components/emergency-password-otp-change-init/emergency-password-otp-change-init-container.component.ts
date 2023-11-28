import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Router } from '@angular/router';
import { canGoBack } from '@shared/shared.action';
import { EmergencyPasswordOtpChangeRoutes } from '@user-management/emergency-password-otp-change/model';
import { selectCookiesAccepted } from '@shared/shared.selector';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-emergency-password-otp-change-init-container',
  template: `
    <app-emergency-password-otp-change-init
      [cookiesAccepted]="cookiesAccepted$ | async"
      (continueToEmail)="onContinueToEmail()"
    ></app-emergency-password-otp-change-init>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class EmergencyPasswordOtpChangeInitContainerComponent
  implements OnInit {
  cookiesAccepted$: Observable<boolean>;

  constructor(private store: Store, private router: Router) {}

  ngOnInit(): void {
    this.store.dispatch(canGoBack({ goBackRoute: '/dashboard' }));
    this.cookiesAccepted$ = this.store.select(selectCookiesAccepted);
  }

  onContinueToEmail() {
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/${EmergencyPasswordOtpChangeRoutes.ROOT}/${EmergencyPasswordOtpChangeRoutes.INIT}`
      })
    );

    this.router.navigate(
      [
        EmergencyPasswordOtpChangeRoutes.ROOT,
        EmergencyPasswordOtpChangeRoutes.EMAIL_ENTRY
      ],
      {
        skipLocationChange: true
      }
    );
  }
}
