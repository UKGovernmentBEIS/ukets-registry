import { Component, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-cancel-request-link',
  templateUrl: './cancel-request-link.component.html',
})
export class CancelRequestLinkComponent {
  @Output() readonly goToCancelScreen = new EventEmitter();

  onCancel() {
    this.goToCancelScreen.emit();
  }
}
