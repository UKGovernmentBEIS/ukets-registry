import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Output,
} from '@angular/core';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { UkRegistryValidators } from '@shared/validation';

@Component({
  selector: 'app-add-publication-year',
  templateUrl: './add-publication-year.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AddPublicationYearComponent extends UkFormComponent {
  @Output() readonly emitter = new EventEmitter<number>();

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  protected doSubmit() {
    this.emitter.emit(this.formGroup.value.addFileYear);
  }

  protected getFormModel(): any {
    return {
      addFileYear: [
        '',
        [UkRegistryValidators.isValidYear(), Validators.required],
      ],
    };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      addFileYear: {
        invalidYear: 'Not a valid year.',
        required: 'Please enter year',
      },
    };
  }
}
