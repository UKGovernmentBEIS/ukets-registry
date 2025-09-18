import {
  Component,
  ElementRef,
  EventEmitter,
  HostListener,
  Input,
  OnInit,
  Output,
  ViewChild,
} from '@angular/core';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { UntypedFormBuilder } from '@angular/forms';
import { empty, getConfigurationValue } from '@shared/shared.util';
import { canGoBack } from '@shared/shared.action';
import { submitBulkArRequest } from '@registry-web/bulk-ar/actions/bulk-ar.actions';
import { submitEmailRecipientsFile } from '@notifications/notifications-wizard/actions/notifications-wizard.actions';
import { Configuration } from '@shared/configuration/configuration.interface';
import { ErrorSummary } from '@shared/error-summary';
import { Store } from '@ngrx/store';
import { NotificationRequestEnum } from '@notifications/notifications-wizard/model/notification-request.enum';
import {
  Notification,
  NotificationTypeLabels,
} from '@notifications/notifications-wizard/model';

@Component({
  selector: 'app-notifications-email-upload-file',
  templateUrl: './notifications-email-upload-file.component.html',
  styleUrls: ['./notifications-email-upload-file.component.css'],
})
export class NotificationsEmailUploadFileComponent
  extends UkFormComponent
  implements OnInit
{
  @Input()
  errorSummary: ErrorSummary;

  @Input()
  notificationRequest: NotificationRequestEnum;

  @Input()
  newNotification: Notification;

  @Output() readonly cancelEmitter = new EventEmitter();
  @Output() readonly fileEmitter = new EventEmitter<File>();

  @ViewChild('fileInput') fileInput: ElementRef;

  allowedFileTypes: string[] = [];

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  @HostListener('change', ['$event.target.files'])
  onFileSelected(event: FileList) {
    this.showErrors = false;
    this.formGroup.patchValue({
      emailRecipients: event && event.item(0),
    });
  }

  protected doSubmit() {
    this.fileEmitter.emit(this.formGroup.value.emailRecipients);
    this.onFileUploadComplete();
  }

  protected getFormModel(): any {
    return {
      emailRecipients: [''],
    };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {};
  }

  onCancel() {
    this.cancelEmitter.emit();
  }

  showFileErrors(): boolean {
    return this.errorSummary?.errors.length > 0;
  }

  onFileUploadComplete() {
    //This code is added since Chrome does not allow uloading the same file again
    //By setting type to the nativeElement Chrome triggers refresh of the input
    //without throwing an error
    if (this.fileInput?.nativeElement) {
      this.fileInput.nativeElement.type = 'text'; // Temporarily change type
      this.fileInput.nativeElement.type = 'file'; // Reset to file input
    }
  }

  protected readonly NotificationRequestEnum = NotificationRequestEnum;
  protected readonly notificationTypeLabels = NotificationTypeLabels;
}
