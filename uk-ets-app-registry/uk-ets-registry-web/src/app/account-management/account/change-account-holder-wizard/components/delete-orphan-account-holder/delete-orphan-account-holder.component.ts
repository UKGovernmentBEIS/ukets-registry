import { Component, input, OnInit, output } from '@angular/core';
import {
  FormControl,
  FormGroup,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { ErrorDetail } from '@registry-web/shared/error-summary';
import { FormRadioGroupInfo } from '@registry-web/shared/form-controls/uk-radio-input/uk-radio.model';
import { isNil } from '@registry-web/shared/shared.util';
import { UkValidationMessageHandler } from '@registry-web/shared/validation';

@Component({
  selector: 'app-delete-orphan-account-holder',
  templateUrl: './delete-orphan-account-holder.component.html',
})
export class DeleteOrphanAccountHolderComponent implements OnInit {
  readonly caption = input.required<string>();
  readonly accountHolderDelete = input.required<boolean>();
  readonly valueSelected = output<boolean>();
  readonly errorDetails = output<ErrorDetail[]>();

  private readonly accountHolderDeleteControl = new FormControl<boolean | null>(
    null,
    [Validators.required]
  );
  readonly formGroup = new FormGroup(
    { accountHolderDelete: this.accountHolderDeleteControl },
    { updateOn: 'submit' }
  );

  validationErrorMessage: ValidationErrors = {};
  private validationMessages: { [key: string]: { [key: string]: string } };
  private genericValidator: UkValidationMessageHandler;

  formRadioGroupInfo: FormRadioGroupInfo;

  ngOnInit() {
    this.formRadioGroupInfo = {
      radioGroupHeadingCaption: this.caption(),
      radioGroupHeading:
        'Do you want to delete the Account Holder named above?',
      key: 'accountHolderDelete',
      options: [
        { label: 'Yes', value: true, enabled: true },
        { label: 'No', value: false, enabled: true },
      ],
    };

    if (!isNil(this.accountHolderDelete())) {
      this.accountHolderDeleteControl.patchValue(this.accountHolderDelete());
    }
    this.validationMessages = {
      accountHolderDelete: {
        required:
          'Select yes if you want to delete the Account Holder named above.',
      },
    };
    this.genericValidator = new UkValidationMessageHandler(
      this.validationMessages
    );
  }

  onContinue() {
    this.formGroup.markAllAsTouched();
    if (this.formGroup.valid) {
      this.valueSelected.emit(this.accountHolderDeleteControl.value);
    } else {
      this.validationErrorMessage = this.genericValidator.processMessages(
        this.formGroup
      );
      this.errorDetails.emit(
        this.genericValidator.mapMessagesToErrorDetails(
          this.validationErrorMessage
        )
      );
    }
  }

  showErrors(): boolean {
    return this.formGroup.invalid && this.formGroup.touched;
  }
}
