import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { canGoBack, clearErrors, errors } from '@shared/shared.action';
import { requestUploadSelectedBulkArFile } from '@shared/file/actions/file-upload-form.actions';
import { UploadStatus } from '@shared/model/file';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import {
  selectBulkArFileUploadIsInProgress,
  selectBulkArFileUploadProgress,
} from '@registry-web/bulk-ar/reducers';
import { Configuration } from '@shared/configuration/configuration.interface';
import { selectConfigurationRegistry } from '@shared/shared.selector';

@Component({
  selector: 'app-bulk-ar-upload-container',
  template: `
    <app-bulk-ar-upload
      (fileEmitter)="sendFileForUpload($event)"
      [isInProgress]="isInProgress$ | async"
      [fileProgress]="fileProgress$ | async"
      [configuration]="configuration$ | async"
      (errorDetails)="onError($event)"
    >
    </app-bulk-ar-upload>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BulkArUploadContainerComponent implements OnInit {
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
    this.isInProgress$ = this.store.select(selectBulkArFileUploadIsInProgress);
    this.fileProgress$ = this.store.select(selectBulkArFileUploadProgress);
    this.store.dispatch(canGoBack({ goBackRoute: null }));
  }

  sendFileForUpload(file) {
    this.store.dispatch(clearErrors());
    this.store.dispatch(
      requestUploadSelectedBulkArFile({
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
}
