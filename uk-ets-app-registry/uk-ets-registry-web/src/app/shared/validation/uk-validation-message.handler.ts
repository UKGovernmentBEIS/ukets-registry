import {
  AbstractControl,
  UntypedFormGroup,
  ValidationErrors,
} from '@angular/forms';
import { ErrorDetail } from '../error-summary/error-detail';

// Generic validator for Reactive forms
// Implemented as a class, not a service, so it can retain state for multiple forms.
export class UkValidationMessageHandler {
  // Provide the set of valid validation messages
  // Stucture:
  // controlName1: {
  //     validationRuleName1: 'Validation Message.',
  //     validationRuleName2: 'Validation Message.'
  // },
  // controlName2: {
  //     validationRuleName1: 'Validation Message.',
  //     validationRuleName2: 'Validation Message.'
  // }
  constructor(
    private validationMessages: { [key: string]: { [key: string]: string } }
  ) {}

  // Processes each control within a FormGroup
  // And returns a set of validation messages to display
  // Structure
  // controlName1: 'Validation Message.',
  // controlName2: 'Validation Message.'
  processMessages(container: UntypedFormGroup): { [key: string]: string } {
    const messages = {};
    for (const controlKey in container.controls) {
      // eslint-disable-next-line no-prototype-builtins
      if (container.controls.hasOwnProperty(controlKey)) {
        const c = container.controls[controlKey];
        // If it is a FormGroup, process its child controls.
        if (c instanceof UntypedFormGroup) {
          if (this.validationMessages[controlKey]) {
            messages[controlKey] = this.mapErrorsToValidationMessages(
              c,
              controlKey
            );
          }
          const childMessages = this.processMessages(c);
          Object.assign(messages, childMessages);
        } else {
          // Only validate if there are validation messages for the control
          if (this.validationMessages[controlKey]) {
            messages[controlKey] = this.mapErrorsToValidationMessages(
              c,
              controlKey
            );
          } else if (this.errorsContainMessages(c.errors)) {
            messages[controlKey] = Object.values(c.errors).join(' ');
          }
        }
      }
    }
    return messages;
  }

  private errorsContainMessages(errors: ValidationErrors) {
    if (errors) {
      return Object.values(errors).every((e) => typeof e === 'string');
    }
    return false;
  }

  private mapErrorsToValidationMessages(
    control: AbstractControl,
    controlKey: string
  ) {
    return control.errors
      ? Object.keys(control.errors)
          // .filter(messageKey => this.validationMessages[controlKey][messageKey])
          .map((messageKey) =>
            this.validationMessages[controlKey][messageKey]
              ? this.validationMessages[controlKey][messageKey]
              : control.errors[messageKey]
          )
          .join(' ')
      : '';
  }

  mapMessagesToErrorDetails(validationErrors: ValidationErrors): ErrorDetail[] {
    const errorDetails: ErrorDetail[] = [];
    Object.keys(validationErrors).forEach((key) => {
      errorDetails.push({
        componentId: key + '-label',
        errorMessage: validationErrors[key],
      });
    });
    return errorDetails;
  }
}
