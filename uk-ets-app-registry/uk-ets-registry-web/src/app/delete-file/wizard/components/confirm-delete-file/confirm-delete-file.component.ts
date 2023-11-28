import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FileDetails } from '@registry-web/shared/model/file/file-details.model';
import { DocumentsRequestType } from '@registry-web/shared/model/request-documents/documents-request-type';

@Component({
  selector: 'app-confirm-delete-file',
  templateUrl: './confirm-delete-file.component.html',
})
export class ConfirmDeleteFileComponent {
  @Output() readonly submitDelete = new EventEmitter<{
    id: string;
    fileId: string;
    fileName: string;
    documentsRequestType: DocumentsRequestType;
  }>();
  @Input()
  id: string;
  @Input()
  file: FileDetails;
  @Input()
  documentsRequestType: DocumentsRequestType;

  onDelete() {
    this.submitDelete.emit({
      id: this.id,
      fileId: String(this.file.id),
      fileName: this.file.name,
      documentsRequestType: this.documentsRequestType,
    });
  }
}
