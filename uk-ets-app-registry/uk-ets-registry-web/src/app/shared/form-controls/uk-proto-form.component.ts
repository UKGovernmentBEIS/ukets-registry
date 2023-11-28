import { Component, Input, OnInit } from '@angular/core';
import {
  AbstractControl,
  UntypedFormControl,
  FormGroupDirective,
} from '@angular/forms';

// eslint-disable-next-line @angular-eslint/use-component-selector
@Component({ template: `` })
export class UkProtoFormComponent implements OnInit {
  @Input() label: string;
  @Input() hint: string;
  @Input() class: string;
  @Input() style: any;
  @Input() hintClass = 'govuk-hint';

  @Input() readonly: boolean;
  @Input() addFormGroupClass = true;
  @Input() validationErrorMessage: string;

  hintId: string;
  labelId: string;
  _id: string;

  formControl: UntypedFormControl;

  constructor(private parentF: FormGroupDirective) {}

  get id(): string {
    return this._id;
  }

  /*
  the value for nested form controls should be the full path and not the control name in the group
  e.g if the group is address and the control name firstName is should only work with address.firstName
   */
  @Input()
  set controlName(value) {
    this._id = value;
    let controlName;
    if (this._id.includes('.')) {
      /**
       * This is a hack to support links from the error summary to nested form elements.
       * if nested forms so for a nested form control name inside group address the label id should
       * be name-label instead of address.name-label
       */
      controlName = this._id.split('.').pop();
    }
    this.labelId = controlName ? controlName + '-label' : this._id + '-label';
    this.hintId = this._id + '-hint';
  }

  /**
   * A method to check whether a form control has the required validator set
   * @param abstractControl the form control
   * @returns true of false
   */
  hasRequiredField = (abstractControl: AbstractControl): boolean => {
    if (abstractControl.validator) {
      const validator = abstractControl.validator({} as AbstractControl);
      if (validator && validator.required) {
        return true;
      }
    }
    return false;
  };

  /**
   * Show only when showErrors when the parent form is touched and the field contains errors
   */
  showError(): boolean {
    return (
      this.formControl.parent.touched &&
      !!this.formControl.errors &&
      !!this.validationErrorMessage
    );
  }

  ngOnInit(): void {
    // after upgrading to angular 9 this issue appeared:
    // https://github.com/angular/angular/issues/23914
    // so the initialisation of formControl was moved in ngOnInit.
    this.formControl = this.parentF.form.get(this._id) as UntypedFormControl;
  }
}
