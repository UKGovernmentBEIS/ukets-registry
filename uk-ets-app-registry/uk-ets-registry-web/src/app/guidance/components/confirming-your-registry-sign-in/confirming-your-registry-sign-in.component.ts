import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { canGoBack } from '@shared/shared.action';

@Component({
  selector: 'app-confirming-your-registry-sign-in',
  templateUrl: './confirming-your-registry-sign-in.component.html',
})
export class ConfirmingYourRegistrySignInComponent implements OnInit {
  constructor(private store: Store) {}

  ngOnInit(): void {
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/guidance`,
      })
    );
  }
}
