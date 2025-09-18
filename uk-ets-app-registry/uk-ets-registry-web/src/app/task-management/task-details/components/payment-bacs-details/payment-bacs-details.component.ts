import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FileBase } from '@shared/model/file';
import { RequestPaymentTaskDetails, TaskDetails } from '@task-management/model';

@Component({
  selector: 'app-payment-bacs-details',
  templateUrl: './payment-bacs-details.component.html',
  styles: ``,
})
export class PaymentBacsDetailsComponent {
  @Input()
  amount: number;
  @Input()
  taskDetails: TaskDetails;
  @Output()
  handleContinue = new EventEmitter<FileBase>();

  onContinue() {
    this.handleContinue.emit(
      (this.taskDetails as RequestPaymentTaskDetails).invoiceFile
    );
  }
}
