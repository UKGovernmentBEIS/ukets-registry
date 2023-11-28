import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FileDetails } from '@shared/model/file/file-details.model';
import { KeycloakUser } from '@shared/user';

@Component({
  selector: 'app-identification-documentation',
  templateUrl: './identification-documentation.component.html',
})
export class IdentificationDocumentationComponent {
  @Input() documents: FileDetails[] = [];
  @Input() user: KeycloakUser;
  @Input() canDeleteFile: boolean;
  @Output() readonly requestDocuments = new EventEmitter();
  @Output() readonly downloadFileEmitter = new EventEmitter<FileDetails>();
  @Output() readonly deleteFileEmitter = new EventEmitter<FileDetails>();

  onRequestDocuments() {
    this.requestDocuments.emit();
  }

  downloadFile(file: FileDetails) {
    this.downloadFileEmitter.emit(file);
  }

  deleteFile(file: FileDetails) {
    this.deleteFileEmitter.emit(file);
  }

  isUserEligibleForDocRequest() {
    if (this.user !== null) {
      return this.user.eligibleForSpecificActions;
    }
    return false;
  }
}
