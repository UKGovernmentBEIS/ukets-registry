import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { canGoBack, clearErrors, errors } from '@shared/shared.action';
import {
  selectUploadFileIsInProgress,
  selectUploadFileProgress,
} from '@allocation-table/reducers';
import { Observable } from 'rxjs';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { UploadStatus } from '@shared/model/file';
import { requestUploadSelectedAllocationTableFile } from '@shared/file/actions/file-upload-form.actions';
import {
  selectConfigurationRegistry,
  selectErrorSummary,
} from '@shared/shared.selector';
import { Configuration } from '@shared/configuration/configuration.interface';
import { downloadAllocationTableErrorsCSV } from '@allocation-table/actions/allocation-table.actions';

@Component({
  selector: 'app-allocation-table-upload-container',
  template: `
    <app-allocation-table-upload
      (fileEmitter)="sendFileForUpload($event)"
      [isInProgress]="isInProgress$ | async"
      [fileProgress]="fileProgress$ | async"
      [configuration]="configuration$ | async"
      [errorSummary]="errorSummary$ | async"
      (errorDetails)="onError($event)"
      (downloadErrorsCSV)="onDownloadErrorsCSV($event)"
    >
    </app-allocation-table-upload>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AllocationTableUploadContainerComponent implements OnInit {
  isInProgress$: Observable<boolean>;
  fileProgress$: Observable<number>;
  configuration$: Observable<Configuration[]>;
  errorSummary$: Observable<ErrorSummary>;

  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private store: Store
  ) {}

  ngOnInit() {
    this.errorSummary$ = this.store.select(selectErrorSummary);
    this.configuration$ = this.store.select(selectConfigurationRegistry);
    this.isInProgress$ = this.store.select(selectUploadFileIsInProgress);
    this.fileProgress$ = this.store.select(selectUploadFileProgress);

    this.store.dispatch(canGoBack({ goBackRoute: null }));
  }

  sendFileForUpload(file) {
    this.store.dispatch(clearErrors());
    this.store.dispatch(
      requestUploadSelectedAllocationTableFile({
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

  onDownloadErrorsCSV(fileId: number): void {
    this.store.dispatch(downloadAllocationTableErrorsCSV({ fileId }));
  }
}
