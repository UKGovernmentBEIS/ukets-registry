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
  selectEmailExpiresAt,
  selectRecoveryEmailResendSuccess,
  selectRecoveryPhoneResendSuccess,
} from '../../store/recovery-methods-change.reducer';
import { FormControl, Validators } from '@angular/forms';
import {
  ResetRecoveryEmailStateRequest,
  UpdateRecoveryEmailVerificationRequest,
} from '../../recovery-methods-change.models';
import { ErrorDetail } from '@registry-web/shared/error-summary';
import { UkProtoFormTextComponent } from '@registry-web/shared/form-controls/uk-proto-form-controls';
import { Store } from '@ngrx/store';
import { Observable, of } from 'rxjs';
import { BannerType } from '@shared/banner/banner-type.enum';
import { TimerComponent } from '@user-management/recovery-methods-change/components/timer/timer.component';
import { selectRecoveryEmailAddress } from '@user-management/recovery-methods-change/store/recovery-methods-change.selectors';

@Component({
  selector: 'app-update-recovery-email-verification',
  templateUrl: './update-recovery-email-verification.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UpdateRecoveryEmailVerificationComponent
  extends UkFormComponent
  implements OnInit
{
  @Input() etsRegistryHelpEmail: string;
  @Input() set state(data: RecoveryMethodsChangeState) {
    this._state = data;

    this.sanitizedEmail = data.newRecoveryEmailAddress;
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
    new EventEmitter<UpdateRecoveryEmailVerificationRequest>();
  @Output() resetState = new EventEmitter<ResetRecoveryEmailStateRequest>();

  @ViewChild('securityCodeInput') securityCodeInput: UkProtoFormTextComponent;
  @ViewChild(TimerComponent) timerComponent: TimerComponent;

  timer$: Observable<number>;
  resendDisabled: boolean = true;
  showBanner$: Observable<boolean> = of(false);
  sanitizedEmail: string;
  securityCodeControl = new FormControl('', Validators.required);

  constructor(private store: Store) {
    super();
  }

  ngOnInit() {
    super.ngOnInit();
    this.timer$ = this.store.select(selectEmailExpiresAt);
  }

  protected doSubmit() {
    this.submitRequest.emit({
      newRecoveryEmailAddress: this._state.newRecoveryEmailAddress,
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
        required: 'Enter the security code sent to your email.',
      },
    };
  }

  enableResendButton(disableButton: boolean) {
    this.resendDisabled = disableButton;
    this.showBanner$ = of(false);
  }

  onResendSecurityCode() {
    this.resendSecurityCode.emit();
    this.timerComponent.triggerTimer();
    this.resendDisabled = true;
    //TODO
    this.showBanner$ = of(true);
    //this.showBanner$ = this.store.select(selectRecoveryEmailResendSuccess);
  }

  ngOnDestroy(): void {
    if (!this._state.recoveryEmailAddressSuccess) {
      this.resetState.emit({
        newRecoveryEmailAddress: this._state.recoveryEmailAddress,
      });
    }
  }

  protected readonly BannerType = BannerType;
}
