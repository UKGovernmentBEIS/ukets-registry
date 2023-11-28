import { Component, Input } from '@angular/core';
import { ControlContainer, FormGroupDirective } from '@angular/forms';
import { UkProtoFormComponent } from '../../uk-proto-form.component';
// https://stackoverflow.com/questions/53692892/wrapping-angular-reactive-form-component-with-validator
// https://github.com/angular/angular/issues/22532  @Injectable in the parent
@Component({
  selector: 'app-form-control-email',
  templateUrl: './uk-proto-form-email.component.html',
  viewProviders: [
    { provide: ControlContainer, useExisting: FormGroupDirective },
  ],
})
export class UkProtoFormEmailComponent extends UkProtoFormComponent {
  @Input() maxlength = 40;

  @Input() disableCopy: boolean;

  isCopyDisabled($event: ClipboardEvent) {
    if (this.disableCopy) {
      $event.preventDefault();
    }
  }
}
