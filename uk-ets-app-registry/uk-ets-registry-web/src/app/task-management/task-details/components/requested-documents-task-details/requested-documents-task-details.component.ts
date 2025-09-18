import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  REQUEST_TYPE_VALUES,
  RequestedDocumentUploadTaskDetails,
  RequestType,
  TaskOutcome,
} from '@task-management/model';
import { FileDetails } from '@shared/model/file/file-details.model';
import { Configuration } from '@shared/configuration/configuration.interface';
import { getConfigurationValue } from '@shared/shared.util';
import { environment } from '../../../../../environments/environment';
import { FileBase } from '@shared/model/file';
import { AuthModel } from '@registry-web/auth/auth.model';

@Component({
  selector: 'app-requested-documents-task-details',
  styleUrls: ['./requested-documents-task-details.component.scss'],
  templateUrl: './requested-documents-task-details.component.html',
})
export class RequestedDocumentsTaskDetailsComponent implements OnInit {
  @Input() loggedInUser: AuthModel;
  @Input() taskDetails: RequestedDocumentUploadTaskDetails;
  @Input() configuration: Configuration[];
  @Input() taskActionsVisibility: boolean;
  @Output() readonly downloadTemplateFile = new EventEmitter<FileDetails>();
  @Output() readonly downloadRequestDocumentFile = new EventEmitter<FileBase>();
  @Output() readonly userDecision = new EventEmitter<{
    taskOutcome: TaskOutcome;
    taskType: RequestType;
  }>();

  requestType = RequestType;
  serviceDeskEmail: string;
  environment = environment;
  TaskOutcome = TaskOutcome;
  RequestType = RequestType;

  ngOnInit(): void {
    this.serviceDeskEmail = getConfigurationValue(
      'mail.etrAddress',
      this.configuration
    );
  }

  onDownloadTemplateFile(templateFile: FileDetails) {
    this.downloadTemplateFile.emit(templateFile);
  }

  onDownloadFile(file: FileBase) {
    this.downloadRequestDocumentFile.emit(file);
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
}
