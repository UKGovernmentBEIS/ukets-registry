import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Login } from '@registry-web/auth/auth.actions';
import { MENU_ROUTES } from '@shared/model/navigation-menu';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-timed-out',
  template: `
    <h1 class="govuk-heading-xl">Your session has timed out</h1>

    <p class="govuk-body">
      We have reset your session because you did not do anything for
      {{ idle }} minutes. We did this to keep your information secure.
    </p>

    <button class="govuk-button" (click)="onSignInAgain()">
      Sign in again
    </button>

    <p class="govuk-body">
      If you don't want to start again, you can
      <a href="https://www.gov.uk/">return to GOV.UK</a>
    </p>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TimedOutComponent implements OnInit {
  idle: string;

  constructor(
    private readonly store: Store,
    private readonly activatedRoute: ActivatedRoute
  ) {}

  onSignInAgain(): void {
    this.store.dispatch(
      Login({
        redirectUri: location.origin + MENU_ROUTES.DASHBOARD,
      })
    );
  }

  ngOnInit(): void {
    this.idle = this.activatedRoute.snapshot.queryParamMap.get('idle');
  }
}
