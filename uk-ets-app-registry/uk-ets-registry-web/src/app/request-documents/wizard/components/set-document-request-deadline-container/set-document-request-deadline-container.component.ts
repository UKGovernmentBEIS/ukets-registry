import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { Store } from '@ngrx/store';
import {
  selectAccountFullIdentifier,
  selectAccountHolderName,
  selectAccountName,
  selectDeadline,
  selectRecipientName,
} from '@request-documents/wizard/reducers/request-document.selector';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { canGoBack, errors } from '@shared/shared.action';
import {
  cancelRequestDocuments,
  setDeadline,
} from '@request-documents/wizard/actions';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-set-document-request-deadline-container',
  template: `
    <app-set-deadline
      [title]="title"
      [subtitle]="subtitle"
      [sectionTitle]="sectionTitle"
      [accountName]="accountName$ | async"
      [accountNumber]="accountNumber$ | async"
      [accountHolderName]="accountHolderName$ | async"
      [recipientName]="recipientName$ | async"
      [deadline]="deadline$ | async"
      (setDeadline)="onContinue($event)"
      (errorDetails)="onError($event)"
    ></app-set-deadline>
    <app-cancel-request-link (goToCancelScreen)="onCancel()">
    </app-cancel-request-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SetDocumentRequestDeadlineContainerComponent implements OnInit {
  accountName$: Observable<string>;
  accountNumber$: Observable<string>;
  accountHolderName$: Observable<string>;
  recipientName$: Observable<string>;
  deadline$: Observable<string>;
  title: string;
  subtitle: string;
  sectionTitle: string;

  isUserDocumentRequest =
    this.route.snapshot['_routerState'].url.indexOf('set-deadline-user') > 0;

  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.accountName$ = this.store.select(selectAccountName);
    this.accountNumber$ = this.store.select(selectAccountFullIdentifier);
    this.accountHolderName$ = this.store.select(selectAccountHolderName);
    this.recipientName$ = this.store.select(selectRecipientName);
    this.deadline$ = this.store.select(selectDeadline);

    this.title = 'Set deadline for this request';
    let goBackRoute = '';
    if (this.isUserDocumentRequest) {
      goBackRoute = '/request-documents/assigning-user-comment';
      this.subtitle = 'Request account holder documents';
      this.sectionTitle = 'Recipient details';
    } else {
      goBackRoute = '/request-documents/select-recipient';
      this.subtitle = 'Request account holder documents';
      this.sectionTitle = 'Account details';
    }

    this.store.dispatch(
      canGoBack({
        goBackRoute,
      })
    );
  }

  onError(details: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: details,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }

  onCancel() {
    this.store.dispatch(
      cancelRequestDocuments({ route: this.route.snapshot['_routerState'].url })
    );
  }

  onContinue(deadline: string) {
    this.store.dispatch(
      setDeadline({
        deadline: new Date(deadline),
      })
    );
  }
}
