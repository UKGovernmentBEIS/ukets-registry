import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { Configuration } from '@shared/configuration/configuration.interface';
import { selectConfigurationRegistry } from '@shared/shared.selector';
import { ActivatedRoute, Router } from '@angular/router';
import { clearErrors, errors } from '@shared/shared.action';
import { requestUploadSelectedPublicationFile } from '@shared/file/actions/file-upload-form.actions';
import { UploadStatus } from '@shared/model/file';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import {
  canGoBackInWizards,
  navigateTo,
} from '@report-publication/actions/report-publication.actions';
import {
  selectPublicationReportFileUploadIsInProgress,
  selectPublicationReportFileUploadProgress,
} from '@report-publication/components/upload-publication-file/reducers/upload-publication-file.selector';

@Component({
  selector: 'app-select-publication-file-container',
  template: `<app-select-publication-file
      (fileEmitter)="sendFileForUpload($event)"
      [isInProgress]="isInProgress$ | async"
      [fileProgress]="fileProgress$ | async"
      [configuration]="configuration$ | async"
      (errorDetails)="onError($event)"
    ></app-select-publication-file>
    <app-cancel-request-link
      (goToCancelScreen)="onCancel()"
    ></app-cancel-request-link>`,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SelectPublicationFileContainerComponent implements OnInit {
  isInProgress$: Observable<boolean>;
  fileProgress$: Observable<number>;
  configuration$: Observable<Configuration[]>;

  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private store: Store
  ) {}

  ngOnInit(): void {
    this.configuration$ = this.store.select(selectConfigurationRegistry);
    this.isInProgress$ = this.store.select(
      selectPublicationReportFileUploadIsInProgress
    );
    this.fileProgress$ = this.store.select(
      selectPublicationReportFileUploadProgress
    );
    this.store.dispatch(canGoBackInWizards({}));
  }

  sendFileForUpload(file) {
    this.store.dispatch(clearErrors());
    this.store.dispatch(
      requestUploadSelectedPublicationFile({
        file,
        status: UploadStatus.Requested,
      })
    );
  }

  onError(value: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: value,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }

  onCancel() {
    this.store.dispatch(
      canGoBackInWizards({ specifyBackLink: '/upload-publication-file' })
    );
    this.store.dispatch(
      navigateTo({ specifyLink: '/upload-publication-file/cancel' })
    );
  }
}
