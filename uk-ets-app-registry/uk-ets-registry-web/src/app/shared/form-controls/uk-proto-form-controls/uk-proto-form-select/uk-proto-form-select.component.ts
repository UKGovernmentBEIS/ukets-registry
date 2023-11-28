import { Component, Input, OnInit } from '@angular/core';
import {
  ControlContainer,
  UntypedFormControl,
  FormGroupDirective,
} from '@angular/forms';
import { UkProtoFormComponent } from '../../uk-proto-form.component';
import { SelectableOption } from '../../uk-select-input/uk-select.model';
// https://stackoverflow.com/questions/53692892/wrapping-angular-reactive-form-component-with-validator
// https://github.com/angular/angular/issues/22532  @Injectable in the parent
@Component({
  selector: 'app-form-control-select',
  templateUrl: './uk-proto-form-select.component.html',
  viewProviders: [
    { provide: ControlContainer, useExisting: FormGroupDirective },
  ],
})
export class UkProtoFormSelectComponent
  extends UkProtoFormComponent
  implements OnInit
{
  @Input() options: SelectableOption[];
  @Input() placeHolder: string;
  @Input() isFirstSelected: boolean;

  constructor(public parentf: FormGroupDirective) {
    super(parentf);
  }

  ngOnInit(): void {
    super.ngOnInit();
    if (this.isFirstSelected) {
      (this.parentf.form.get(this.id) as UntypedFormControl).setValue(
        this.options[0].value
      );
    }
    //TODO maybe perform a sanity check
    //if more than one option is selected.
    if (this.options) {
      for (const option of this.options) {
        if (option.selected) {
          (this.parentf.form.get(this.id) as UntypedFormControl).setValue(
            option.value
          );
        }
      }
    }
  }
}
