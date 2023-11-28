import { Component, EventEmitter, Input, Output } from '@angular/core';
import { EnrolmentLetterTaskDetails } from '@task-management/model';

@Component({
  selector: 'app-enrolment-letter-task-details',
  templateUrl: './enrolment-letter-task-details.component.html',
  styleUrls: ['./enrolment-letter-task-details.component.css'],
})
export class EnrolmentLetterTaskDetailsComponent {
  @Input()
  taskDetails: EnrolmentLetterTaskDetails;
  @Output() readonly downloadFile = new EventEmitter();

  onDownloadFile() {
    this.downloadFile.emit();
  }
}
