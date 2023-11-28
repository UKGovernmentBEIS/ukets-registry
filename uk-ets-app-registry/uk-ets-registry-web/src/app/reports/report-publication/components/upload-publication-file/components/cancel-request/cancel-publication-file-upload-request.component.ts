import { Component, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-cancel-publication-file-upload-request',
  templateUrl: './cancel-publication-file-upload-request.component.html',
})
export class CancelPublicationFileUploadRequestComponent {
  @Output() readonly cancelRequest = new EventEmitter();

  onCancel() {
    this.cancelRequest.emit();
  }
}
