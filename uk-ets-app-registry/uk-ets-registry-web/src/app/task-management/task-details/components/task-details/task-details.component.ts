import { Component, EventEmitter, Input, Output } from '@angular/core';
import {
  REQUEST_TYPE_VALUES,
  RequestType,
  TaskDetails,
  TaskFileDownloadInfo,
  taskStatusMap,
  TaskType,
} from '@task-management/model';
import { Configuration } from '@shared/configuration/configuration.interface';
import { AuthModel } from '@registry-web/auth/auth.model';
import { FileDetails } from '@shared/model/file/file-details.model';
import { getLabel } from '@shared/shared.util';
import { FileBase } from '@shared/model/file';

@Component({
  templateUrl: './task-details.component.html',
  selector: 'app-task-details',
})
export class TaskDetailsComponent {
  @Input()
  isEtsTransaction: boolean;

  @Input()
  isTransactionReversal: boolean;

  @Input()
  isAdmin: boolean;

  @Input()
  loggedInUser: AuthModel;

  @Input()
  configuration: Configuration[];

  @Input()
  taskDetails: TaskDetails;

  @Input() taskTypeOptions: TaskType[];

  @Output()
  readonly downloadFileTemplateEmitter = new EventEmitter<FileDetails>();

  @Output()
  readonly downloadRequestDocumentFile = new EventEmitter<TaskFileDownloadInfo>();

  @Output() readonly openDetail = new EventEmitter<string>();

  @Output() readonly requestDocumentEmitter = new EventEmitter();

  @Output() userDecision = new EventEmitter();

  requestTypes = RequestType;
  taskStatusMap = taskStatusMap;
  requestTypeMap = REQUEST_TYPE_VALUES;

  downloadTemplate(fileDetails: FileDetails) {
    this.downloadFileTemplateEmitter.emit(fileDetails);
  }

  downloadFile() {
    this.downloadRequestDocumentFile.emit({
      taskRequestId: this.taskDetails.requestId,
      taskType: this.taskDetails.taskType,
    });
  }

  downloadRequestDocument(file: FileBase) {
    this.downloadRequestDocumentFile.emit({
      fileId: file.id,
      taskRequestId: this.taskDetails.requestId,
      taskType: this.taskDetails.taskType,
    });
  }

  getTaskTypeLabel(key: string): string {
    return getLabel(key, this.taskTypeOptions);
  }

  onAccountHolderOrUserRequestDocuments(requestDocumentDetails) {
    this.requestDocumentEmitter.emit(requestDocumentDetails);
  }

  onUserDecisionForTask(decision) {
    this.userDecision.emit(decision);
  }
}
