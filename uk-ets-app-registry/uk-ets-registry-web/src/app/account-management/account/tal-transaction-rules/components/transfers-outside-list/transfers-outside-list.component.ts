import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormRadioGroupInfo } from '@shared/form-controls/uk-radio-input/uk-radio.model';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { TrustedAccountListRules } from '@shared/model/account';

@Component({
  selector: 'app-transfers-outside-list',
  templateUrl: './transfers-outside-list.component.html',
})
export class TransfersOutsideListComponent
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
        'Are transfers to accounts not on the trusted account list allowed?',
      radioGroupHeadingCaption: 'Request to update rules for transactions',
      radioGroupHint: 'Select one option',
      key: 'updateType',
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
    trustedAccountListPartial.rule2 = this.formGroup.get(
      'transfersOutsideTalAllowed'
    ).value;
    this.selectTransactionRuleOption.emit(trustedAccountListPartial);
  }

  protected getFormModel(): any {
    if (this.updatedRules.rule2 != null) {
      return {
        transfersOutsideTalAllowed: [
          this.updatedRules.rule2,
          Validators.required,
        ],
      };
    }
    return {
      transfersOutsideTalAllowed: [
        this.currentRules.rule2,
        Validators.required,
      ],
    };
  }

  protected getValidationMessages() {
    return {
      transfersOutsideTalAllowed: {
        required: 'Please select an option',
      },
    };
  }
}
