import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  AbstractControl,
  UntypedFormBuilder,
  UntypedFormGroup,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from '@angular/forms';
import { ErrorDetail } from '@shared/error-summary/error-detail';

import {
  AccountHolder,
  Organisation,
  RegistrationNumberType,
} from '@shared/model/account/account-holder';
import {
  UkRegistryValidators,
  UkValidationMessageHandler,
} from '@shared/validation';

@Component({
  selector: 'app-account-holder-organisation-details',
  templateUrl: './account-holder-organisation-details.component.html',
})
// TODO This is the old OrganisationDetailsComponent which became Shared. Needs refactoring to use UkFormComponent
export class AccountHolderOrganisationDetailsComponent implements OnInit {
  @Input() caption: string;
  @Input() header: string;
  @Input() isAHUpdateWizard = false;
  @Input() accountHolder: AccountHolder;

  @Output() readonly outputUser = new EventEmitter<AccountHolder>();
  @Output() readonly errorDetails = new EventEmitter<ErrorDetail[]>();

  validationErrorMessage: { [key: string]: string } = {};
  private validationMessages: { [key: string]: { [key: string]: string } };
  private genericValidator: UkValidationMessageHandler;

  regNumType: RegistrationNumberType;

  readonly regNumTypes = RegistrationNumberType;

  formGroup: UntypedFormGroup = this.formBuilder.group(
    {
      details: this.formBuilder.group({
        name: [
          '',
          [
            Validators.required,
            UkRegistryValidators.checkForOnlyWhiteSpaces('required'),
          ],
        ],
        // this group is nested only because we want to show the 'name' validation errors
        // before the registration number errors. when the registration number fields
        // were at the same level with the name field the validator was on the level of
        // the details FormGroup so the errors were shown before the errors for the name...
        regNum: this.formBuilder.group(
          {
            registrationNumber: [''],
            noRegistrationNumJustification: [''],
            regNumTypeRadio: [''],
          },
          {
            validators: this.registrationNumberValidator(),
          }
        ),
      }),
    },
    { updateOn: 'submit' }
  );

  constructor(private formBuilder: UntypedFormBuilder) {}

  ngOnInit() {
    this.validationMessages = {
      name: {
        required: 'Enter the company  name',
      },
      regNum: {
        bothEmpty: 'Select if you have a company registration number',
        regNumRequired: 'Enter the company registration number',
        regNumReasonRequired:
          'Enter a reason for not providing a company registration number',
      },
    };
    this.genericValidator = new UkValidationMessageHandler(
      this.validationMessages
    );

    this.regNumType = this.initRegNumType();
    this.initNestedRegNumFormGroup();
  }

  onContinue() {
    this.formGroup.markAllAsTouched();
    if (this.formGroup.valid) {
      const updateObject = new Organisation();
      updateObject.details = {
        name: this.formGroup.get('details.name').value,
        registrationNumber:
          this.registrationNumberFormGroup.get('registrationNumber').value,
        noRegistrationNumJustification: this.registrationNumberFormGroup.get(
          'noRegistrationNumJustification'
        ).value,
      };
      this.outputUser.emit(updateObject);
    } else {
      this.validationErrorMessage = this.genericValidator.processMessages(
        this.formGroup
      );
      const errorDetails: ErrorDetail[] =
        this.genericValidator.mapMessagesToErrorDetails(
          this.validationErrorMessage
        );
      this.errorDetails.emit(errorDetails);
    }
  }

  // the radio button values are not part of the account holder info but derived from it.
  // TODO: maybe this should be an Input
  initRegNumType(): RegistrationNumberType {
    const org = this.accountHolder as Organisation;
    if (org?.details?.registrationNumber) {
      return RegistrationNumberType.REGISTRATION_NUMBER;
    } else if (org?.details?.noRegistrationNumJustification) {
      return RegistrationNumberType.REGISTRATION_NUMBER_REASON;
    }
  }

  selectRegNumType(regNumType: RegistrationNumberType) {
    this.regNumType = regNumType;
    // patchValue instead of reset is used because the validator checks for empty values
    // while reset sets the values to null.
    this.registrationNumberFormGroup.get('registrationNumber').patchValue('');
    this.registrationNumberFormGroup
      .get('noRegistrationNumJustification')
      .patchValue('');
  }

  /**
   * First checks if radio is selected. If it is not then a bothEmpty error is returned.
   * If radio is selected, checks if the corresponding field is empty;.
   */
  registrationNumberValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (this.regNumType === undefined) {
        return { bothEmpty: true };
      }
      if (this.regNumType === RegistrationNumberType.REGISTRATION_NUMBER) {
        const regNum = control.get('registrationNumber').value;
        if (regNum === '') {
          return { regNumRequired: true };
        }
      }

      if (
        this.regNumType === RegistrationNumberType.REGISTRATION_NUMBER_REASON
      ) {
        const regNumReason = control.get(
          'noRegistrationNumJustification'
        ).value;
        if (
          regNumReason &&
          (regNumReason === '' || regNumReason.trim() === '')
        ) {
          return { regNumReasonRequired: true };
        }
      }
      return null;
    };
  }

  get registrationNumberFormGroup(): UntypedFormGroup {
    return this.formGroup.get('details.regNum') as UntypedFormGroup;
  }

  /**
   * The nested FormGroup structure does not follow the accountHolder details model structure
   * anymore, so we have to update the control values manually
   */
  private initNestedRegNumFormGroup() {
    const details = (this.accountHolder as Organisation)?.details;

    this.registrationNumberFormGroup.patchValue({
      registrationNumber: details?.registrationNumber,
      noRegistrationNumJustification: details?.noRegistrationNumJustification,
      regNumTypeRadio: this.regNumType,
    });
  }
}
