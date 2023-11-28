import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { Validators, UntypedFormBuilder } from '@angular/forms';
import { FormRadioGroupInfo } from '@shared/form-controls/uk-radio-input/uk-radio.model';
import {
  AccountStatusActionOption,
  AccountStatusActionState,
} from '@shared/model/account/account-status-action';

@Component({
  selector: 'app-select-account-status-action',
  templateUrl: './select-account-status-action.component.html',
  styles: [],
})
export class SelectAccountStatusActionComponent
  extends UkFormComponent
  implements OnInit
{
  @Input() allowedAccountStatusActions: AccountStatusActionOption[];
  @Input() accountStatusAction: AccountStatusActionState;
  @Output()
  readonly selectedAccountStatusAction = new EventEmitter<AccountStatusActionState>();
  @Output() readonly cancelAccountStatusAction = new EventEmitter();

  formRadioGroupInfo: FormRadioGroupInfo;

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  ngOnInit() {
    super.ngOnInit();
    this.formRadioGroupInfo = {
      radioGroupHeading: 'Select Action',
      radioGroupHeadingCaption: 'Change the account status',
      radioGroupHint: 'Select one option',
      key: 'accountStatusAction',
      options: this.getAllowedAccountStatuses(),
    };
  }

  onContinue() {
    this.onSubmit();
  }

  protected doSubmit() {
    const selectedOption = this.allowedAccountStatusActions.find(
      (option) =>
        option.value === this.formGroup.get('accountStatusAction').value
    );
    this.selectedAccountStatusAction.emit({
      value: selectedOption.value,
      label: selectedOption.label,
      hint: selectedOption.hint,
      newStatus: selectedOption.newStatus,
      message: selectedOption.message,
    });
  }

  protected getFormModel(): any {
    return {
      accountStatusAction: [
        this.accountStatusAction ? this.accountStatusAction.value : null,
        Validators.required,
      ],
    };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      accountStatusAction: {
        required: 'You must select an action',
      },
    };
  }

  onCancel() {
    this.cancelAccountStatusAction.emit();
  }

  getAllowedAccountStatuses() {
    if (this.allowedAccountStatusActions) {
      return this.allowedAccountStatusActions.map(
        (accountStatusActionOption) => {
          return {
            label: accountStatusActionOption.label,
            hint: accountStatusActionOption.hint,
            value: accountStatusActionOption.value,
            enabled: accountStatusActionOption.enabled,
          };
        }
      );
    }
  }
}
