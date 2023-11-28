import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Configuration } from '@shared/configuration/configuration.interface';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { requestUploadSelectedEmissionsTableFile } from '@shared/file/actions/file-upload-form.actions';
import { UploadStatus } from '@shared/model/file';
import { canGoBack, clearErrors, errors } from '@shared/shared.action';
import {
  selectConfigurationRegistry,
  selectErrorSummary,
} from '@shared/shared.selector';
import { Observable } from 'rxjs';
import {
  selectUploadFileIsInProgress,
  selectUploadFileProgress,
} from '@emissions-table/store/reducers';
import { downloadErrorsCSV } from '../../store/actions';

@Component({
  selector: 'app-emissions-table-upload-container',
  template: ` <app-emissions-table-upload
    (fileEmitter)="sendFileForUpload($event)"
    [isInProgress]="isInProgress$ | async"
    [fileProgress]="fileProgress$ | async"
    [configuration]="configuration$ | async"
    [errorSummary]="errorSummary$ | async"
    (errorDetails)="onError($event)"
    (downloadErrorsCSV)="onDownloadErrorsCSV($event)"
  >
  </app-emissions-table-upload>`,
  styles: [],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmissionsTableUploadContainerComponent implements OnInit {
  isInProgress$: Observable<boolean>;
  fileProgress$: Observable<number>;
  configuration$: Observable<Configuration[]>;
  errorSummary$: Observable<ErrorSummary>;

  constructor(private store: Store) {}

  ngOnInit(): void {
    this.configuration$ = this.store.select(selectConfigurationRegistry);
    this.isInProgress$ = this.store.select(selectUploadFileIsInProgress);
    this.fileProgress$ = this.store.select(selectUploadFileProgress);
    this.errorSummary$ = this.store.select(selectErrorSummary);

    this.store.dispatch(canGoBack({ goBackRoute: null }));
  }

  sendFileForUpload(file): void {
    this.store.dispatch(clearErrors());
    this.store.dispatch(
      requestUploadSelectedEmissionsTableFile({
        file,
        status: UploadStatus.Requested,
      })
    );
  }
  onError(value: ErrorDetail[]): void {
    const summary: ErrorSummary = {
      errors: value,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }

  onDownloadErrorsCSV(fileId: number): void {
    this.store.dispatch(downloadErrorsCSV({ fileId }));
  }
}
