import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import {
  FormRadioGroupInfo,
  FormRadioOption,
} from '@shared/form-controls/uk-radio-input/uk-radio.model';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { Data } from '@angular/router';
import { AccountHolderDetailsType } from '@account-management/account/account-holder-details-wizard/model';

@Component({
  selector: 'app-select-account-holder-details-type',
  templateUrl: './select-account-holder-update-type.component.html',
})
export class SelectAccountHolderUpdateTypeComponent
  extends UkFormComponent
  implements OnInit
{
  @Input() routeData: Data;
  @Input() updateType: AccountHolderDetailsType;

  @Output()
  readonly selectUpdateType = new EventEmitter<AccountHolderDetailsType>();

  updateTypes: FormRadioOption[];
  formRadioGroupInfo: FormRadioGroupInfo;

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }
  ngOnInit() {
    super.ngOnInit();
    this.formRadioGroupInfo = {
      radioGroupHeading: 'Select type of update',
      radioGroupHeadingCaption: 'Request to update the account holder',
      radioGroupHint: 'Select one option',
      key: 'accountHolderDetailsType',
      options: this.routeData.updateTypes,
    };
  }

  protected doSubmit() {
    this.selectUpdateType.emit(
      this.formGroup.get('accountHolderDetailsType').value
    );
  }

  protected getFormModel(): any {
    return {
      accountHolderDetailsType: [this.updateType, Validators.required],
    };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      accountHolderDetailsType: {
        required: 'Select type of update',
      },
    };
  }

  onContinue() {
    this.onSubmit();
  }
}
