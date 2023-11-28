import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Installation } from '@shared/model/account';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';

@Component({
  selector: 'app-installation-update',
  template: `<app-installation-input
      [installation]="operatorInfo"
      [title]="'Update the installation and permit details'"
      [headerTitle]="'Request to update the installation information'"
      (installationOutput)="onContinue($event)"
      (errorDetails)="onErrors($event)"
    ></app-installation-input
    ><app-cancel-request-link
      (goToCancelScreen)="onCancel()"
    ></app-cancel-request-link>`,
})
export class InstallationUpdateComponent {
  @Input()
  operatorInfo: Installation;
  @Output() readonly cancelEmitter = new EventEmitter();
  @Output() readonly errorEmitter = new EventEmitter<ErrorSummary>();
  @Output() readonly continueEmitter = new EventEmitter<Installation>();

  onContinue(value: Installation) {
    this.continueEmitter.emit(value);
  }

  onErrors(value: ErrorDetail[]) {
    this.errorEmitter.emit(new ErrorSummary(value));
  }

  onCancel() {
    this.cancelEmitter.emit();
  }
}
