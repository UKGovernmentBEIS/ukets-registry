import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FileDetails } from '@shared/model/file/file-details.model';

@Component({
  selector: 'app-identification-documentation-list',
  templateUrl: './identification-documentation-list.component.html',
})
export class IdentificationDocumentationListComponent {
  @Input()
  documents: FileDetails[] = [];
  @Input() canDeleteFile: boolean;
  @Output() readonly downloadFile = new EventEmitter<FileDetails>();
  @Output() readonly deleteFile = new EventEmitter<FileDetails>();

  onDownloadFile(file: FileDetails) {
    this.downloadFile.emit(file);
  }

  onDeleteFile(file: FileDetails) {
    this.deleteFile.emit(file);
  }
}
