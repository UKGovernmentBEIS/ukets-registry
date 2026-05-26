import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { ExistingEmitterIdAsyncValidator } from '@shared/validation';
import { ErrorDetail } from '@shared/error-summary';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { take } from 'rxjs';

/**
 * Component for setting the emitter ID during account transfer.
 * This component allows users to enter an emitter ID and validates it against existing IDs.
 * It emits the selected emitter ID and any validation errors to the parent component.
 */
@Component({
  selector: 'app-set-emitter-id',
  templateUrl: './set-emitter-id.component.html',
})
export class SetEmitterIdComponent extends UkFormComponent implements OnInit {
  @Input()
  compliantEntityIdentifier!: number;
  @Input()
  emitterId!: string;

  @Output()
  readonly selectedEmitterId = new EventEmitter<{
    selectedEmitterId: string;
  }>();
  @Output()
  readonly errorDetails = new EventEmitter<ErrorDetail[]>();

  /**
   * Initializes the component and sets up the form model and validation messages.
   * @param formBuilder
   * @param existingEmitterIdAsyncValidator
   */
  constructor(
    protected formBuilder: UntypedFormBuilder,
    private existingEmitterIdAsyncValidator: ExistingEmitterIdAsyncValidator
  ) {
    super();
  }

  /**
   * Initializes the component and sets up the form model and validation messages by calling the base class's ngOnInit method.
   */
  ngOnInit(): void {
    super.ngOnInit();
  }

  /**
   * Emits the selected emitter ID when the form is submitted.
   * The emitter ID is retrieved from the form control and emitted as part of an object with the key 'selectedEmitterId'.
   */
  protected doSubmit(): void {
    this.selectedEmitterId.emit({
      selectedEmitterId: this.formGroup.get('emitterId')!.value,
    });
  }

  /**
   * Creates the form model for the component, including the emitterId control with its validators.
   * @returns
   */
  protected getFormModel(): any {
    return {
      emitterId: [
        this.emitterId,
        [Validators.required],
        [
          this.existingEmitterIdAsyncValidator.validateEmitterId(
            this.compliantEntityIdentifier
          ),
        ],
      ],
    };
  }

  /**
   * Returns the validation messages for the form controls.
   * @returns the validation message(s).
   */
  protected getValidationMessages(): { [p: string]: { [e: string]: string } } {
    return {
      emitterId: {
        required: 'Please enter the Emitter ID.',
        exists: 'This emitter ID is used by another account',
        serverError:
          'Unable to validate the emitter ID. Please try again later.',
      },
    };
  }

  /**
   * Override to handle async validation before calling the base onSubmit logic.
   * This ensures that we wait for the async validation to complete and update the form's validity before proceeding.
   */
  onSubmit(): void {
    if (this.formGroup.pending) {
      this.formGroup.statusChanges.pipe(take(1)).subscribe(() => {
        super.onSubmit();
      });
    } else {
      super.onSubmit();
    }
  }

  /**
   * Accessibility requirement: Set the page title to "Add the emitter ID" when this component is active.
   * @returns the page title
   */
  getTitle(): string {
    return `Add the emitter ID`;
  }
}
