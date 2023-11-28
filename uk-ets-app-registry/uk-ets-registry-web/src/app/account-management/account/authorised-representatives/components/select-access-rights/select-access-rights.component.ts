import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormRadioGroupInfo } from '@shared/form-controls/uk-radio-input/uk-radio.model';
import {
  ARAccessRights,
  arAccessRightsRadioOptionMap,
} from '@shared/model/account';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { AuthorisedRepresentativesUpdateType } from '@authorised-representatives/model';

@Component({
  selector: 'app-select-access-rights',
  templateUrl: './select-access-rights.component.html',
})
export class SelectAccessRightsComponent
  extends UkFormComponent
  implements OnInit
{
  @Input()
  updateType: AuthorisedRepresentativesUpdateType;
  @Input()
  accessRights: ARAccessRights;
  @Input()
  arFullName: string;
  @Input()
  showSurrender: boolean;
  @Output() readonly selectAccessRights = new EventEmitter<ARAccessRights>();

  formRadioGroupInfo: FormRadioGroupInfo;

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  ngOnInit() {
    let options = Array.from(arAccessRightsRadioOptionMap.values());

    if (!this.showSurrender) {
      options = options?.filter(
        (option) =>
          option.value !== ARAccessRights.SURRENDER_INITIATE_AND_APPROVE
      );
    }

    super.ngOnInit();
    this.formRadioGroupInfo = {
      radioGroupHeading: null,
      radioGroupHeadingCaption: null,
      radioGroupHint: 'Select one option',
      key: 'accessRights',
      options: options,
    };
  }

  onContinue() {
    this.onSubmit();
  }

  protected doSubmit() {
    this.selectAccessRights.emit(this.formGroup.get('accessRights').value);
  }

  protected getFormModel(): any {
    return {
      accessRights: [this.accessRights, Validators.required],
    };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      accessRights: {
        required: 'Select the permissions',
      },
    };
  }
}
