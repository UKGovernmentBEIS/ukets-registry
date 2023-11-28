import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { Store } from '@ngrx/store';
import { requestResetPasswordEmail } from '../../store/actions/forgot-password.actions';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { errors, canGoBack } from '@shared/shared.action';
import {
  selectCookiesAccepted,
  selectResetPasswordUrlExpirationConfiguration
} from '@shared/shared.selector';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-submit-email-container',
  template: `
    <app-submit-email
      [cookiesAccepted]="cookiesAccepted$ | async"
      [linkExpiration]="linkExpiration$ | async"
      (emailAddress)="onSubmitRequestResetPasswordEmailAddress($event)"
      (errorDetails)="onError($event)"
    >
    </app-submit-email>
  `,
  styles: [],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SubmitEmailContainerComponent implements OnInit {
  cookiesAccepted$: Observable<boolean>;

  linkExpiration$ = this.store.select(
    selectResetPasswordUrlExpirationConfiguration
  );

  constructor(private store: Store) {}

  ngOnInit(): void {
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/`
      })
    );
    this.cookiesAccepted$ = this.store.select(selectCookiesAccepted);
  }

  onSubmitRequestResetPasswordEmailAddress(emailAddress: string) {
    this.store.dispatch(requestResetPasswordEmail({ email: emailAddress }));
  }

  onError(details: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: details
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
