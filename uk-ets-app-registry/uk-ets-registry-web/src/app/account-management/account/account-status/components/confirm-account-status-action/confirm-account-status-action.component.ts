import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { AccountStatusActionState } from '@shared/model/account/account-status-action';
import { AccountStatus } from '@shared/model/account';
import { accountStatusMap } from '@account-management/account-list/account-list.model';

@Component({
  selector: 'app-confirm-account-status-action',
  templateUrl: './confirm-account-status-action.component.html',
  styleUrls: ['./confirm-account-status-action.component.scss'],
})
export class ConfirmAccountStatusActionComponent
  extends UkFormComponent
  implements OnInit
{
  @Input() currentAccountStatus: AccountStatus;
  @Input() accountStatusAction: AccountStatusActionState;
  @Output() readonly comment = new EventEmitter<string>();
  @Output() readonly cancelAccountStatusAction = new EventEmitter();

  accountStatusActionSnapshot: AccountStatusActionState;
  accountStatusMap = accountStatusMap;

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  ngOnInit() {
    super.ngOnInit();
    this.accountStatusActionSnapshot = this.accountStatusAction;
  }

  protected getFormModel(): any {
    return {
      comment: ['', { validators: Validators.required, updateOn: 'change' }],
    };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      comment: {
        required: 'You must enter a comment',
      },
    };
  }

  protected doSubmit() {
    this.comment.emit(this.formGroup.value['comment']);
  }

  onContinue() {
    this.onSubmit();
  }

  onCancel() {
    this.cancelAccountStatusAction.emit();
  }
}
