import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { canGoBack, errors } from '@shared/shared.action';
import { PasswordChangeRequest } from '@password-change/model';
import { requestPasswordChangeAction } from '@password-change/action/password-change.actions';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { Observable } from "rxjs";
import { Configuration } from "@shared/configuration/configuration.interface";
import { selectConfigurationRegistry } from "@shared/shared.selector";

@Component({
  selector: 'app-new-password-form-container',
  template: `
    <app-new-password-form
      [configuration]="configuration$ | async"
      (submitPasswordChangeRequest)="onSubmitNewPassword($event)"
      (errorDetails)="onError($event)"
    ></app-new-password-form>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NewPasswordFormContainerComponent implements OnInit {
  constructor(private store: Store) {}

  configuration$: Observable<Configuration[]>;

  ngOnInit(): void {
    this.store.dispatch(
      canGoBack({
        goBackRoute: '/user-details/my-profile',
        extras: { skipLocationChange: false },
      })
    );
    this.configuration$ = this.store.select(selectConfigurationRegistry);
  }

  onSubmitNewPassword(request: PasswordChangeRequest): void {
    this.store.dispatch(requestPasswordChangeAction({ request }));
  }

  onError(details: ErrorDetail[]): void {
    const summary: ErrorSummary = {
      errors: details,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
