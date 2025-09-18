import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { clearErrors, errors } from '@shared/shared.action';
import { requestUploadSelectedFileForDocumentRequest } from '@shared/file/actions/file-upload-form.actions';
import { FileBase, UploadStatus } from '@shared/model/file';
import { ErrorDetail } from '@shared/error-summary';

import {
  selectFileUploadErrorMessages,
  selectSubmittedApproveTask,
  selectTask,
  selectUploadedFileDetails,
  selectUploadFileIsInProgress,
  selectUploadFileProgress,
} from '@task-details/reducers/task-details.selector';
import {
  RequestedDocumentsModel,
  TaskFileDownloadInfo,
} from '@task-management/model';
import { selectLoggedInUser } from '@registry-web/auth/auth.selector';
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
      [isInProgress]="isInProgress$ | async"
      [fileProgress]="fileProgress$ | async"
      [fileDetails]="fileDetails$ | async"
      [fileUploadErrorMessages]="fileUploadErrorMessages$ | async"
      [loggedinUser]="loggedinUser$ | async"
      [configuration]="configuration$ | async"
      [taskDetails]="taskDetails$ | async"
      [mayShowErrors]="submittedApproveTask$ | async"
      (fileEmitter)="sendFileForUpload($event)"
      (errorDetails)="onError($event)"
      (commentEmitter)="onComment($event)"
      (removeRequestDocumentFile)="onRemoveRequestDocumentFile($event)"
      (downloadRequestDocumentFile)="onDownloadRequestDocumentFile($event)"
    />
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RequestedDocumentsFormContainerComponent {
  isInProgress$ = this.store.select(selectUploadFileIsInProgress);
  fileProgress$ = this.store.select(selectUploadFileProgress);
  fileDetails$ = this.store.select(selectUploadedFileDetails);
  fileUploadErrorMessages$ = this.store.select(selectFileUploadErrorMessages);
  loggedinUser$ = this.store.select(selectLoggedInUser);
  configuration$ = this.store.select(selectConfigurationRegistry);
  taskDetails$ = this.store.select(selectTask);
  submittedApproveTask$ = this.store.select(selectSubmittedApproveTask);

  constructor(private store: Store) {}

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
    this.store.dispatch(errors({ errorSummary: { errors: value } }));
  }

  onComment(comment: string) {
    this.store.dispatch(sendCommentForNotUploadingDocument({ comment }));
  }

  onRemoveRequestDocumentFile(fileId: number) {
    this.store.dispatch(deleteSelectedFile({ fileId }));
  }

  onDownloadRequestDocumentFile(taskFileInfo: TaskFileDownloadInfo) {
    this.store.dispatch(
      fetchTaskUserFile({ taskFileDownloadInfo: taskFileInfo })
    );
  }
}
