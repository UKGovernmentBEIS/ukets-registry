import { Component, OnInit, Input } from '@angular/core';
import { Option } from './uk-select.model';
import { UkFormControlComponent } from '../uk-form-control.component';

export const SELECT_FROM_LIST = 'Select from list';

@Component({
  selector: 'app-uk-select-input',
  templateUrl: './uk-select-input.component.html'
})
export class UkSelectInputComponent extends UkFormControlComponent
  implements OnInit {
  @Input() options: Option[];

  ngOnInit() {
    super.ngOnInit();
    if (!this.hint) {
      this.hint = SELECT_FROM_LIST;
    }
  }
}
