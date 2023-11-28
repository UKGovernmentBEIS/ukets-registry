import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormRadioGroupInfo } from '@shared/form-controls/uk-radio-input/uk-radio.model';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { TrustedAccountListRules } from '@shared/model/account';

@Component({
  selector: 'app-second-approval-necessary',
  templateUrl: './second-approval-necessary.component.html',
})
export class SecondApprovalNecessaryComponent
  extends UkFormComponent
  implements OnInit
{
  @Output()
  readonly selectTransactionRuleOption = new EventEmitter<TrustedAccountListRules>();
  @Input()
  currentRules: TrustedAccountListRules;
  @Input()
  updatedRules: TrustedAccountListRules;
  formRadioGroupInfo: FormRadioGroupInfo;

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  ngOnInit() {
    super.ngOnInit();
    this.formRadioGroupInfo = {
      radioGroupHeading:
        'Is the approval of a second authorised representative necessary to execute transfers to accounts on the trusted account list?',
      radioGroupHeadingCaption: 'Request to update rules for transactions',
      radioGroupHint: 'Select one option',
      key: 'approval_second_ar_required',
      options: [
        {
          label: 'Yes',
          value: true,
          enabled: true,
        },
        {
          label: 'No',
          value: false,
          enabled: true,
        },
      ],
    };
  }

  onContinue() {
    this.onSubmit();
  }

  protected doSubmit() {
    const trustedAccountListPartial = new TrustedAccountListRules();
    trustedAccountListPartial.rule1 = this.formGroup.get(
      'approvalSecondArRequired'
    ).value;
    this.selectTransactionRuleOption.emit(trustedAccountListPartial);
  }

  protected getFormModel(): any {
    if (this.updatedRules != null) {
      return {
        approvalSecondArRequired: [
          this.updatedRules.rule1,
          Validators.required,
        ],
      };
    }
    return {
      approvalSecondArRequired: [this.currentRules.rule1, Validators.required],
    };
  }

  protected getValidationMessages() {
    return {
      approvalSecondArRequired: {
        required: 'Please select an option',
      },
    };
  }
}
