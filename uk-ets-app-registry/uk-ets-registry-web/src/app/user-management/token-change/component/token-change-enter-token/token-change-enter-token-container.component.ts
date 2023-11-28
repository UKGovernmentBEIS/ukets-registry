import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { actionSubmitToken } from '@user-management/token-change/action/token-change.actions';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { errors } from '@shared/shared.action';

@Component({
  selector: 'app-token-change-enter-token-container',
  template: `
    <app-token-change-enter-token
      (submitOtpCode)="onSubmitToken($event)"
      (errorDetails)="onError($event)"
    ></app-token-change-enter-token>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TokenChangeEnterTokenContainerComponent {
  constructor(private store: Store) {}

  onSubmitToken(otp: string) {
    this.store.dispatch(
      actionSubmitToken({
        otp
      })
    );
  }

  onError(details: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: details
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
