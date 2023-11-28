import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { Store } from '@ngrx/store';
import {
  displayUserCommentsPage,
  selectAccountFullIdentifier,
  selectAccountHolderName,
  selectAccountName,
  selectComment,
  selectDocumentNames,
  selectDocumentsRequestType,
  selectRecipientName,
  selectRecipientUrid,
  selectRequestDocumentsOrigin,
} from '@request-documents/wizard/reducers/request-document.selector';
import {
  cancelRequestDocuments,
  navigateToAssignUserComment,
  navigateToSelectDocuments,
  navigateToSelectRecipient,
  submitDocumentsRequest,
} from '@request-documents/wizard/actions';
import { ActivatedRoute, Data } from '@angular/router';
import { RequestDocumentsRoutePaths } from '@request-documents/wizard/model/request-documents-route-paths.model';
import { RequestDocumentsOrigin } from '@shared/model/request-documents/request-documents-origin';
import { canGoBack } from '@shared/shared.action';
import { DocumentsRequestType } from '@shared/model/request-documents/documents-request-type';
import { isAdmin, isSeniorAdmin } from '@registry-web/auth/auth.selector';

@Component({
  selector: 'app-check-documents-container',
  template: `
    <app-check-documents-request
      [documentRequestType]="documentRequestType$ | async"
      [accountHolderName]="accountHolderName$ | async"
      [accountName]="accountName$ | async"
      [accountNumber]="accountNumber$ | async"
      [recipientName]="recipientName$ | async"
      [recipientUrid]="recipientUrid$ | async"
      [documentNames]="documentNames$ | async"
      [comment]="comment$ | async"
      [isAdmin]="isAdmin$ | async"
      [isSeniorAdmin]="isSeniorAdmin$ | async"
      [requestDocumentsOrigin]="requestDocumentsOrigin$ | async"
      [displayUserCommentsPage]="displayUserCommentsPage$ | async"
      (navigateToEmitter)="onChange($event)"
      (submitRequest)="onSubmit()"
    ></app-check-documents-request>
    <app-cancel-request-link (goToCancelScreen)="onCancel()">
    </app-cancel-request-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CheckDocumentsRequestContainerComponent implements OnInit {
  accountHolderName$: Observable<string>;
  accountName$: Observable<string>;
  accountNumber$: Observable<string>;
  recipientName$: Observable<string>;
  recipientUrid$: Observable<string>;
  documentNames$: Observable<string[]>;
  comment$: Observable<string>;
  isAdmin$: Observable<boolean>;
  isSeniorAdmin$: Observable<boolean>;
  displayUserCommentsPage$: Observable<boolean>;
  documentRequestType$: Observable<DocumentsRequestType>;
  requestDocumentsOrigin$: Observable<RequestDocumentsOrigin>;

  goBackPath: string;

  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.accountHolderName$ = this.store.select(selectAccountHolderName);
    this.accountNumber$ = this.store.select(selectAccountFullIdentifier);
    this.accountName$ = this.store.select(selectAccountName);
    this.accountNumber$ = this.store.select(selectAccountFullIdentifier);
    this.recipientName$ = this.store.select(selectRecipientName);
    this.recipientUrid$ = this.store.select(selectRecipientUrid);
    this.documentNames$ = this.store.select(selectDocumentNames);
    this.comment$ = this.store.select(selectComment);
    this.isAdmin$ = this.store.select(isAdmin);
    this.isSeniorAdmin$ = this.store.select(isSeniorAdmin);
    this.requestDocumentsOrigin$ = this.store.select(
      selectRequestDocumentsOrigin
    );
    this.displayUserCommentsPage$ = this.store.select(displayUserCommentsPage);
    this.documentRequestType$ = this.store.select(selectDocumentsRequestType);

    this.route.data.subscribe((data: Data) => {
      this.initData(data);
    });

    this.store.dispatch(
      canGoBack({
        goBackRoute: `/request-documents/${this.goBackPath}`,
        extras: { skipLocationChange: true },
      })
    );
  }

  protected initData(data: Data) {
    this.goBackPath = data.goBackPath;
  }

  onCancel() {
    this.store.dispatch(
      cancelRequestDocuments({ route: this.route.snapshot['_routerState'].url })
    );
  }

  onChange(step: RequestDocumentsRoutePaths) {
    switch (step) {
      case RequestDocumentsRoutePaths['select-documents']:
        this.store.dispatch(navigateToSelectDocuments());
        break;
      case RequestDocumentsRoutePaths['select-recipient']:
        this.store.dispatch(navigateToSelectRecipient());
        break;
      case RequestDocumentsRoutePaths['assigning-user-comment']:
        this.store.dispatch(navigateToAssignUserComment());
        break;
    }
  }

  onSubmit() {
    this.store.dispatch(submitDocumentsRequest());
  }
}
