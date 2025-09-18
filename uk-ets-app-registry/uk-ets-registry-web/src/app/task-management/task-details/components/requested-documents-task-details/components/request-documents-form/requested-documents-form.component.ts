import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import {
  AfterViewInit,
  Component,
  EventEmitter,
  HostListener,
  Input,
  OnInit,
  Output,
  QueryList,
  ViewChildren,
} from '@angular/core';
import {
  RequestedDocumentsModel,
  RequestedDocumentUploadTaskDetails,
  TaskFileDownloadInfo,
} from '@task-management/model';
import { AuthModel } from '@registry-web/auth/auth.model';
import { empty, getConfigurationValue } from '@shared/shared.util';
import { UkValidationMessageHandler } from '@shared/validation';
import { Configuration } from '@shared/configuration/configuration.interface';
import { debounceTime, takeUntil } from 'rxjs/operators';
import { FormControl, Validators } from '@angular/forms';
import { UkSelectFileComponent } from '@registry-web/shared/form-controls';

@Component({
  selector: 'app-requested-documents-form',
  templateUrl: './requested-documents-form.component.html',
  styleUrls: ['./requested-documents-form.component.scss'],
})
export class RequestedDocumentsFormComponent
  extends UkFormComponent
  implements OnInit, AfterViewInit
{
  @Input() set taskDetails(value: RequestedDocumentUploadTaskDetails) {
    this.parsedDifference = JSON.parse(value.difference);
    this._taskDetails = value;
  }
  get taskDetails(): RequestedDocumentUploadTaskDetails {
    return this._taskDetails;
  }
  private _taskDetails: RequestedDocumentUploadTaskDetails;
  @Input() isInProgress: boolean;
  @Input() fileProgress: number;
  @Input() fileDetails: RequestedDocumentsModel;
  @Input() set fileUploadErrorMessages(errors: string[]) {
    errors.forEach((error, index) =>
      error ? (this.showErrorArray[index] = true) : null
    );
    this._fileUploadErrorMessages = errors;
  }
  get fileUploadErrorMessages(): string[] {
    return this._fileUploadErrorMessages;
  }
  private _fileUploadErrorMessages: string[] = [];

  @Input() loggedinUser: AuthModel;
  @Input() configuration: Configuration[];
  @Input() set mayShowErrors(value: boolean) {
    this.showErrors = !!value;

    if (this.showErrors) {
      this.markAllAsTouched();
      this.validationErrorMessage = this.processValidationMessages();
    }
  }

  @Output() readonly fileEmitter = new EventEmitter<RequestedDocumentsModel>();
  @Output() readonly commentEmitter = new EventEmitter<string>();
  @Output() readonly removeRequestDocumentFile = new EventEmitter<number>();
  @Output() readonly downloadRequestDocumentFile =
    new EventEmitter<TaskFileDownloadInfo>();

  @ViewChildren('fileSelectInputs')
  fileSelectInputs: QueryList<UkSelectFileComponent>;

  parsedDifference: any;
  isInProgressActive: boolean[] = [];
  showErrorArray: boolean[] = [];
  isClaimantTheSameWithLoggedIn = false;
  fileIndex: number;
  latestFileUpload: RequestedDocumentsModel[];
  allowedFileTypes: string[] = [];
  maxFileSize: number;
  commentFormControl: FormControl<string>;

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
    this.taskDetails.documentNames.forEach((s, index) => {
      this.isInProgressActive.push(false);
      this.showErrorArray.push(false);
    });
    this.isClaimantTheSameWithLoggedIn =
      this.loggedinUser.urid === this.taskDetails.claimantURID;

    this.commentFormControl.valueChanges
      .pipe(debounceTime(200), takeUntil(this.destroy$))
      .subscribe((data) => this.commentEmitter.emit(data));

    this.taskDetails.documentNames.forEach((_, index) =>
      this.formGroup
        .get('file-upload-' + index)
        .statusChanges.pipe(takeUntil(this.destroy$))
        .subscribe(() => this.setCommentValidator())
    );
  }

  ngAfterViewInit(): void {
    this.setCommentValidator();
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
      documentName: this.taskDetails.documentNames[this.fileIndex],
      index: this.fileIndex,
      id:
        this.latestFileUpload.length > 0
          ? this.latestFileUpload[this.latestFileUpload.length - 1].id
          : null,
    });
  }

  protected getFormModel(): any {
    this.commentFormControl = new FormControl(this.taskDetails.comment, {
      updateOn: 'change',
    });
    const formModel = { comment: this.commentFormControl };
    this.taskDetails.documentNames.forEach((_, index) => {
      formModel['file-upload-' + index] = ['', { updateOn: 'change' }];
    });
    return formModel;
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return this.taskDetails.documentNames.reduce(
      (acc, _currValue, currIndex) => {
        acc['file-upload-' + currIndex] = {
          selectFileRequired: 'Upload the requested file',
        };
        return acc;
      },
      {
        comment: {
          required:
            this.taskDetails.documentNames.length > 1
              ? 'Enter a reason for not uploading the files'
              : 'Enter a reason for not uploading the file',
        },
      }
    );
  }

  removeUploadedFileById(fileId: number, fileUploadIndex: number) {
    if (fileId) {
      this.removeRequestDocumentFile.emit(fileId);
      this.fileSelectInputs.get(fileUploadIndex)?.reset();
    }
  }

  onDownloadFile(fileId: number) {
    this.downloadRequestDocumentFile.emit({
      fileId,
      taskRequestId: this.taskDetails.requestId,
      taskType: this.taskDetails.taskType,
    });
  }

  private setCommentValidator(): void {
    const allFileUploadsValid: boolean = this.taskDetails.documentNames.every(
      (_value, index) => this.formGroup.get('file-upload-' + index).valid
    );
    allFileUploadsValid
      ? this.commentFormControl.clearValidators()
      : this.commentFormControl.setValidators([Validators.required]);
    this.commentFormControl.updateValueAndValidity();
  }
}
