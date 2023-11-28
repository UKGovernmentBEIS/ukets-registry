import { Component, Input, OnInit } from '@angular/core';
import {
  ControlContainer,
  UntypedFormControl,
  FormGroupDirective,
} from '@angular/forms';

@Component({
  selector: 'app-uk-activation-code-input',
  templateUrl: './uk-activation-code-input.component.html',
  viewProviders: [
    { provide: ControlContainer, useExisting: FormGroupDirective },
  ],
})
export class UkActivationCodeInputComponent implements OnInit {
  @Input() maxlength: number;
  @Input() titleToDisplay: string;
  @Input() formControlInput1: string;
  @Input() formControlInput2: string;
  @Input() formControlInput3: string;
  @Input() formControlInput4: string;
  @Input() formControlInput5: string;
  @Input() hint: string;
  formControl1: UntypedFormControl;
  formControl2: UntypedFormControl;
  formControl3: UntypedFormControl;
  formControl4: UntypedFormControl;
  formControl5: UntypedFormControl;

  constructor(private parentF: FormGroupDirective) {}

  ngOnInit() {
    this.formControl1 = this.parentF.form.get(
      this.formControlInput1
    ) as UntypedFormControl;
    this.formControl2 = this.parentF.form.get(
      this.formControlInput2
    ) as UntypedFormControl;
    this.formControl3 = this.parentF.form.get(
      this.formControlInput3
    ) as UntypedFormControl;
    this.formControl4 = this.parentF.form.get(
      this.formControlInput4
    ) as UntypedFormControl;
    this.formControl5 = this.parentF.form.get(
      this.formControlInput5
    ) as UntypedFormControl;
  }
}
