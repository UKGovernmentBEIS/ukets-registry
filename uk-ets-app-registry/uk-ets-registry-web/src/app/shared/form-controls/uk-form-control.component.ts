import { OnInit, Input, Directive, Component } from '@angular/core';
import {
  UntypedFormGroup,
  ValidatorFn,
  AbstractControlOptions,
  AsyncValidatorFn,
  UntypedFormControl,
} from '@angular/forms';

// eslint-disable-next-line @angular-eslint/use-component-selector
@Component({ template: `` })
export class UkFormControlComponent implements OnInit {
  @Input() form: UntypedFormGroup;
  @Input() id: string;
  @Input() label: string;
  @Input() formState: any;
  @Input() validatorOrOpts:
    | ValidatorFn
    | ValidatorFn[]
    | AbstractControlOptions
    | null;
  @Input() asyncValidator: AsyncValidatorFn | AsyncValidatorFn[] | null;
  @Input() hint: string;

  formControl: UntypedFormControl;
  hintId: string;

  ngOnInit() {
    this.hintId = this.id + '-hint';
    this.formControl = new UntypedFormControl(
      this.formState,
      this.validatorOrOpts,
      this.asyncValidator
    );
    if (this.form) {
      this.form.addControl(this.id, this.formControl);
    }
  }
}
