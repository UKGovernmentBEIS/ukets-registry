/*
 * Copyright (c) 2019.
 *
 * UK Emission Trading Scheme.
 */

import { Directive, Input } from '@angular/core';
import { FormGroupDirective } from '@angular/forms';

@Directive({ selector: '[appConnectForm]' })
export class ConnectFormDirective {
  @Input('appConnectForm')
  set data(val: any) {
    if (val) {
      this.formGroupDirective.form.patchValue(val);
      this.formGroupDirective.form.markAsPristine();
    }
  }
  constructor(private formGroupDirective: FormGroupDirective) {}
}
