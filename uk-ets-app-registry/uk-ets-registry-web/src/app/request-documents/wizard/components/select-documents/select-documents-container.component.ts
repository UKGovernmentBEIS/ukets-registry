import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { canGoBack, errors } from '@shared/shared.action';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import {
  selectAccountFullIdentifier,
  selectAccountHolderName,
  selectAccountName,
  selectDocumentNames,
  selectOriginatingPath,
  selectRecipientName,
} from '@request-documents/wizard/reducers/request-document.selector';
import {
  cancelRequestDocuments,
  setDocumentNames,
} from '@request-documents/wizard/actions';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { ApiErrorHandlingService } from '@shared/services';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-select-documents-container',
  template: `
    <app-select-documents
      [accountName]="accountName$ | async"
      [accountNumber]="accountNumber$ | async"
      [accountHolderName]="accountHolderName$ | async"
      [recipientName]="recipientName$ | async"
      [documentNames]="documentNames$ | async"
      (setDocumentNames)="onContinue($event)"
      (errorDetails)="onError($event)"
    ></app-select-documents>
    <app-cancel-request-link (goToCancelScreen)="onCancel()">
    </app-cancel-request-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SelectDocumentsContainerComponent implements OnInit {
  documentNames$: Observable<string[]>;
  originatingPath: string;
  accountName$: Observable<string>;
  accountHolderName$: Observable<string>;
  recipientName$: Observable<string>;
  accountNumber$: Observable<string>;

  constructor(
    private store: Store,
    private apiErrorHandlingService: ApiErrorHandlingService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.accountNumber$ = this.store.select(selectAccountFullIdentifier);
    this.accountName$ = this.store.select(selectAccountName);
    this.accountHolderName$ = this.store.select(selectAccountHolderName);
    this.recipientName$ = this.store.select(selectRecipientName);
    this.documentNames$ = this.store.select(selectDocumentNames);

    this.store.select(selectOriginatingPath).subscribe((path) => {
      this.originatingPath = path;
    });
    this.store.dispatch(
      canGoBack({
        goBackRoute: this.originatingPath,
      })
    );
  }

  onContinue(documentNames: string[]) {
    const uniqueDocsArray = documentNames.map((v) => v.toLowerCase());
    if (documentNames.length === 0) {
      this.onUiError(
        this.apiErrorHandlingService.buildUiError(
          'Select at least one document type.'
        )
      );
    } else if (new Set(uniqueDocsArray).size !== documentNames.length) {
      this.onUiError(
        this.apiErrorHandlingService.buildUiError(
          'Duplicate document names - document names must be unique.'
        )
      );
    } else {
      this.store.dispatch(setDocumentNames({ documentNames }));
    }
  }

  onError(details: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: details,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }

  onUiError(summary: ErrorSummary) {
    this.store.dispatch(errors({ errorSummary: summary }));
  }

  onCancel() {
    this.store.dispatch(
      cancelRequestDocuments({ route: this.route.snapshot['_routerState'].url })
    );
  }
}
