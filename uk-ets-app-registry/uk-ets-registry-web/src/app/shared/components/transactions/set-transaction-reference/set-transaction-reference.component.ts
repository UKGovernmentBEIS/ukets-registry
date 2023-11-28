import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { UntypedFormBuilder } from '@angular/forms';

@Component({
  selector: 'app-set-transaction-reference',
  templateUrl: './set-transaction-reference.component.html',
})
export class SetTransactionReferenceComponent
  extends UkFormComponent
  implements OnInit
{
  @Input()
  reference: string;
  @Input()
  title: string;
  @Output()
  readonly setTransactionReference = new EventEmitter<string>();

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  protected getFormModel(): any {
    return {
      transactionReference: [this.reference],
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
