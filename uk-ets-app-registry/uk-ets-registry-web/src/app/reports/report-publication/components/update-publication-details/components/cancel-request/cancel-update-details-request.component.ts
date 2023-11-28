import { Component, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-cancel-update-details-request',
  templateUrl: './cancel-update-details-request.component.html',
})
export class CancelUpdateDetailsRequestComponent {
  @Output() readonly cancelRequest = new EventEmitter();

  onCancel() {
    this.cancelRequest.emit();
  }
}
