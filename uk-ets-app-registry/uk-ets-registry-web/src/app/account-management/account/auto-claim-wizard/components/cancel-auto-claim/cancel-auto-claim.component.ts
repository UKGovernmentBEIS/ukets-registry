import { Component, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-cancel-auto-claim',
  templateUrl: './cancel-auto-claim.component.html',
  styles: ``,
})
export class CancelAutoClaimComponent {
  @Output() readonly cancelRequest = new EventEmitter();

  onCancel() {
    this.cancelRequest.emit();
  }
}
