import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { EmailChangeState } from '@email-change/reducer';
import { Store } from '@ngrx/store';
import { EmailChangeRequest } from '@email-change/model';
import { requestEmailChangeAction } from '@email-change/action/email-change.actions';
import { selectState } from '@email-change/reducer/email-change.selector';
import { selectUserDetails } from '@user-management/user-details/store/reducers';
import { KeycloakUser } from '@shared/user';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { errors } from '@shared/shared.action';

@Component({
  selector: 'app-enter-new-email-form-container',
  template: `
    <app-new-email-form
      [state]="emailChangeState$ | async"
      [currentUser]="currentUser$ | async"
      (submitEmailChangeRequest)="onSubmitNewEmail($event)"
      (errorDetails)="onError($event)"
    ></app-new-email-form>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NewEmailFormContainerComponent implements OnInit {
  emailChangeState$: Observable<EmailChangeState>;
  currentUser$: Observable<KeycloakUser>;

  constructor(private store: Store) {}

  ngOnInit(): void {
    this.emailChangeState$ = this.store.select(selectState);
    this.currentUser$ = this.store.select(selectUserDetails);
  }

  onSubmitNewEmail(request: EmailChangeRequest) {
    this.store.dispatch(requestEmailChangeAction({ request }));
  }

  onError(details: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: details,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
