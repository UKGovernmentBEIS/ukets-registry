import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import {
  Component,
  EventEmitter,
  HostListener,
  Input,
  OnInit,
  Output,
} from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import {
  RequestedDocumentsModel,
  TaskDetails,
  TaskFileDownloadInfo,
} from '@task-management/model';
import { AuthModel } from '@registry-web/auth/auth.model';
import { empty, getConfigurationValue } from '@shared/shared.util';
import { UkValidationMessageHandler } from '@shared/validation';
import { Configuration } from '@shared/configuration/configuration.interface';
import { debounceTime } from 'rxjs/operators';
import { FileBase } from '@registry-web/shared/model/file';

@Component({
  selector: 'app-requested-documents-form',
  templateUrl: './requested-documents-form.component.html',
  styleUrls: ['./requested-documents-form.component.scss'],
})
export class RequestedDocumentsFormComponent
  extends UkFormComponent
  implements OnInit
{
  parsedDifference: any;

  @Input()
  documentNames: string[];

  @Input()
  comment: string;

  @Input() requestStatus: string;

  @Input() set difference(value: string) {
    this.parsedDifference = JSON.parse(value);
  }

  @Input() uploadedFiles: FileBase[];

  @Input()
  isInProgress: boolean;

  @Input()
  fileProgress: number;

  @Input()
  fileDetails: RequestedDocumentsModel;

  @Input()
  loggedinUser: AuthModel;

  @Input() claimantURID: string;

  @Input() configuration: Configuration[];

  @Input() taskDetails: TaskDetails;

  @Output() readonly fileEmitter = new EventEmitter<RequestedDocumentsModel>();
  @Output() readonly commentEmitter = new EventEmitter<string>();

  @Output() readonly removeRequestDocumentFile = new EventEmitter<number>();

  @Output() readonly downloadRequestDocumentFile =
    new EventEmitter<TaskFileDownloadInfo>();

  isInProgressActive: boolean[] = [];
  showErrorArray: boolean[] = [];
  isClaimantTheSameWithLoggedIn = false;
  fileIndex: number;
  latestFileUpload: RequestedDocumentsModel[];
  allowedFileTypes: string[] = [];
  maxFileSize: number;

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  ngOnInit() {
    super.ngOnInit();
    this.allowedFileTypes = getConfigurationValue(
      'registry.file.submit.document.type',
      this.configuration
    ).split(',');
    this.maxFileSize = getConfigurationValue(
      'registry.file.max.size',
      this.configuration
    );
    this.documentNames.forEach((s, index) => {
      this.isInProgressActive.push(false);
      this.showErrorArray.push(false);
    });
    this.isClaimantTheSameWithLoggedIn =
      this.loggedinUser.urid === this.claimantURID;
    this.formGroup
      .get('comment')
      .valueChanges.pipe(debounceTime(200))
      .subscribe((data) => this.commentEmitter.emit(data));
  }

  @HostListener('change', ['$event.target'])
  onFileSelected(target) {
    this.resetProgressBar();
    this.fileIndex = Number(
      target.name.toString().substring(target.name.lastIndexOf('-') + 1)
    );
    this.latestFileUpload = this.fileDetails.totalFileUploads.filter(
      (detail) => detail.index === this.fileIndex
    );

    if (!empty(target.files) && target.files.item(0)) {
      this.formGroup.controls['file-upload-' + this.fileIndex].patchValue(
        target.files.item(0)
      );
      this.onSubmit();
    } else {
      this.showErrorArray[this.fileIndex] = false;
      this.fileEmitter.emit({
        file: null,
        documentName: null,
        index: this.fileIndex,
        id:
          this.latestFileUpload.length > 0
            ? this.latestFileUpload[this.latestFileUpload.length - 1].id
            : null,
      });
    }
  }

  onSubmit() {
    this.validationMessages = this.getValidationMessages();
    this.genericValidator = new UkValidationMessageHandler(
      this.validationMessages
    );
    this.showErrorArray[this.fileIndex] = false;
    this.markAllAsTouched();
    if (this.formGroup.controls['file-upload-' + this.fileIndex].valid) {
      this.doSubmit();
    } else {
      this.showErrorArray[this.fileIndex] = true;
      this.validationErrorMessage = this.processValidationMessages();
      this.errorDetails.emit(
        this.genericValidator.mapMessagesToErrorDetails(
          this.validationErrorMessage
        )
      );
    }
  }

  resetProgressBar() {
    this.isInProgressActive.forEach((progressBar, index) => {
      this.isInProgressActive[index] = false;
    });
  }

  protected doSubmit() {
    this.isInProgressActive[this.fileIndex] = true;
    this.fileEmitter.emit({
      file: this.formGroup.controls['file-upload-' + this.fileIndex].value,
      documentName: this.documentNames[this.fileIndex],
      index: this.fileIndex,
      id:
        this.latestFileUpload.length > 0
          ? this.latestFileUpload[this.latestFileUpload.length - 1].id
          : null,
    });
  }

  protected getFormModel(): any {
    const formModel = {
      comment: [this.comment, { updateOn: 'change' }],
    };
    this.documentNames.forEach((s, index) => {
      formModel['file-upload-' + index] = [''];
    });
    return formModel;
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {};
  }

  removeUploadedFileById(fileId: number) {
    if (fileId) {
      this.removeRequestDocumentFile.emit(fileId);
    }
  }

  onDownloadFile(fileId: number) {
    this.downloadRequestDocumentFile.emit({
      fileId,
      taskRequestId: this.taskDetails.requestId,
      taskType: this.taskDetails.taskType,
    });
  }
}
