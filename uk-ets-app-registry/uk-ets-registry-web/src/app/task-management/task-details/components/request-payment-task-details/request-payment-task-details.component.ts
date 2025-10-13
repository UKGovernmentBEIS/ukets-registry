import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { AuthModel } from '@registry-web/auth/auth.model';
import { Configuration } from '@shared/configuration/configuration.interface';
import { FileBase } from '@shared/model/file';
import { FileDetails } from '@shared/model/file/file-details.model';
import {
  RequestPaymentTaskDetails,
  TaskOutcome,
  RequestType,
  REQUEST_TYPE_VALUES,
} from '@task-management/model';

@Component({
  selector: 'app-request-payment-task-details',
  templateUrl: './request-payment-task-details.component.html',
  styleUrl: './request-payment-task-details.component.css',
})
export class RequestPaymentTaskDetailsComponent {
  @Input()
  loggedInUser: AuthModel;
  @Input()
  taskDetails: RequestPaymentTaskDetails;
  @Input()
  configuration: Configuration[];
  @Input()
  taskActionsVisibility: boolean;

  @Output() readonly downloadTemplateFile = new EventEmitter<FileDetails>();

  @Output() readonly downloadRequestDocumentFile = new EventEmitter<FileBase>();

  @Output() readonly navigateToPaymentsList = new EventEmitter();

  @Output() readonly userDecision = new EventEmitter<{
    taskOutcome: TaskOutcome;
    taskType: RequestType;
  }>();

  TaskOutcome = TaskOutcome;
  RequestType = RequestType;

  onDownloadInvoice() {
    this.downloadRequestDocumentFile.emit(this.taskDetails.invoiceFile);
  }

  onDownloadReceipt() {
    this.downloadRequestDocumentFile.emit(this.taskDetails.receiptFile);
  }

  onNavigateToPaymentList() {
    this.navigateToPaymentsList.emit();
  }

  proceedWith(taskOutcome: TaskOutcome) {
    this.userDecision.emit({
      taskOutcome,
      taskType: this.taskDetails.taskType,
    });
  }

  checkIfCanOnlyComplete(type: RequestType) {
    return REQUEST_TYPE_VALUES[this.taskDetails.taskType].completeOnly;
  }

  shouldShowPaymentLink() {
    return (
      !this.taskDetails.paymentStatus ||
      this.taskDetails.paymentStatus === 'CANCELLED'||
      this.taskDetails.paymentStatus === 'FAILED'||
      this.taskDetails.paymentStatus === 'CREATED'
    );
  }

  shouldShowPaymentReceipt() {
    return (
      this.taskDetails.paymentStatus === 'SUCCESS' &&
      (this.taskDetails.paymentMethod === 'CARD_OR_DIGITAL_WALLET' ||
        this.taskDetails.paymentMethod === 'WEBLINK')
    );
  }
}
