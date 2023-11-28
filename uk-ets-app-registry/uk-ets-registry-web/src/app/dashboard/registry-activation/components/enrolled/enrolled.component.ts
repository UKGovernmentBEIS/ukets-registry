import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { canGoBack, clearErrors } from '@shared/shared.action';
import { Router } from '@angular/router';
import { Logout } from '@registry-web/auth/auth.actions';

@Component({
  selector: 'app-enrolled',
  templateUrl: './enrolled.component.html'
})
export class EnrolledComponent implements OnInit {
  constructor(private router: Router, private store: Store) {}

  ngOnInit() {
    this.store.dispatch(clearErrors());
    this.store.dispatch(canGoBack(null));
    setTimeout(() => {
      this.logout();
    }, 3000);
  }

  goToDashboard() {
    this.logout();
  }

  logout() {
    this.store.dispatch(
      Logout({
        redirectUri: location.origin + '/dashboard'
      })
    );
  }
}
