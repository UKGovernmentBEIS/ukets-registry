import { Component, EventEmitter, Input, Output } from '@angular/core';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';

@Component({
  selector: 'app-set-transaction-reference',
  templateUrl: './set-transaction-reference.component.html',
})
export class SetTransactionReferenceComponent extends UkFormComponent {
  @Input()
  reference: string;
  @Input()
  title: string;
  @Output()
  readonly setTransactionReference = new EventEmitter<string>();

  protected getFormModel(): any {
    return {
      transactionReference: [this.reference, { updateOn: 'change' }],
    };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {};
  }

  protected doSubmit() {
    this.setTransactionReference.emit(
      this.formGroup.value['transactionReference']
    );
  }
}
