import { Component, Input } from '@angular/core';
import { ControlContainer, FormGroupDirective } from '@angular/forms';
import { UkProtoFormComponent } from '../../uk-proto-form.component';

@Component({
  selector: 'app-form-control-textarea[label]',
  templateUrl: './uk-proto-form-textarea.component.html',
  viewProviders: [
    { provide: ControlContainer, useExisting: FormGroupDirective },
  ],
})
export class UkProtoFormTextareaComponent extends UkProtoFormComponent {
  @Input() maxlength = 40;
  @Input() showLabel = true;
  @Input() rows = 4;
}
