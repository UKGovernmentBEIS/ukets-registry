import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-cancel-pending-activation',
  templateUrl: './cancel-pending-activation.component.html',
})
export class CancelPendingActivationComponent {
  @Input() description: string;
  @Output() readonly cancelAddition = new EventEmitter();

  cancel() {
    this.cancelAddition.emit();
  }
}
