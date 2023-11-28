import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  AccessRightLabelHintMap,
  ARAccessRights,
  AuthorisedRepresentative,
} from '@shared/model/account/authorised-representative';
import { ErrorDetail } from '@shared/error-summary';
import {
  UntypedFormBuilder,
  UntypedFormGroup,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { FormRadioGroupInfo } from '@shared/form-controls/uk-radio-input/uk-radio.model';
import { UkValidationMessageHandler } from '@shared/validation';

@Component({
  selector: 'app-access-rights',
  templateUrl: './access-rights.component.html',
})
export class AccessRightsComponent implements OnInit {
  @Output() readonly selectedAccessRights = new EventEmitter<ARAccessRights>();
  @Output() readonly errorDetails = new EventEmitter<ErrorDetail[]>();

  _authorisedRepresentative: AuthorisedRepresentative;
  accessRightsFormGroup: UntypedFormGroup;

  validationErrorMessage: ValidationErrors = {};
  private validationMessages: { [key: string]: { [key: string]: string } };
  private genericValidator: UkValidationMessageHandler;

  arAccessRightGroupInfo: FormRadioGroupInfo;
  constructor(private formBuilder: UntypedFormBuilder) {}

  @Input() showSurrender: boolean;

  @Input()
  set authorisedRepresentative(value: AuthorisedRepresentative) {
    this._authorisedRepresentative = value;
  }

  ngOnInit() {
    this.arAccessRightGroupInfo = {
      radioGroupHeading: 'Choose the permissions for this representative',
      radioGroupHeadingCaption: 'Add an Authorised Representative',
      radioGroupHint: '',
      key: 'arAccessRight',
      options: [
        {
          label: AccessRightLabelHintMap.get(
            ARAccessRights.INITIATE_AND_APPROVE
          ).text,
          value: ARAccessRights.INITIATE_AND_APPROVE,
          hint: AccessRightLabelHintMap.get(ARAccessRights.INITIATE_AND_APPROVE)
            .hint,
          enabled: true,
        },
        {
          label: AccessRightLabelHintMap.get(ARAccessRights.APPROVE).text,
          value: ARAccessRights.APPROVE,
          hint: AccessRightLabelHintMap.get(ARAccessRights.APPROVE).hint,
          enabled: true,
        },
        {
          label: AccessRightLabelHintMap.get(ARAccessRights.INITIATE).text,
          value: ARAccessRights.INITIATE,
          hint: AccessRightLabelHintMap.get(ARAccessRights.INITIATE).hint,
          enabled: true,
        },
      ],
    };

    if (this.showSurrender) {
      this.arAccessRightGroupInfo.options.push({
        label: AccessRightLabelHintMap.get(
          ARAccessRights.SURRENDER_INITIATE_AND_APPROVE
        ).text,
        value: ARAccessRights.SURRENDER_INITIATE_AND_APPROVE,
        hint: AccessRightLabelHintMap.get(
          ARAccessRights.SURRENDER_INITIATE_AND_APPROVE
        ).hint,
        enabled: true,
      });
    }

    this.accessRightsFormGroup = this.formBuilder.group(
      {
        arAccessRights: ['', [Validators.required]],
      },
      { updateOn: 'submit' }
    );

    this.validationMessages = {
      arAccessRights: {
        required: 'Select the permissions.',
      },
    };
    this.genericValidator = new UkValidationMessageHandler(
      this.validationMessages
    );
    this.accessRightsFormGroup
      .get('arAccessRights')
      .setValue(this._authorisedRepresentative.right);
  }

  onContinue() {
    this.accessRightsFormGroup.markAllAsTouched();
    if (this.accessRightsFormGroup.valid) {
      this.selectedAccessRights.emit(
        this.accessRightsFormGroup.get('arAccessRights').value
      );
    } else {
      this.validationErrorMessage = this.genericValidator.processMessages(
        this.accessRightsFormGroup
      );
      this.errorDetails.emit(
        this.genericValidator.mapMessagesToErrorDetails(
          this.validationErrorMessage
        )
      );
    }
  }

  showErrors(): boolean {
    return (
      this.accessRightsFormGroup.invalid && this.accessRightsFormGroup.touched
    );
  }
}
