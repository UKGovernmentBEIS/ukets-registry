import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormRadioGroupInfo } from '@shared/form-controls/uk-radio-input/uk-radio.model';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { TrustedAccountListRules } from '@shared/model/account';

@Component({
  selector: 'app-single-person-surrender-excess-allocation',
  templateUrl: './single-person-surrender-excess-allocation.component.html',
})
export class SinglePersonSurrenderExcessAllocationComponent
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
        'Is the approval of a second AR necessary to execute a surrender transaction or a return of excess allocation?',
      radioGroupHeadingCaption: 'Request to update rules for transactions',
      radioGroupHint: 'Select one option',
      key: 'singlePersonSurrenderAllocationAllowed',
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
    trustedAccountListPartial.rule3 = this.formGroup.get(
      'singlePersonSurrenderAllocationAllowed'
    ).value;
    this.selectTransactionRuleOption.emit(trustedAccountListPartial);
  }

  protected getFormModel(): any {
    if (this.updatedRules.rule3 != null) {
      return {
        singlePersonSurrenderAllocationAllowed: [
          this.updatedRules.rule3,
          Validators.required,
        ],
      };
    }
    return {
      singlePersonSurrenderAllocationAllowed: [
        this.currentRules.rule3,
        Validators.required,
      ],
    };
  }

  protected getValidationMessages() {
    return {
      singlePersonSurrenderAllocationAllowed: {
        required: 'Please select an option',
      },
    };
  }
}
