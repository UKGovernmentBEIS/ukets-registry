import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import {canGoBack, clearErrors} from '@shared/shared.action';
import {
  fetchAccount,
} from '@account-management/account/account-details/account.actions';

@Component({
  selector: 'app-submit-success-change-description-container',
  template: `<app-submit-success-change-description
    [backLinkMessage]="'Back to the account'"
    [backPath]="'/account/' + accountId"
    [message]="'The account description has been updated'"
  ></app-submit-success-change-description>`,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubmitSuccessChangeDescriptionContainerComponent
  implements OnInit {
  accountId: string;

  constructor(
    private route: ActivatedRoute,
    private _router: Router,
    private store: Store
  ) {}

  initConfirmation() {
    this.accountId = this.route.snapshot.paramMap.get('accountId');
    this.store.dispatch(clearErrors());
    this.store.dispatch(fetchAccount({ accountId: this.accountId }));
    this.store.dispatch(canGoBack({ goBackRoute: null }));
  }

  ngOnInit(): void {
    this.initConfirmation();
  }
}
