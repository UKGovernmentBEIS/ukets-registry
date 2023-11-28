import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { ErrorDetail } from '@shared/error-summary';
import { errors } from '@shared/shared.action';
import { submitEmail } from '@user-management/emergency-otp-change/actions/emergency-otp-change.actions';
import { Observable } from 'rxjs';
import { selectRegistryConfigurationProperty } from '@shared/shared.selector';

@Component({
  selector: 'app-email-entry-container',
  template: `
    <app-email-entry
      [emailExpiration]="emailExpiration$ | async"
      (errorDetails)="onError($event)"
      (submitEmail)="onSubmitEmail($event)"
    ></app-email-entry>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmailEntryContainerComponent implements OnInit {
  emailExpiration$: Observable<number>;
  constructor(private store: Store) {}

  ngOnInit(): void {
    this.emailExpiration$ = this.store.select(
      selectRegistryConfigurationProperty,
      {
        property: 'change.otp.emergency.verification.url.expiration',
      }
    );
  }

  onError(value: ErrorDetail[]) {
    this.store.dispatch(
      errors({
        errorSummary: {
          errors: value,
        },
      })
    );
  }

  onSubmitEmail(email: string) {
    this.store.dispatch(submitEmail({ email }));
  }
}
