import {
  Component,
  computed,
  EventEmitter,
  Input,
  OnInit,
  Output,
  TemplateRef,
  viewChild,
} from '@angular/core';
import { FormControl, Validators } from '@angular/forms';
import { Option } from '@shared/form-controls/uk-select-input/uk-select.model';
import { IUkOfficialCountry } from '@shared/countries/country.interface';
import { FormRadioGroupInfo } from '@shared/form-controls/uk-radio-input/uk-radio.model';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { PublicAgentDetails, UserAgentUpdateRequest } from '@user-agent/model';
import { PhoneInfo } from '@shared/form-controls/uk-select-phone/phone.model';
import {
  AGENT_TYPE_LABELS,
  AgentType,
} from '@user-management/user-details/model/agent.model';
import { CountryCodeModel } from '@shared/countries/country-code.model';
import { takeUntil } from 'rxjs/operators';
import { UkRegistryValidators } from '@registry-web/shared/validation';

@Component({
  selector: 'app-user-agent-form',
  templateUrl: './user-agent-form.component.html',
})
export class UserAgentFormComponent extends UkFormComponent implements OnInit {
  @Input()
  agentType: AgentType;
  @Input()
  countryCodes: CountryCodeModel[];

  @Output()
  readonly agentDetailsOutput = new EventEmitter<UserAgentUpdateRequest>();

  _agentDetails: PublicAgentDetails;
  _agentPhoneInfo: PhoneInfo;
  _countries: IUkOfficialCountry[];
  _countryOptions: Option[];

  @Input()
  set agentDetails(value: PublicAgentDetails) {
    if (this.agentType === 'YES_PUBLIC') {
      this._agentPhoneInfo = {
        phoneNumber: value.phone?.phoneNumber,
        countryCode: value.phone?.countryCode,
      };
    }
    this._agentDetails = value;
  }

  @Input()
  set countries(value: IUkOfficialCountry[]) {
    if (value) {
      this._countries = value;
      this._countryOptions = this.countries.map((c) => ({
        label: c.item[0].name,
        value: c.key,
      }));
    }
  }

  get countries() {
    return this._countries;
  }

  ngOnInit() {
    super.ngOnInit();
    //Init components enable/disable state based on agentTypeControl value
    if (this.agentType === 'YES_PUBLIC') {
      this.agentCompanyNameControl.enable();
      this.agentContactEmailAddressControl.enable();
      this.agentPhoneControl.enable();
      this.formGroup.get('agentDetails').enable();
    } else {
      this.agentCompanyNameControl.disable();
      this.agentContactEmailAddressControl.disable();
      this.agentPhoneControl.disable();
      this.formGroup.get('agentDetails').disable();
    }

    //Subscribe for changes
    this.agentTypeControl.valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe((agentType) => {
        if (agentType === 'YES_PUBLIC') {
          this.agentCompanyNameControl.enable();
          this.agentContactEmailAddressControl.enable();
          this.agentPhoneControl.enable();
          this.formGroup.get('agentDetails').enable();
        } else {
          this.agentCompanyNameControl.disable();
          this.agentContactEmailAddressControl.disable();
          this.agentPhoneControl.disable();
          this.formGroup.get('agentDetails').disable();
        }
        this.formGroup.markAsUntouched();
        this.formGroup.updateValueAndValidity();
        this.errorDetails.emit([]);
      });
  }

  protected getFormModel(): any {
    return {
      agentType: [
        this.agentType ? this.agentType : null,
        {
          validators: [Validators.required],
          updateOn: 'change',
        },
      ],
      agentDetails: this.formBuilder.group({
        companyName: [
          this._agentDetails ? this._agentDetails.companyName : null,
          { validators: [Validators.required] },
        ],
        contactEmailAddress: [
          this._agentDetails ? this._agentDetails.contactEmailAddress : null,
          { validators: [Validators.required] },
        ],
        phone: [
          this._agentPhoneInfo ?? { countryCode: '', phoneNumber: '' },
          { validators: [UkRegistryValidators.allFieldsRequired] },
        ],
      }),
    };
  }
  protected getValidationMessages(): {
    [key: string]: { [key: string]: string };
  } {
    return {
      agentType: {
        required: 'Select the agent type',
      },
      contactEmailAddress: {
        required: 'Please enter the email address',
        email:
          'Enter an email address in the correct format, like name@example.com',
        maxLength: 'Email address should not exceed 256 characters',
      },
      phone: {
        allFieldsRequired: 'Please enter the phone number',
      },
      companyName: { required: 'Please enter the company name' },
    };
  }

  onContinue() {
    this.onSubmit();
  }

  doSubmit() {
    const outputModel: UserAgentUpdateRequest = {
      agent: null,
      agentCompanyName: null,
      agentEmailAddress: null,
      agentPhoneNumberCountryCode: null,
      agentPhoneNumber: null,
    };

    outputModel.agent = this.agentTypeControl.value;

    if (outputModel.agent === 'YES_PUBLIC') {
      if (this.agentCompanyNameControl.value.trim() === '') {
        outputModel.agentCompanyName = null;
      } else {
        outputModel.agentCompanyName = this.agentCompanyNameControl.value;
      }

      if (this.agentContactEmailAddressControl.value.trim() === '') {
        outputModel.agentEmailAddress = null;
      } else {
        outputModel.agentEmailAddress =
          this.agentContactEmailAddressControl.value;
      }

      const agentPhoneInfo = this.agentPhoneControl.value;

      if (agentPhoneInfo.phoneNumber?.trim() === '') {
        outputModel.agentPhoneNumberCountryCode = null;
        outputModel.agentPhoneNumber = null;
      } else {
        outputModel.agentPhoneNumberCountryCode = agentPhoneInfo.countryCode;
        outputModel.agentPhoneNumber = agentPhoneInfo.phoneNumber;
      }
    }

    this.agentDetailsOutput.emit(outputModel);
  }

  readonly agentTypeRadioGroup = computed<FormRadioGroupInfo>(() => ({
    key: 'agentType',
    options: [
      {
        label: AGENT_TYPE_LABELS['YES_PUBLIC'].label,
        value: 'YES_PUBLIC',
        enabled: true,
        conditionalTemplate: this.agentDetailsTemplate(),
      },
      {
        label: AGENT_TYPE_LABELS['YES_PRIVATE'].label,
        value: 'YES_PRIVATE',
        enabled: true,
      },
      {
        label: AGENT_TYPE_LABELS['NO'].label,
        value: 'NO',
        enabled: true,
      },
    ],
  }));

  private readonly agentDetailsTemplate = viewChild.required(
    'agentDetailsTemplate',
    { read: TemplateRef }
  );

  //Form controls getters
  private get agentTypeControl() {
    return this.formGroup.get('agentType') as FormControl<AgentType>;
  }

  private get agentCompanyNameControl() {
    return this.formGroup
      .get('agentDetails')
      .get('companyName') as FormControl<string>;
  }
  private get agentContactEmailAddressControl() {
    return this.formGroup
      .get('agentDetails')
      .get('contactEmailAddress') as FormControl<string>;
  }

  private get agentPhoneControl() {
    return this.formGroup
      .get('agentDetails')
      .get('phone') as FormControl<PhoneInfo>;
  }

  showAgentDetailsError(): boolean {
    return (
      this.formGroup.touched &&
      this.agentTypeControl.value === 'YES_PUBLIC' &&
      !!this.formGroup.get('agentDetails').errors &&
      !!this.validationErrorMessage.agentDetails
    );
  }
}
