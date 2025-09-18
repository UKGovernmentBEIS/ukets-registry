import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { DocumentsWizardPath } from '@registry-web/documents/models/documents-wizard-path.model';
import { canGoBack, errors } from '@registry-web/shared/shared.action';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { Observable, take } from 'rxjs';
import {
  selectDocument,
  selectDocumentFile,
  selectFileUpdated,
  selectLoadingFile,
  selectUpdateType,
} from '../../store/documents-wizard.selector';
import { SharedModule } from '@registry-web/shared/shared.module';
import * as DocumentsWizardActions from '../../store/documents-wizard.actions';
import { Configuration } from '@registry-web/shared/configuration/configuration.interface';
import { ErrorDetail, ErrorSummary } from '@registry-web/shared/error-summary';
import { UploadFileComponent } from './upload-file.component';
import { selectConfigurationRegistry } from '@registry-web/shared/shared.selector';
import { DocumentUpdateType } from '@registry-web/documents/models/document-update-type.model';
import { SaveRegistryDocumentDTO } from '@registry-web/documents/models/document.model';

@Component({
  standalone: true,
  selector: 'app-upload-file-container',
  imports: [CommonModule, RouterModule, SharedModule, UploadFileComponent],
  template: `<app-upload-file
      (fileEmitter)="saveFile($event)"
      (downloadStoredFileEmitter)="downloadStoredDocumentFile()"
      (removeStoredFileEmitter)="removeStoredDocumentFile()"
      [updateType]="updateType$ | async"
      [storedDocumentFile]="file$ | async"
      [storedDocument]="document$ | async"
      [isInProgress]="isInProgress$ | async"
      [fileProgress]="fileProgress$ | async"
      [configuration]="configuration$ | async"
      [loadingFile]="loadingFile$ | async"
      [fileUpdated]="fileUpdated$ | async"
      (errorDetails)="onError($event)"
    ></app-upload-file>
    <app-cancel-request-link
      (goToCancelScreen)="onCancel()"
    ></app-cancel-request-link>`,
})
export class UploadFileContainerComponent implements OnInit {
  isInProgress$: Observable<boolean>;
  fileProgress$: Observable<number>;
  file$: Observable<File>;
  document$: Observable<SaveRegistryDocumentDTO>;
  updateType$: Observable<DocumentUpdateType>;
  configuration$: Observable<Configuration[]>;
  loadingFile$: Observable<boolean>;
  fileUpdated$: Observable<boolean>;
  goBackRoute: string;
  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private store: Store
  ) {}

  ngOnInit(): void {
    this.configuration$ = this.store.select(selectConfigurationRegistry);
    this.updateType$ = this.store.select(selectUpdateType);
    this.file$ = this.store.select(selectDocumentFile);
    this.document$ = this.store.select(selectDocument);
    this.loadingFile$ = this.store.select(selectLoadingFile);
    this.fileUpdated$ = this.store.select(selectFileUpdated);

    this.updateType$.pipe(take(1)).subscribe((updateType) => {
      switch (updateType) {
        case DocumentUpdateType.ADD_DOCUMENT:
          this.goBackRoute = `${DocumentsWizardPath.BASE_PATH}/${DocumentsWizardPath.ADD_DOCUMENT}`;
          break;
        case DocumentUpdateType.UPDATE_DOCUMENT:
          this.goBackRoute = `${DocumentsWizardPath.BASE_PATH}/${DocumentsWizardPath.UPDATE_DOCUMENT}`;
          break;
        default:
          this.goBackRoute = `${DocumentsWizardPath.BASE_PATH}`;
          break;
      }
    });
    this.store.dispatch(
      canGoBack({
        goBackRoute: this.goBackRoute,
        extras: { skipLocationChange: true },
      })
    );
  }

  saveFile(payload: { file: File; title: string; fileUpdated: boolean }) {
    this.store.dispatch(DocumentsWizardActions.setDocumentFile(payload));
  }

  onError(value: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: value,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }

  onCancel() {
    this.store.dispatch(
      DocumentsWizardActions.navigateToCancelDocumentUpdateRequest({
        route: this.route.snapshot['_routerState'].url,
      })
    );
  }

  downloadStoredDocumentFile() {
    this.store.dispatch(DocumentsWizardActions.fetchStoredDocumentFile());
  }

  removeStoredDocumentFile() {
    this.store.dispatch(DocumentsWizardActions.removeStoredDocumentFile());
  }
}
