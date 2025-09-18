import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { EmailChangeRequest } from '@email-change/model';
import { EmailChangeState } from '@email-change/reducer';
import { Store } from '@ngrx/store';
import {
  UntypedFormBuilder,
  UntypedFormControl,
  Validators,
} from '@angular/forms';
import { canGoBack } from '@shared/shared.action';
import { UkRegistryValidators } from '@shared/validation';
import { KeycloakUser } from '@shared/user';

@Component({
  selector: 'app-new-email-form',
  templateUrl: './new-email-form.component.html',
})
export class NewEmailFormComponent extends UkFormComponent implements OnInit {
  @Output()
  readonly submitEmailChangeRequest = new EventEmitter<EmailChangeRequest>();
  @Input() state: EmailChangeState;
  @Input() currentUser: KeycloakUser;

  constructor(
    private store: Store,
    protected formBuilder: UntypedFormBuilder
  ) {
    super();
  }

  ngOnInit(): void {
    this.store.dispatch(
      canGoBack({
        goBackRoute: this.state.caller.route,
        extras: this.state.caller.extras,
      })
    );
    super.ngOnInit();
  }

  get newEmailControl(): UntypedFormControl {
    return this.formGroup.get('newEmail') as UntypedFormControl;
  }

  protected doSubmit() {
    this.submitEmailChangeRequest.emit({
      urid: this.currentUser.attributes.urid[0],
      newEmail: this.newEmailControl.value,
      otp: this.otpControl.value,
    });
  }

  get otpControl(): UntypedFormControl {
    return this.formGroup.get('otpCode') as UntypedFormControl;
  }

  protected getFormModel(): any {
    return {
      newEmail: [
        this.state.newEmail,
        [
          Validators.required,
          Validators.email,
          Validators.maxLength(256),
          UkRegistryValidators.notPermittedValue(this.currentUser?.email),
        ],
      ],
      newEmailConfirmation: [
        this.state.newEmail,
        [
          Validators.required,
          Validators.email,
          Validators.maxLength(256),
          UkRegistryValidators.emailVerificationMatcher('newEmail'),
        ],
      ],
      otpCode: ['', Validators.required],
    };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      newEmail: {
        required: 'Enter your new email address',
        email:
          'Enter an email address in the correct format, like name@example.com',
        maxLength: 'Enter an email address with less than 256 characters',
        notPermittedValue:
          'Please enter an email address different from your current email address',
      },
      newEmailConfirmation: {
        required: 'Confirm your new email address',
        email:
          'Enter an email address in the correct format, like name@example.com',
        emailNotMatch:
          'Invalid re-typed email address. The new email address and the re-typed new email address should match',
        maxLength: 'Enter a new email address with less than 256 characters',
      },
      otpCode: {
        required: 'Enter the 6-digit code shown in the authenticator app',
      },
    };
  }
}
