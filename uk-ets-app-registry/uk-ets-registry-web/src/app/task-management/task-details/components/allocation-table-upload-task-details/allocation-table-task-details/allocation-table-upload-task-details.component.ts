import { Component, EventEmitter, Input, Output } from '@angular/core';
import { AllocationTableUploadTaskDetails } from '@task-management/model';

@Component({
  selector: 'app-allocation-table-upload-task-details',
  templateUrl: './allocation-table-upload-task-details.component.html',
})
export class AllocationTableUploadTaskDetailsComponent {
  @Input()
  taskDetails: AllocationTableUploadTaskDetails;
  @Output() readonly downloadFile = new EventEmitter();

  onDownloadFile() {
    this.downloadFile.emit();
  }
}
