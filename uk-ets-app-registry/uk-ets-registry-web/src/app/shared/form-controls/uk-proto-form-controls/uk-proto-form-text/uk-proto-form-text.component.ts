import { Component, Input } from '@angular/core';
import { ControlContainer, FormGroupDirective } from '@angular/forms';
import { UkProtoFormComponent } from '../../uk-proto-form.component';
// https://stackoverflow.com/questions/53692892/wrapping-angular-reactive-form-component-with-validator
// https://github.com/angular/angular/issues/22532  @Injectable in the parent
@Component({
  selector: 'app-form-control-text[label]',
  templateUrl: './uk-proto-form-text.component.html',
  viewProviders: [
    { provide: ControlContainer, useExisting: FormGroupDirective },
  ],
})
export class UkProtoFormTextComponent extends UkProtoFormComponent {
  @Input() maxlength = 40;
  @Input() showLabel = true;
  @Input() showLabelInBold = false;
  @Input() autocomplete = 'on';
  @Input() disabled = false;
  @Input() showTextAfterInputElement: string;
  @Input() prefix: string;

  onBlur(event): void {
    const val = event.target['value'];
    if (val) {
      const array = val.split('');
      const last = array.length - 1;
      if (array[0].indexOf(' ') === 0 || array[last].indexOf(' ') === 0) {
        const trimmedVal = val.trim();
        this.formControl.setValue(trimmedVal);
      }
    }
  }

  keyDown(event: KeyboardEvent) {
    if (event.keyCode === 13 || event.key === 'Enter') {
      this.onBlur(event);
    }
  }
}
