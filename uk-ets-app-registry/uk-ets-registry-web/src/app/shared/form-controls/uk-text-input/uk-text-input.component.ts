import { Component, OnInit, Input } from '@angular/core';
import { UkFormControlComponent } from '../uk-form-control.component';

export const ENTER_TEXT = 'Enter text';
const MAX_LENGTH = 40;

@Component({
  selector: 'app-uk-text-input',
  templateUrl: './uk-text-input.component.html',
})
export class UkTextInputComponent
  extends UkFormControlComponent
  implements OnInit {
  @Input() maxlength: number;

  ngOnInit() {
    if (!this.formState) {
      this.formState = '';
    }
    if (!this.hint) {
      this.hint = ENTER_TEXT;
    }
    if (!this.maxlength) {
      this.maxlength = MAX_LENGTH;
    }
    super.ngOnInit();
  }
}
