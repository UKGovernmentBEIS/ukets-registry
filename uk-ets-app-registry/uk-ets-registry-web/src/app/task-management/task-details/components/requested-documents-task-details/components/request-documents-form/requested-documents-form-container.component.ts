import {
  ChangeDetectionStrategy,
  Component,
  Input,
  OnInit,
} from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { clearErrors, errors } from '@shared/shared.action';
import { requestUploadSelectedFileForDocumentRequest } from '@shared/file/actions/file-upload-form.actions';
import { FileBase, UploadStatus } from '@shared/model/file';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';

import { Observable } from 'rxjs';
import {
  selectTask,
  selectUploadedFileDetails,
  selectUploadFileIsInProgress,
  selectUploadFileProgress,
} from '@task-details/reducers/task-details.selector';
import {
  RequestedDocumentsModel,
  TaskDetails,
  TaskFileDownloadInfo,
} from '@task-management/model';
import { selectLoggedInUser } from '@registry-web/auth/auth.selector';
import { AuthModel } from '@registry-web/auth/auth.model';
import { Configuration } from '@shared/configuration/configuration.interface';
import { selectConfigurationRegistry } from '@shared/shared.selector';
import {
  deleteSelectedFile,
  fetchTaskUserFile,
  sendCommentForNotUploadingDocument,
} from '@task-details/actions/task-details.actions';

@Component({
  selector: 'app-requested-documents-form-container',
  template: `
    <app-requested-documents-form
      (fileEmitter)="sendFileForUpload($event)"
      [isInProgress]="isInProgress$ | async"
      [fileProgress]="fileProgress$ | async"
      [fileDetails]="fileDetails$ | async"
      [loggedinUser]="loggedinUser$ | async"
      [configuration]="configuration$ | async"
      [taskDetails]="taskDetails$ | async"
      [documentNames]="documentNames"
      [comment]="comment"
      [claimantURID]="claimantURID"
      [requestStatus]="requestStatus"
      [difference]="difference"
      [uploadedFiles]="uploadedFiles"
      (errorDetails)="onError($event)"
      (commentEmitter)="onComment($event)"
      (removeRequestDocumentFile)="onRemoveRequestDocumentFile($event)"
      (downloadRequestDocumentFile)="onDownloadRequestDocumentFile($event)"
    >
    </app-requested-documents-form>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RequestedDocumentsFormContainerComponent implements OnInit {
  @Input() documentNames: string[];
  @Input() comment: string;
  @Input() requestStatus: string;
  @Input() claimantURID: string;
  @Input() difference: string;
  @Input() uploadedFiles: FileBase[];

  isInProgress$: Observable<boolean>;
  fileProgress$: Observable<number>;
  fileDetails$: Observable<RequestedDocumentsModel>;
  loggedinUser$: Observable<AuthModel>;
  configuration$: Observable<Configuration[]>;
  taskDetails$: Observable<TaskDetails>;

  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private store: Store
  ) {}

  ngOnInit() {
    this.isInProgress$ = this.store.select(selectUploadFileIsInProgress);
    this.fileProgress$ = this.store.select(selectUploadFileProgress);
    this.fileDetails$ = this.store.select(selectUploadedFileDetails);
    this.loggedinUser$ = this.store.select(selectLoggedInUser);
    this.configuration$ = this.store.select(selectConfigurationRegistry);
    this.taskDetails$ = this.store.select(selectTask);
  }

  sendFileForUpload(fileInfo: RequestedDocumentsModel) {
    this.store.dispatch(clearErrors());
    this.store.dispatch(
      requestUploadSelectedFileForDocumentRequest({
        documentName: fileInfo.documentName,
        file: fileInfo.file,
        status: UploadStatus.Requested,
        fileUploadIndex: fileInfo.index,
        fileId: fileInfo.id,
      })
    );
  }

  onError(value: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: value,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }

  onComment(comment: string) {
    this.store.dispatch(sendCommentForNotUploadingDocument({ comment }));
  }

  onRemoveRequestDocumentFile(fileId: number) {
    this.store.dispatch(deleteSelectedFile({ fileId }));
  }

  onDownloadRequestDocumentFile(taskFileInfo: TaskFileDownloadInfo) {
    console.log(taskFileInfo);
    this.store.dispatch(
      fetchTaskUserFile({
        taskFileDownloadInfo: taskFileInfo,
      })
    );
  }
}
