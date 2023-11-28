import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { navigateTo } from '@shared/shared.action';
import { Observable } from 'rxjs';
import { EmailChangeConfirmation } from '@email-change/model/email-change.model';
import { selectConfirmation } from '@email-change/reducer/email-change.selector';
import { EmailChangeRoutePath } from '@email-change/model';
import { Logout } from '../../../../auth/auth.actions';

@Component({
  selector: 'app-email-change-confirmation-container',
  template: `
    <app-email-change-confirmation
      [confirmation]="confirmation$ | async"
      (restart)="onRestart()"
      (signIn)="onSignIn()"
      (backToMyProfile)="onBackToMyProfile()"
    ></app-email-change-confirmation>
  `
})
export class EmailChangeConfirmationContainerComponent implements OnInit {
  confirmation$: Observable<EmailChangeConfirmation>;

  constructor(private store: Store) {}

  ngOnInit(): void {
    this.confirmation$ = this.store.select(selectConfirmation);
  }

  onSignIn() {
    this.store.dispatch(
      Logout({
        redirectUri: location.origin + '/dashboard'
      })
    );
  }

  onBackToMyProfile() {
    this.store.dispatch(
      navigateTo({
        route: '/user-details/my-profile'
      })
    );
  }

  onRestart() {
    this.store.dispatch(
      navigateTo({
        route: `/${EmailChangeRoutePath.BASE_PATH}`
      })
    );
  }
}
