import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
  ViewChild,
} from '@angular/core';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { RecoveryMethodsChangeState } from '../../store/recovery-methods-change.reducer';
import { FormControl, Validators } from '@angular/forms';
import { UkRegistryValidators } from '@shared/validation';
import { UpdateRecoveryPhoneRequest } from '../../recovery-methods-change.models';
import { PhoneInfo } from '@registry-web/shared/form-controls';
import { CountryCodeModel } from '@registry-web/shared/countries/country-code.model';
import { takeUntil } from 'rxjs';
import { ErrorDetail } from '@registry-web/shared/error-summary';
import { UkProtoFormTextComponent } from '@registry-web/shared/form-controls/uk-proto-form-controls';

@Component({
  selector: 'app-update-recovery-phone-form',
  templateUrl: './update-recovery-phone-form.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UpdateRecoveryPhoneFormComponent
  extends UkFormComponent
  implements OnInit
{
  @Input() state: RecoveryMethodsChangeState;
  @Input() countryCodes: CountryCodeModel[];
  @Input() set serverSideErrorDetails(details: ErrorDetail[]) {
    details.forEach((e) => {
      this.formGroup.markAllAsTouched();
      this.formGroup.get(e.componentId)?.setErrors({ invalid: true });

      if (e.componentId === 'otpCode') {
        this.otpCodeInput.validationErrorMessage = e.errorMessage;
      }
    });
  }

  @Output() submitRequest = new EventEmitter<UpdateRecoveryPhoneRequest>();

  @ViewChild('otpCodeInput') otpCodeInput: UkProtoFormTextComponent;

  private isTheSameAsWorkMobileControl = new FormControl<boolean>(false, {
    updateOn: 'change',
  });
  private newRecoveryPhoneControl: FormControl<PhoneInfo>;
  private otpCodeControl = new FormControl('', Validators.required);

  ngOnInit(): void {
    const newRecoveryPhoneValidators = [UkRegistryValidators.allFieldsRequired];

    if (this.state.recoveryPhoneNumber) {
      newRecoveryPhoneValidators.push(
        UkRegistryValidators.notPermittedValue({
          countryCode: this.state.recoveryCountryCode,
          phoneNumber: this.state.recoveryPhoneNumber,
        })
      );
    }

    this.newRecoveryPhoneControl = new FormControl<PhoneInfo>(
      {
        countryCode:
          this.state.newRecoveryCountryCode ||
          this.state.recoveryCountryCode ||
          '',
        phoneNumber:
          this.state.newRecoveryPhoneNumber ||
          this.state.recoveryPhoneNumber ||
          '',
      },
      {
        updateOn: 'change',
        validators: newRecoveryPhoneValidators,
      }
    );

    this.isTheSameAsWorkMobileControl.valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => this.onSameAsWorkMobileChange());

    this.newRecoveryPhoneControl.valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => this.setIsTheSameAsWorkMobile());

    this.setIsTheSameAsWorkMobile();

    super.ngOnInit();
  }

  private setIsTheSameAsWorkMobile() {
    const newRecoveryCountryCode =
      this.newRecoveryPhoneControl.value?.countryCode;
    const newRecoveryPhoneNumber =
      this.newRecoveryPhoneControl.value?.phoneNumber;
    const workMobileCountryCode = this.state.workMobileCountryCode;
    const workMobilePhoneNumber = this.state.workMobilePhoneNumber;

    this.isTheSameAsWorkMobileControl.patchValue(
      newRecoveryCountryCode &&
        newRecoveryPhoneNumber &&
        newRecoveryCountryCode === workMobileCountryCode &&
        newRecoveryPhoneNumber === workMobilePhoneNumber,
      {
        emitEvent: false,
      }
    );
  }

  private onSameAsWorkMobileChange() {
    if (this.isTheSameAsWorkMobileControl.value) {
      this.newRecoveryPhoneControl.patchValue({
        countryCode: this.state.workMobileCountryCode,
        phoneNumber: this.state.workMobilePhoneNumber,
      });
    }
  }

  protected doSubmit() {
    this.submitRequest.emit({
      newRecoveryCountryCode: this.newRecoveryPhoneControl.value?.countryCode,
      newRecoveryPhoneNumber: this.newRecoveryPhoneControl.value?.phoneNumber,
      otpCode: this.otpCodeControl.value,
    });
  }

  protected getFormModel(): any {
    return {
      isTheSameAsWorkMobile: this.isTheSameAsWorkMobileControl,
      newRecoveryPhone: this.newRecoveryPhoneControl,
      otpCode: this.otpCodeControl,
    };
  }

  protected getValidationMessages(): {
    [key: string]: { [key: string]: string };
  } {
    return {
      newRecoveryPhone: {
        allFieldsRequired: 'Enter a mobile phone number',
        notPermittedValue:
          'Enter a recovery mobile number different from your current recovery mobile number',
      },
      otpCode: {
        required: 'Enter the 6-digit code shown in the authenticator app',
      },
    };
  }
}
