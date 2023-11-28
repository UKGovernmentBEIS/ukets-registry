import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { Store } from '@ngrx/store';
import {
  selectAccountFullIdentifier,
  selectAccountHolderName,
  selectAccountName,
  selectCandidateRecipients,
  selectComment,
  selectRecipientUrid
} from '@request-documents/wizard/reducers/request-document.selector';
import { User } from '@shared/user';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { canGoBack, errors } from '@shared/shared.action';
import {
  cancelRequestDocuments,
  setRecipient
} from '@request-documents/wizard/actions';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-select-recipient-container',
  template: `
    <app-select-recipient
      [accountName]="accountName$ | async"
      [accountNumber]="accountNumber$ | async"
      [accountHolderName]="accountHolderName$ | async"
      [candidateRecipients]="candidateRecipients$ | async"
      [recipientUrid]="recipientUrid$ | async"
      [comment]="comment$ | async"
      (setRecipient)="onContinue($event)"
      (errorDetails)="onError($event)"
    ></app-select-recipient>
    <app-cancel-request-link (goToCancelScreen)="onCancel()">
    </app-cancel-request-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SelectRecipientContainerComponent implements OnInit {
  accountName$: Observable<string>;
  accountNumber$: Observable<string>;
  accountHolderName$: Observable<string>;
  candidateRecipients$: Observable<User[]>;
  recipientUrid$: Observable<string>;
  comment$: Observable<string>;

  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.accountName$ = this.store.select(selectAccountName);
    this.accountNumber$ = this.store.select(selectAccountFullIdentifier);
    this.accountHolderName$ = this.store.select(selectAccountHolderName);
    this.candidateRecipients$ = this.store.select(selectCandidateRecipients);
    this.recipientUrid$ = this.store.select(selectRecipientUrid);
    this.comment$ = this.store.select(selectComment);
    this.store.dispatch(
      canGoBack({
        goBackRoute: '/request-documents/select-documents'
      })
    );
  }

  onError(details: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: details
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }

  onCancel() {
    this.store.dispatch(
      cancelRequestDocuments({ route: this.route.snapshot['_routerState'].url })
    );
  }

  onContinue(recipient: {
    recipientUrid: string;
    recipientName: string;
    comment: string;
  }) {
    this.store.dispatch(
      setRecipient({
        recipientUrid: recipient.recipientUrid,
        recipientName: recipient.recipientName,
        comment: recipient.comment
      })
    );
  }
}
