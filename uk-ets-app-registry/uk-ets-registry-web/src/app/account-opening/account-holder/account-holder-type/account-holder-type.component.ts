import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { ErrorDetail } from '@shared/error-summary';
import {
  UntypedFormBuilder,
  UntypedFormGroup,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { AccountHolderType } from '@shared/model/account';
import { FormRadioGroupInfo } from '@shared/form-controls/uk-radio-input/uk-radio.model';
import { UkValidationMessageHandler } from '@shared/validation';

@Component({
  selector: 'app-account-holder-type',
  templateUrl: './account-holder-type.component.html',
})
export class AccountHolderTypeComponent implements OnInit {
  @Input() accountHolderType: AccountHolderType;
  @Output()
  readonly selectedAccountHolderType = new EventEmitter<AccountHolderType>();
  @Output() readonly errorDetails = new EventEmitter<ErrorDetail[]>();

  accountHolderTypeFormGroup: UntypedFormGroup;

  validationErrorMessage: ValidationErrors = {};
  private validationMessages: { [key: string]: { [key: string]: string } };
  private genericValidator: UkValidationMessageHandler;

  formRadioGroupInfo: FormRadioGroupInfo;

  constructor(private formBuilder: UntypedFormBuilder) {}

  ngOnInit() {
    this.formRadioGroupInfo = {
      radioGroupHeading: 'Choose the account holder',
      radioGroupHeadingCaption: 'Add the account holder',
      radioGroupHint:
        'The account holder is the organisation or individual responsible for operating the account',
      key: 'accountHolderType',
      options: [
        {
          label: 'Organisation',
          hint: 'For example a company or partnership',
          value: AccountHolderType.ORGANISATION,
          enabled: true,
        },
        {
          label: 'Individual',
          hint: 'A person who is not acting on behalf of an organisation, for example a sole trader',
          value: AccountHolderType.INDIVIDUAL,
          enabled: true,
        },
      ],
    };

    this.accountHolderTypeFormGroup = this.formBuilder.group(
      {
        accountHolderType: ['', Validators.required],
      },
      { updateOn: 'submit' }
    );

    if (this.accountHolderType) {
      this.accountHolderTypeFormGroup
        .get('accountHolderType')
        .patchValue(this.accountHolderType);
    }
    this.validationMessages = {
      accountHolderType: {
        required: 'Select the account holder type.',
      },
    };
    this.genericValidator = new UkValidationMessageHandler(
      this.validationMessages
    );
  }

  onContinue() {
    this.accountHolderTypeFormGroup.markAllAsTouched();
    if (this.accountHolderTypeFormGroup.valid) {
      this.selectedAccountHolderType.emit(
        this.accountHolderTypeFormGroup.get('accountHolderType').value
      );
    } else {
      this.validationErrorMessage = this.genericValidator.processMessages(
        this.accountHolderTypeFormGroup
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
      this.accountHolderTypeFormGroup.invalid &&
      this.accountHolderTypeFormGroup.touched
    );
  }
}
