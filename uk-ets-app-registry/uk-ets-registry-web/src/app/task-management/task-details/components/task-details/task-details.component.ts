import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
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
import { IsPastDatePipe } from '@registry-web/shared/pipes';

@Component({
  templateUrl: './task-details.component.html',
  selector: 'app-task-details',
})
export class TaskDetailsComponent implements OnInit {
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

  @Input() isSeniorOrJuniorAdmin: boolean;

  @Output()
  readonly downloadFileTemplateEmitter = new EventEmitter<FileDetails>();

  @Output()
  readonly downloadRequestDocumentFile =
    new EventEmitter<TaskFileDownloadInfo>();

  @Output() readonly openDetail = new EventEmitter<string>();

  @Output() readonly requestDocumentEmitter = new EventEmitter();

  @Output() readonly requestPaymentEmitter = new EventEmitter();

  @Output() readonly navigateToPaymentsListEmitter = new EventEmitter();

  @Output() readonly navigateToGovUKPayEmitter = new EventEmitter<{
    nextUrl: string;
  }>();

  @Output() userDecision = new EventEmitter();

  requestTypes = RequestType;
  taskStatusMap = taskStatusMap;
  requestTypeMap = REQUEST_TYPE_VALUES;

  showOverdueSubtaskWarning = false;

  ngOnInit() {
    if (this.taskDetails?.subTasks?.length > 0) {
      this.showOverdueSubtaskWarning = this.taskDetails.subTasks.some(
        (t) =>
          t.deadline &&
          t.taskStatus !== 'COMPLETED' &&
          new IsPastDatePipe().transform(t.deadline)
      );
    }
  }

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

  onAccountHolderOrUserRequestDocuments(requestDocumentDetails) {
    this.requestDocumentEmitter.emit(requestDocumentDetails);
  }

  onRequestPayment(requestPaymentDetails) {
    this.requestPaymentEmitter.emit(requestPaymentDetails);
  }

  onNavigateToPaymentList() {
    this.navigateToPaymentsListEmitter.emit({
      referenceNumber: this.taskDetails.requestId,
    });
  }

  onNavigateToGovUKPay(nextUrl) {
    this.navigateToGovUKPayEmitter.emit(nextUrl);
  }

  onUserDecisionForTask(decision) {
    this.userDecision.emit(decision);
  }
}
