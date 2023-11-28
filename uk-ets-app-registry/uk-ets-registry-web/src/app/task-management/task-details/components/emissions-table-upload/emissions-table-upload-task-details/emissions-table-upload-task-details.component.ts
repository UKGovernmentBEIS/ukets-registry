import { Component, EventEmitter, Input, Output } from '@angular/core';
import { EmissionsTableUploadTaskDetails } from '@task-management/model';

@Component({
  selector: 'app-emissions-table-upload-task-details',
  templateUrl: './emissions-table-upload-task-details.component.html',
  styles: [],
})
export class EmissionsTableUploadTaskDetailsComponent {
  @Input()
  taskDetails: EmissionsTableUploadTaskDetails;
  @Output() readonly downloadFile = new EventEmitter();

  onDownloadFile(): void {
    this.downloadFile.emit();
  }
}
