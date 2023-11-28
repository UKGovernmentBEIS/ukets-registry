import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-cancel-update-request',
  templateUrl: './cancel-update-request.component.html',
})
export class CancelUpdateRequestComponent {
  @Input()
  pageTitle = 'account';
  @Input()
  updateRequestText: string;
  @Input()
  notification: string;
  @Output() readonly cancelRequest = new EventEmitter();

  onCancel() {
    this.cancelRequest.emit();
  }
}
