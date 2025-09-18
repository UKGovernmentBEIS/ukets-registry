import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UserStatusActionState } from '@user-management/model';
import { UserStatus } from '@shared/user';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';

@Component({
  selector: 'app-confirm-user-status-action',
  templateUrl: './confirm-user-status-action.component.html',
  styleUrls: ['./confirm-user-status-action.component.scss'],
})
export class ConfirmUserStatusActionComponent
  extends UkFormComponent
  implements OnInit
{
  @Input() currentUserStatus: UserStatus;
  @Input() userStatusAction: UserStatusActionState;
  @Output() readonly comment = new EventEmitter<string>();
  @Output() readonly cancelUserStatusAction = new EventEmitter();

  userStatusActionSnapshot: UserStatusActionState;

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.userStatusActionSnapshot = this.userStatusAction;
  }

  protected getFormModel(): any {
    return ['RESTORE', 'SUSPEND'].includes(this.userStatusAction?.value)
      ? {
          comment: [
            '',
            {
              validators: [Validators.required, Validators.minLength(3)],
              updateOn: 'change',
            },
          ],
        }
      : {
          comment: ['', { updateOn: 'change' }],
        };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return ['RESTORE', 'SUSPEND'].includes(this.userStatusAction?.value)
      ? {
          comment: {
            required: 'You must enter a comment',
            minlength: 'Comment must be 3 characters or more',
          },
        }
      : {
          comment: {},
        };
  }

  doSubmit() {
    this.comment.emit(this.formGroup.value['comment']);
  }

  onContinue() {
    this.onSubmit();
  }

  onCancel() {
    this.cancelUserStatusAction.emit();
  }
}
