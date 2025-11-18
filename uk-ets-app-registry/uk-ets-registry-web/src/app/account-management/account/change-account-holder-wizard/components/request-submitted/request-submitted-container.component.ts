import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { isAdmin } from '@registry-web/auth/auth.selector';
import { canGoBack } from '@registry-web/shared/shared.action';
import { selectSubmittedRequestIdentifier } from '@registry-web/account-management/account/change-account-holder-wizard/store/selectors';

@Component({
  selector: 'app-change-account-holder-request-submitted-container',
  template: `
    <app-request-submitted
      [confirmationMessageTitle]="
        'You have submitted a request to change the account holder.'
      "
      [confirmationMessageBody]="'Your request ID'"
      [accountId]="accountId"
      [isAdmin]="isAdmin$ | async"
      [submittedIdentifier]="submittedIdentifier$ | async"
    />
  `,
})
export class RequestSubmittedContainerComponent implements OnInit {
  readonly isAdmin$ = this.store.select(isAdmin);
  readonly submittedIdentifier$ = this.store.select(
    selectSubmittedRequestIdentifier
  );
  accountId: string;

  constructor(
    private store: Store,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.store.dispatch(canGoBack({ goBackRoute: null }));
    this.accountId = this.route.snapshot.paramMap.get('accountId');
  }
}
