import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { ErrorDetail } from 'src/app/shared/error-summary/error-detail';
import { IUser } from 'src/app/shared/user/user';
import { verifyUserEmail } from '../registration.actions';
import {
  selectUser,
  selectVerificationNextStepMessage,
} from '../registration.selector';

@Component({
  selector: 'app-email-confirm',
  templateUrl: './email-confirm.component.html',
})
export class EmailConfirmComponent {
  token: string;
  user$: Observable<IUser> = this.store.select(selectUser);
  nextStepMessage$: Observable<string> = this.store.select(
    selectVerificationNextStepMessage
  );

  readonly nextRoute = '/registration/personal-details';

  constructor(
    private store: Store,
    private _router: Router,
    private route: ActivatedRoute
  ) {
    this.token = this.route.snapshot.paramMap.get('token');
    this.store.dispatch(
      verifyUserEmail({
        token: this.token,
        potentialErrors: this.potentialErrors(),
        nextStepMessages: this.nextStepMessages(),
      })
    );
  }

  potentialErrors() {
    const errorMap = new Map<any, ErrorDetail>();
    errorMap.set(
      406,
      new ErrorDetail(null, 'The email address has already been registered.')
    );
    errorMap.set(
      409,
      new ErrorDetail(null, 'The email address has already been confirmed.')
    );
    errorMap.set(
      410,
      new ErrorDetail(null, 'The email verification link has expired.')
    );
    errorMap.set(
      'other',
      new ErrorDetail(null, 'There was a problem validating the email address.')
    );
    return errorMap;
  }

  nextStepMessages() {
    const messageMap = new Map<any, string>();
    messageMap.set(
      406,
      'Please use the registered e-mail address to <a href="/dashboard">sign in</a> to the system.'
    );
    messageMap.set(
      409,
      'You need to go <a href="">back</a> and start the registration process again.'
    );
    messageMap.set(
      410,
      'You need to go <a href="">back</a> and start the registration process again.'
    );
    messageMap.set(
      'other',
      'You need to go <a href="">back</a> and start the registration process again.'
    );
    return messageMap;
  }

  next() {
    this._router.navigate([this.nextRoute], {
      skipLocationChange: false,
    });
  }
}
