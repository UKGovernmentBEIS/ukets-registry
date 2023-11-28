import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { UntypedFormBuilder, Validators } from '@angular/forms';

@Component({
  selector: 'app-manually-cancel-transaction',
  templateUrl: './manually-cancel-transaction.component.html',
})
export class ManuallyCancelTransactionComponent
  extends UkFormComponent
  implements OnInit
{
  @Output() readonly comment = new EventEmitter<string>();

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  protected getFormModel(): any {
    return {
      comment: ['', { validators: Validators.required }],
    };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      comment: {
        required: 'You must enter a comment',
      },
    };
  }

  onContinue() {
    this.onSubmit();
  }

  protected doSubmit() {
    this.comment.emit(this.formGroup.value['comment']);
  }
}
