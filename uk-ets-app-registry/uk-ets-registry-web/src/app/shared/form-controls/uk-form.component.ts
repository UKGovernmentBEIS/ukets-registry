import { UkValidationMessageHandler } from '@shared/validation';
import { EventEmitter, OnInit, Output, Directive, inject } from '@angular/core';
import {
  AbstractControl,
  UntypedFormBuilder,
  UntypedFormGroup,
} from '@angular/forms';
import { ErrorDetail } from '@shared/error-summary';
import { injectDestroy } from '../utils/inject-destroy';

/**
 *
 * The empty selector is needed for tests, this issue seems relevant:
 * https://github.com/angular/angular/issues/36427
 *
 */
// eslint-disable-next-line @angular-eslint/directive-selector
@Directive({ selector: '[]' })
// eslint-disable-next-line @angular-eslint/directive-class-suffix
export abstract class UkFormComponent implements OnInit {
  readonly destroy$ = injectDestroy();
  formGroup: UntypedFormGroup;
  genericValidator: UkValidationMessageHandler;
  validationErrorMessage: { [key: string]: string } = {};
  validationMessages: { [key: string]: { [key: string]: string } };
  protected readonly formBuilder = inject(UntypedFormBuilder);
  showErrors: boolean;
  @Output() readonly errorDetails = new EventEmitter<ErrorDetail[]>();

  private static isFormGroup(
    control: AbstractControl
  ): control is UntypedFormGroup {
    return !!(control as UntypedFormGroup).controls;
  }

  /*
   * IMPORTANT: formGroup.valueChanges emits only on submit!
   */
  private initForm(): UntypedFormGroup {
    return (this.formGroup = this.formBuilder.group(this.getFormModel(), {
      updateOn: 'submit',
    }));
  }

  protected abstract getFormModel(): any;

  protected abstract getValidationMessages(): {
    [key: string]: { [key: string]: string };
  };

  protected abstract doSubmit();

  ngOnInit() {
    this.formGroup = this.initForm();
    this.validationMessages = this.getValidationMessages();
    this.genericValidator = new UkValidationMessageHandler(
      this.validationMessages
    );
  }

  markAllAsTouched() {
    this.formGroup.markAllAsTouched();
  }

  /**
   * A helper method to get all validation errors for a form group
   */
  getAllFormGroupValidationErrors(): any | null {
    return this.getAllFormGroupErrors(this.formGroup);
  }

  private getAllFormGroupErrors(control: AbstractControl): any | null {
    if (UkFormComponent.isFormGroup(control)) {
      return Object.entries(control.controls).reduce(
        (acc, [key, childControl]) => {
          const childErrors = this.getAllFormGroupErrors(childControl);
          if (childErrors) {
            acc = { ...acc, [key]: childErrors };
          }
          return acc;
        },
        null
      );
    } else {
      return control.errors;
    }
  }

  processValidationMessages(): { [key: string]: string } {
    return this.genericValidator.processMessages(this.formGroup);
  }

  onSubmit() {
    this.validationMessages = this.getValidationMessages();
    this.genericValidator = new UkValidationMessageHandler(
      this.validationMessages
    );
    this.showErrors = false;
    this.markAllAsTouched();
    if (this.formGroup.valid) {
      this.doSubmit();
    } else {
      this.showErrors = true;
      this.validationErrorMessage = this.processValidationMessages();
      this.errorDetails.emit(
        this.genericValidator.mapMessagesToErrorDetails(
          this.validationErrorMessage
        )
      );
    }
  }
}
