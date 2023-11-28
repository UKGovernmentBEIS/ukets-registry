import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input,
  Output,
} from '@angular/core';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { UntypedFormBuilder, Validators } from '@angular/forms';

@Component({
  selector: 'app-account-closure-comment',
  templateUrl: './account-closure-comment.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AccountClosureCommentComponent extends UkFormComponent {
  @Input()
  comment: string;
  @Output()
  readonly justificationComment = new EventEmitter<string>();

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  protected getFormModel(): any {
    return {
      justification: [
        this.comment,
        { validators: Validators.required, updateOn: 'change' },
      ],
    };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      justification: {
        required: 'Enter a reason for closing this account',
      },
    };
  }

  protected doSubmit() {
    this.justificationComment.emit(this.formGroup.value['justification']);
  }
}
