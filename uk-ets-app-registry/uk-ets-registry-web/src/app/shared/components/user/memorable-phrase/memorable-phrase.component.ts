import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { Validators } from '@angular/forms';
import { IUser } from '@shared/user';

@Component({
  selector: 'app-memorable-phrase',
  templateUrl: './memorable-phrase.component.html',
  styleUrls: [],
})
export class MemorablePhraseComponent extends UkFormComponent {
  @Input() caption: string;
  @Input() header: string;
  @Input() isRequestUpdateProcess = false;
  @Input() user: IUser;
  @Output() readonly outputUser = new EventEmitter<IUser>();

  protected getFormModel() {
    return {
      memorablePhrase: [
        '',
        [Validators.required, Validators.pattern('^$|^[A-Za-z0-9 .]+')],
      ],
    };
  }

  protected getValidationMessages(): {
    [key: string]: { [key: string]: string };
  } {
    return {
      memorablePhrase: {
        required: 'Enter a memorable phrase',
        pattern: 'The memorable phrase cannot contain any special characters',
      },
    };
  }

  protected doSubmit() {
    this.outputUser.emit(this.formGroup.value);
  }
}
