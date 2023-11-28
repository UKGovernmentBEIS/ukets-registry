import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Router } from '@angular/router';
import { EmergencyOtpChangeRoutes } from '@user-management/emergency-otp-change/model/emergency-otp-change.model';
import { canGoBack } from '@shared/shared.action';
import { selectCookiesAccepted } from '@shared/shared.selector';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-emergency-otp-change-init-container',
  template: `
    <app-emergency-otp-change-init
      [cookiesAccepted]="cookiesAccepted$ | async"
      (continueToEmail)="onContinueToEmail()"
    ></app-emergency-otp-change-init>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class EmergencyOtpChangeInitContainerComponent implements OnInit {
  cookiesAccepted$: Observable<boolean>;

  constructor(private store: Store, private router: Router) {}

  ngOnInit(): void {
    this.store.dispatch(canGoBack({ goBackRoute: '/dashboard' }));
    this.cookiesAccepted$ = this.store.select(selectCookiesAccepted);
  }

  onContinueToEmail() {
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/${EmergencyOtpChangeRoutes.ROOT}/${EmergencyOtpChangeRoutes.INIT}`
      })
    );

    this.router.navigate(
      [EmergencyOtpChangeRoutes.ROOT, EmergencyOtpChangeRoutes.EMAIL_ENTRY],
      {
        skipLocationChange: true
      }
    );
  }
}
