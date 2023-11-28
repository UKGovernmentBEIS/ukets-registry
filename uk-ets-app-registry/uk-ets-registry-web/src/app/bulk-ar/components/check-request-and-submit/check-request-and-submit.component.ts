import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FileBase } from '@shared/model/file';

@Component({
  selector: 'app-check-request-and-submit',
  templateUrl: './check-request-and-submit.component.html',
})
export class CheckRequestAndSubmitComponent {
  @Input()
  fileHeader: FileBase;

  @Output() readonly navigateBackEmitter = new EventEmitter<string>();
  @Output() readonly allocationTableSubmitted = new EventEmitter();

  navigateBack(value: string) {
    this.navigateBackEmitter.emit(value);
  }

  onContinue() {
    this.allocationTableSubmitted.emit();
  }
}
