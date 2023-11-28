import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { ErrorDetail, ErrorSummary } from '@registry-web/shared/error-summary';
import { FileDetails } from '@registry-web/shared/model/file/file-details.model';
import { canGoBack, errors } from '@registry-web/shared/shared.action';
import { Observable } from 'rxjs';
import { submitDeleteFile } from '@delete-file/wizard/actions/delete-file.actions';
import {
  selectDocumentType,
  selectFile,
  selectId,
  selectOriginatingPath,
} from '../../reducers/delete-file.selector';
import { DocumentsRequestType } from '@registry-web/shared/model/request-documents/documents-request-type';

@Component({
  selector: 'app-confirm-delete-file-container',
  template: `<app-confirm-delete-file
    [id]="id$ | async"
    [file]="file$ | async"
    [documentsRequestType]="documentsRequestType$ | async"
    (submitDelete)="onSubmit($event)"
    (errorDetails)="onError($event)"
  ></app-confirm-delete-file> `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConfirmDeleteFileContainerComponent implements OnInit {
  id$: Observable<string>;
  file$: Observable<FileDetails>;
  documentsRequestType$: Observable<DocumentsRequestType>;
  originatingPath: string;
  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit() {
    this.store.select(selectOriginatingPath).subscribe((path) => {
      this.originatingPath = path;
    });
    this.store.dispatch(
      canGoBack({
        goBackRoute: this.originatingPath,
      })
    );
    this.id$ = this.store.select(selectId);
    this.file$ = this.store.select(selectFile);
    this.documentsRequestType$ = this.store.select(selectDocumentType);
  }

  onSubmit(value: {
    id: string;
    fileId: string;
    fileName: string;
    documentsRequestType: DocumentsRequestType;
  }) {
    this.store.dispatch(
      submitDeleteFile({
        id: value.id,
        fileId: value.fileId,
        fileName: value.fileName,
        documentsRequestType: value.documentsRequestType,
      })
    );
  }

  onError(value: ErrorDetail) {
    const summary: ErrorSummary = {
      errors: [value],
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
