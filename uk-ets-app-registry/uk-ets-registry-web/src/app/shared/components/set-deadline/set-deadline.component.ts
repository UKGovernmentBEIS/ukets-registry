import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  AbstractControl,
  FormControl,
  UntypedFormBuilder,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from '@angular/forms';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { Option } from '@shared/form-controls/uk-select-input/uk-select.model';

@Component({
  selector: 'app-set-deadline',
  templateUrl: './set-deadline.component.html',
})
export class SetDeadlineComponent extends UkFormComponent implements OnInit {
  @Input()
  title: string;
  @Input()
  subtitle: string;
  @Input()
  sectionTitle: string;
  @Input()
  accountName: string;
  @Input()
  accountNumber: string;
  @Input()
  accountHolderName: string;
  @Input()
  recipientName: string;
  @Input()
  deadline: string;
  @Input()
  initialDeadline?: string;

  @Output() readonly setDeadline = new EventEmitter<Date | any>();

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  ngOnInit() {
    super.ngOnInit();
  }

  notInitialValidator(initialValue: string): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const value = control.value;

      if (!value || !initialValue || value !== initialValue) {
        return null;
      }
      return { notInitial: true };
    };
  }

  protected doSubmit() {
    this.setDeadline.emit(this.formGroup.get('deadline').value);
  }

  protected getFormModel(): any {
    return {
      deadline: [
        this.deadline,
        [Validators.required, this.notInitialValidator(this.initialDeadline)],
      ],
    };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      deadline: {
        required: 'Please set a deadline.',
        pattern: 'The date you have entered is invalid.',
        ngbDate: 'Please set a valid deadline. Deadline cannot be in the past.',
        notInitial: 'Cannot set the same deadline.',
      },
    };
  }
}
