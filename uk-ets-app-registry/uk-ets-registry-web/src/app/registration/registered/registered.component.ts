import { Component, OnDestroy, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { IUser } from 'src/app/shared/user/user';
import { cleanUpRegistration } from '../registration.actions';
import { canGoBack, clearErrors } from '@shared/shared.action';
import { Login } from 'src/app/auth/auth.actions';
import { selectUser } from '../registration.selector';

@Component({
  selector: 'app-registered',
  templateUrl: './registered.component.html',
  styleUrls: ['./registered.component.css'],
})
export class RegisteredComponent implements OnInit, OnDestroy {
  user$: Observable<IUser> = this.store.select(selectUser);

  constructor(private store: Store) {}

  ngOnInit(): void {
    this.store.dispatch(canGoBack({ goBackRoute: null }));
    this.store.dispatch(clearErrors());
  }

  ngOnDestroy(): void {
    this.store.dispatch(cleanUpRegistration());
  }

  login() {
    // Dispatch the login action
    this.store.dispatch(
      Login({
        redirectUri: location.origin + '/dashboard',
      })
    );
  }
}
