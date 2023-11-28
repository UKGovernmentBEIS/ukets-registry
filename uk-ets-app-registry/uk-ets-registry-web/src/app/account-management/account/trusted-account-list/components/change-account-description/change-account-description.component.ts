import {
  AfterViewInit,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
} from '@angular/core';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { DescriptionUpdateActionState } from '@account-management/account/trusted-account-list/reducers/trusted-account-list.reducer';

@Component({
  selector: 'app-change-account-description',
  templateUrl: './change-account-description.component.html',
})
export class ChangeAccountDescriptionComponent
  extends UkFormComponent
  implements OnInit, AfterViewInit
{
  @Input() accountId: string;
  @Input() accountFullIdentifier: string;
  @Input() descriptionValue: DescriptionUpdateActionState;
  @Input() existingDescription: string;
  @Output()
  readonly updateDescription = new EventEmitter<DescriptionUpdateActionState>();
  @Output() readonly cancelAccountChangeAction = new EventEmitter();

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  ngOnInit(): void {
    super.ngOnInit();
  }

  ngAfterViewInit(): void {
    this.formGroup
      .get('description')
      .patchValue(
        this.descriptionValue?.description
          ? this.descriptionValue?.description
          : this.existingDescription
      );
  }

  protected getFormModel() {
    return {
      description: [
        '',
        {
          validators: [Validators.required, Validators.minLength(3)],
          updateOn: 'change',
        },
      ],
    };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      description: {
        minlength: 'Enter at least 3 characters in the "Description"',
        required: 'Description is mandatory',
      },
    };
  }

  protected doSubmit() {
    this.updateDescription.emit({
      description: this.formGroup.value['description'],
      accountFullIdentifier: this.accountFullIdentifier,
    });
  }

  onContinue() {
    this.formGroup.markAllAsTouched();
    this.formGroup.updateValueAndValidity();
    if (this.formGroup.valid) {
      this.doSubmit();
    } else {
      this.generateValidationErrorMessage();
      this.errorDetails.emit(
        this.genericValidator.mapMessagesToErrorDetails(
          this.validationErrorMessage
        )
      );
    }
  }

  onCancel() {
    this.cancelAccountChangeAction.emit();
  }

  private generateValidationErrorMessage(): void {
    if (this.formGroup.get('description').errors?.required) {
      this.validationErrorMessage = {
        description: this.getValidationMessages().description.required,
      };
    }
    if (this.formGroup.get('description').errors?.minlength) {
      this.validationErrorMessage = {
        description: this.getValidationMessages().description.minlength,
      };
    }
  }
}
