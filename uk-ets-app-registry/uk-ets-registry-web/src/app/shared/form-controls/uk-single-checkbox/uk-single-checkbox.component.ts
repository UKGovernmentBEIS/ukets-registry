import { Component, Input } from '@angular/core';
import { UkProtoFormComponent } from '@shared/form-controls/uk-proto-form.component';
import { ControlContainer, FormGroupDirective } from '@angular/forms';

@Component({
  selector: 'app-uk-single-checkbox',
  templateUrl: './uk-single-checkbox.component.html',
  viewProviders: [
    { provide: ControlContainer, useExisting: FormGroupDirective }
  ]
})
export class UkSingleCheckboxComponent extends UkProtoFormComponent {
  @Input() label: string;
  @Input() key: string;
}
