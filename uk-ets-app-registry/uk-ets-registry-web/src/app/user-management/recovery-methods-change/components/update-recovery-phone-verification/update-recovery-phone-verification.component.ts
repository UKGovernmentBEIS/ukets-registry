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
import {
  RecoveryMethodsChangeState,
  selectPhoneExpiresAt,
  selectRecoveryPhoneResendSuccess,
} from '../../store/recovery-methods-change.reducer';
import { FormControl, Validators } from '@angular/forms';
import {
  ResetRecoveryPhoneStateRequest,
  UpdateRecoveryPhoneVerificationRequest,
} from '../../recovery-methods-change.models';
import { ErrorDetail } from '@registry-web/shared/error-summary';
import { UkProtoFormTextComponent } from '@registry-web/shared/form-controls/uk-proto-form-controls';
import { Store } from '@ngrx/store';
import { Observable, of } from 'rxjs';
import { TimerComponent } from '@user-management/recovery-methods-change/components/timer/timer.component';
import { BannerType } from '@shared/banner/banner-type.enum';

@Component({
  selector: 'app-update-recovery-phone-verification',
  templateUrl: './update-recovery-phone-verification.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UpdateRecoveryPhoneVerificationComponent
  extends UkFormComponent
  implements OnInit
{
  @Input() etsRegistryHelpEmail: string;
  @Input() set state(data: RecoveryMethodsChangeState) {
    this._state = data;

    if (data.newRecoveryPhoneNumber) {
      this.sanitizedPhone = this.getSanitizedPhone(
        data.newRecoveryCountryCode,
        data.newRecoveryPhoneNumber
      );
    }
  }
  private _state: RecoveryMethodsChangeState;

  @Input() set serverSideErrorDetails(details: ErrorDetail[]) {
    details.forEach((e) => {
      this.formGroup.markAllAsTouched();
      this.formGroup.get(e.componentId)?.setErrors({ invalid: true });

      if (e.componentId === 'securityCode') {
        this.securityCodeInput.validationErrorMessage = e.errorMessage;
      }
    });
  }

  @Output() resendSecurityCode = new EventEmitter<void>();
  @Output() submitRequest =
    new EventEmitter<UpdateRecoveryPhoneVerificationRequest>();
  @Output() resetState = new EventEmitter<ResetRecoveryPhoneStateRequest>();

  @ViewChild('securityCodeInput') securityCodeInput: UkProtoFormTextComponent;
  @ViewChild(TimerComponent) timerComponent: TimerComponent;

  timer$: Observable<number>;
  resendDisabled: boolean = true;
  showBanner$: Observable<boolean> = of(false);
  sanitizedPhone: string;
  securityCodeControl = new FormControl('', Validators.required);

  constructor(private store: Store) {
    super();
  }

  ngOnInit() {
    super.ngOnInit();
    this.timer$ = this.store.select(selectPhoneExpiresAt);
  }

  private getSanitizedPhone(countryCode: string, phoneNumber: string) {
    return countryCode + ' ' + phoneNumber;
  }

  protected doSubmit() {
    this.submitRequest.emit({
      newRecoveryCountryCode: this._state.newRecoveryCountryCode,
      newRecoveryPhoneNumber: this._state.newRecoveryPhoneNumber,
      securityCode: this.securityCodeControl.value,
    });
  }

  protected getFormModel(): any {
    return { securityCode: this.securityCodeControl };
  }

  protected getValidationMessages(): {
    [key: string]: { [key: string]: string };
  } {
    return {
      securityCode: {
        required: 'Enter the security code sent to your phone number.',
      },
    };
  }

  enableResendButton(disableButton: boolean) {
    this.resendDisabled = disableButton;
    this.showBanner$ = of(false);
  }

  onResendSecurityCode() {
    this.resendSecurityCode.emit();
    //this.timer$ = this.store.select(selectPhoneExpiresAt);
    this.timerComponent.triggerTimer();
    this.resendDisabled = true;
    //TODO
    this.showBanner$ = of(true);
    //this.showBanner$ = this.store.select(selectRecoveryPhoneResendSuccess);
  }

  ngOnDestroy(): void {
    if (!this._state.recoveryPhoneNumberSuccess) {
      this.resetState.emit({
        newRecoveryCountryCode: this._state.recoveryCountryCode,
        newRecoveryPhoneNumber: this._state.recoveryPhoneNumber,
      });
    }
  }

  protected readonly BannerType = BannerType;
}
