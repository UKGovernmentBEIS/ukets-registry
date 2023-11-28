import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { canGoBack, errors } from '@shared/shared.action';
import { Store } from '@ngrx/store';
import {
  selectAccountHolderName,
  selectComment,
  selectDocumentsRequestType,
  selectRecipientName,
} from '@request-documents/wizard/reducers/request-document.selector';
import { Observable } from 'rxjs';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import {
  cancelRequestDocuments,
  setComment,
} from '@request-documents/wizard/actions';
import { ActivatedRoute } from '@angular/router';
import { DocumentsRequestType } from '@shared/model/request-documents/documents-request-type';

@Component({
  selector: 'app-user-document-assigning-comment-container',
  template: ` <app-user-document-assigning-comment
      [documentRequestType]="documentRequestType$ | async"
      [recipientName]="recipientName$ | async"
      [comment]="comment$ | async"
      (setComment)="onContinue($event)"
      (errorDetails)="onError($event)"
    ></app-user-document-assigning-comment
    ><app-cancel-request-link (goToCancelScreen)="onCancel()">
    </app-cancel-request-link>`,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UserDocumentAssigningCommentContainerComponent implements OnInit {
  documentRequestType$: Observable<DocumentsRequestType>;
  comment$: Observable<string>;
  recipientName$: Observable<string>;

  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.store.dispatch(
      canGoBack({
        goBackRoute: '/request-documents/select-documents',
      })
    );
    this.documentRequestType$ = this.store.select(selectDocumentsRequestType);
    this.comment$ = this.store.select(selectComment);
    this.recipientName$ = this.store.select(selectRecipientName);
  }

  onCancel() {
    this.store.dispatch(
      cancelRequestDocuments({ route: this.route.snapshot['_routerState'].url })
    );
  }

  onError(details: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: details,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }

  onContinue(comment) {
    this.store.dispatch(setComment({ comment }));
  }
}
