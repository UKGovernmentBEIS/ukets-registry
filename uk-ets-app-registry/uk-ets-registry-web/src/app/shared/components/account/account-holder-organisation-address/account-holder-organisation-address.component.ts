import {
  AfterViewInit,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
} from '@angular/core';
import {
  UntypedFormBuilder,
  UntypedFormGroup,
  Validators,
} from '@angular/forms';
import { IUkOfficialCountry } from '@shared/countries/country.interface';
import { ErrorDetail } from '@shared/error-summary/error-detail';
import {
  AccountHolder,
  Organisation,
} from '@shared/model/account/account-holder';
import {
  UkRegistryValidators,
  UkValidationMessageHandler,
} from '@shared/validation';

@Component({
  selector: 'app-account-holder-organisation-address',
  templateUrl: './account-holder-organisation-address.component.html',
})
// TODO This is the old OrganisationAddressComponent which became Shared. Needs refactoring to use UkFormComponent
export class AccountHolderOrganisationAddressComponent
  implements OnInit, AfterViewInit
{
  @Input() caption: string;
  @Input() header: string;
  @Input() isAHUpdateWizard = false;
  @Input() accountHolder: AccountHolder;
  @Input() countries: IUkOfficialCountry[];
  @Output() readonly outputUser = new EventEmitter<AccountHolder>();
  @Output() readonly errorDetails = new EventEmitter<ErrorDetail[]>();

  validationErrorMessage: { [key: string]: string } = {};
  private validationMessages: { [key: string]: { [key: string]: string } };
  private genericValidator: UkValidationMessageHandler;

  formGroup: UntypedFormGroup = this.formBuilder.group(
    {
      address: this.formBuilder.group({
        buildingAndStreet: [
          '',
          [
            Validators.required,
            UkRegistryValidators.checkForOnlyWhiteSpaces('required'),
          ],
        ],
        buildingAndStreet2: [''],
        buildingAndStreet3: [''],
        postCode: [''],
        townOrCity: [
          '',
          [
            Validators.required,
            UkRegistryValidators.checkForOnlyWhiteSpaces('required'),
          ],
        ],
        stateOrProvince: [''],
        country: [
          '',
          [
            Validators.required,
            UkRegistryValidators.checkForOnlyWhiteSpaces('required'),
          ],
        ],
      }),
    },
    { updateOn: 'submit' }
  );

  constructor(private formBuilder: UntypedFormBuilder) {}

  ngOnInit() {
    this.formGroup.get('address.country').valueChanges.subscribe((value) => {
      if (value !== 'UK') {
        this.formGroup.get('address.postCode').clearValidators();
      } else {
        this.formGroup
          .get('address.postCode')
          .setValidators(Validators.required);
      }
      this.formGroup.get('address.postCode').updateValueAndValidity();
    });

    this.validationMessages = {
      buildingAndStreet: {
        required: 'Enter the address line 1.',
      },
      townOrCity: {
        required: 'Enter the town or city.',
      },
      postCode: {
        required: 'Enter the UK postal code or zip.',
      },
    };
    this.genericValidator = new UkValidationMessageHandler(
      this.validationMessages
    );
  }

  ngAfterViewInit(): void {
    if (!this.formGroup.get('address.country').value) {
      this.formGroup.get('address').patchValue({ country: 'UK' });
      this.formGroup.get('address.country').updateValueAndValidity();
    }
  }

  onContinue() {
    this.formGroup.markAllAsTouched();
    if (this.formGroup.valid) {
      const updateObject = new Organisation();
      updateObject.address = this.formGroup.get('address').value;
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
}
