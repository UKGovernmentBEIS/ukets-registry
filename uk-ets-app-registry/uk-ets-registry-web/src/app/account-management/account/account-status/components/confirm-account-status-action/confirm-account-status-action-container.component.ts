import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { canGoBack, clearErrors, errors } from '@shared/shared.action';
import { Observable } from 'rxjs';
import { AccountStatusActionState } from '@shared/model/account/account-status-action';
import { selectAccountStatusAction } from '../../store/reducers/account-status.selector';
import { AccountStatus } from '@shared/model/account/account-details';
import { selectAccountStatus } from '@account-management/account/account-details/account.selector';
import {
  cancelAccountStatus,
  setCommentAndSubmitAccountStatusAction
} from '../../store/actions/account-status.actions';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';

@Component({
  selector: 'app-confirm-account-status-action-container',
  template: `
    <app-confirm-account-status-action
      [currentAccountStatus]="currentAccountStatus$ | async"
      [accountStatusAction]="accountStatusAction$ | async"
      (cancelAccountStatusAction)="onCancel()"
      (comment)="onContinue($event)"
      (errorDetails)="onError($event)"
    >
    </app-confirm-account-status-action>
  `,
  styles: [],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ConfirmAccountStatusActionContainerComponent implements OnInit {
  currentAccountStatus$: Observable<AccountStatus>;
  accountStatusAction$: Observable<AccountStatusActionState>;

  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit() {
    this.store.dispatch(clearErrors());
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.route.snapshot.paramMap.get(
          'accountId'
        )}/status`
      })
    );
    this.currentAccountStatus$ = this.store.select(selectAccountStatus);
    this.accountStatusAction$ = this.store.select(selectAccountStatusAction);
  }

  onContinue(value: string) {
    this.store.dispatch(
      setCommentAndSubmitAccountStatusAction({ comment: value })
    );
  }

  onCancel() {
    this.store.dispatch(cancelAccountStatus());
  }

  onError(details: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: details
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
