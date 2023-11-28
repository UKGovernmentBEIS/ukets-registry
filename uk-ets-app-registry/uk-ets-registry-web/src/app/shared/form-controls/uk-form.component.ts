import { UkValidationMessageHandler } from '@shared/validation';
import { EventEmitter, OnInit, Output, Directive } from '@angular/core';
import {
  AbstractControl,
  UntypedFormBuilder,
  UntypedFormGroup,
} from '@angular/forms';
import { ErrorDetail } from '@shared/error-summary';

/*
The child class should always have a constructor with a form builder injection?
 */

/**
 * TODO is there a better way to do this?
 * This is needed since Angular 9:
 * https://angular.io/guide/migration-undecorated-classes
 * The previous implementation with:
 * "@Component({ template: `` })"
 * caused strange errors like:
 * "Uncaught (in promise): Error: The pipe 'json' could not be found!"
 *
 * The use of the Directive annotation seems to be  recommended anyway.
 *
 * The empty selector is needed for tests, this issue seems relevant:
 * https://github.com/angular/angular/issues/36427
 *
 */
// eslint-disable-next-line @angular-eslint/directive-selector
@Directive({ selector: '[]' })
// eslint-disable-next-line @angular-eslint/directive-class-suffix
export abstract class UkFormComponent implements OnInit {
  formGroup: UntypedFormGroup;
  genericValidator: UkValidationMessageHandler;
  validationErrorMessage: { [key: string]: string } = {};
  validationMessages: { [key: string]: { [key: string]: string } };
  protected formBuilder: UntypedFormBuilder;
  showErrors: boolean;
  @Output() readonly errorDetails = new EventEmitter<ErrorDetail[]>();

  private static isFormGroup(
    control: AbstractControl
  ): control is UntypedFormGroup {
    return !!(control as UntypedFormGroup).controls;
  }

  private initForm(): UntypedFormGroup {
    if (!this.formBuilder) {
      console.error(
        'Did you forget to create a constructor and inject the angular formbuilder in your form component?'
      );
    }
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
