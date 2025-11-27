import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { isAdmin } from '@registry-web/auth/auth.selector';
import { canGoBack } from '@registry-web/shared/shared.action';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-change-account-holder-request-submitted-container',
  template: `
    <app-request-submitted
      [confirmationMessageTitle]="'The account holder has been changed'"
      [accountId]="accountId"
      [isAdmin]="isAdmin$ | async"
    ></app-request-submitted>
  `,
  styles: ``,
})
export class RequestSubmittedContainerComponent implements OnInit {
  submittedIdentifier$: Observable<string>;
  accountId: string;
  isAdmin$: Observable<boolean>;

  constructor(
    private store: Store,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.isAdmin$ = this.store.select(isAdmin);
    this.store.dispatch(canGoBack({ goBackRoute: null }));
    this.accountId = this.route.snapshot.paramMap.get('accountId');
  }
}
