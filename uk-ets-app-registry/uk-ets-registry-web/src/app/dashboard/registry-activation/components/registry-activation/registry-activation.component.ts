import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { canGoBack, clearErrors, errors } from '@shared/shared.action';
import {
  UntypedFormBuilder,
  UntypedFormGroup,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { ActivatedRoute } from '@angular/router';
import {
  UkRegistryValidators,
  UkValidationMessageHandler,
} from '@shared/validation';
import { RouterLinks } from '../../model/registry-activation.model';
import { enrolUser } from '../../actions/registry-activation.actions';

@Component({
  selector: 'app-registry-activation',
  templateUrl: './registry-activation.component.html',
})
export class RegistryActivationComponent implements OnInit {
  validationErrorMessage: ValidationErrors = {};
  private validationMessages: { [key: string]: { [key: string]: string } };
  private genericValidator: UkValidationMessageHandler;
  errorDetails: ErrorDetail[];

  formGroup: UntypedFormGroup = this.formBuilder.group(
    {
      activationCodeInput1: ['', Validators.required],
      activationCodeInput2: ['', Validators.required],
      activationCodeInput3: ['', Validators.required],
      activationCodeInput4: ['', Validators.required],
      activationCodeInput5: ['', Validators.required],
    },
    { updateOn: 'submit' }
  );

  readonly routerLinks = RouterLinks;

  constructor(
    private formBuilder: UntypedFormBuilder,
    private route: ActivatedRoute,
    private store: Store
  ) {}

  ngOnInit() {
    this.store.dispatch(clearErrors());
    this.store.dispatch(canGoBack({ goBackRoute: '/dashboard' }));
    this.validationMessages = {
      emptyActivationCode: {
        required: 'Please enter your Registry Activation Code',
      },
    };
    this.genericValidator = new UkValidationMessageHandler(
      this.validationMessages
    );
  }

  enrol() {
    this.store.dispatch(clearErrors());
    this.formGroup.setValidators(
      UkRegistryValidators.registryActivationCodeAllInputsProvided([
        'activationCodeInput1',
        'activationCodeInput2',
        'activationCodeInput3',
        'activationCodeInput4',
        'activationCodeInput5',
      ])
    );
    this.formGroup.updateValueAndValidity();
    this.formGroup.markAsTouched();
    if (this.formGroup.valid) {
      const registryActivationCode =
        this.formGroup.get('activationCodeInput1').value +
        '-' +
        this.formGroup.get('activationCodeInput2').value +
        '-' +
        this.formGroup.get('activationCodeInput3').value +
        '-' +
        this.formGroup.get('activationCodeInput4').value +
        '-' +
        this.formGroup.get('activationCodeInput5').value;
      this.store.dispatch(
        enrolUser({
          enrolmentKey: registryActivationCode,
        })
      );
    } else {
      if (this.formGroup.errors.emptyActivationCode) {
        Object.assign(this.validationErrorMessage, {
          emptyActivationCode: 'Please enter your Registry Activation Code',
        });
      }
      this.errorDetails = this.genericValidator.mapMessagesToErrorDetails(
        this.validationErrorMessage
      );
      this.onError(this.errorDetails);
    }
  }

  showError(): boolean {
    return this.formGroup.invalid && this.formGroup.touched;
  }

  onError(value: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: value,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
