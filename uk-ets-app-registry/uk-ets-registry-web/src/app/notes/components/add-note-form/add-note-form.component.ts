import { Component, Input, OnInit, Output, EventEmitter } from '@angular/core';
import {
  ReactiveFormsModule,
  UntypedFormBuilder,
  Validators,
} from '@angular/forms';
import { UkFormComponent } from '@registry-web/shared/form-controls/uk-form.component';
import {
  FormRadioGroupInfo,
  FormRadioOption,
} from '@registry-web/shared/form-controls/uk-radio-input/uk-radio.model';
import { SharedModule } from '@registry-web/shared/shared.module';
import { NotificationType } from '@notifications/notifications-wizard/model';

@Component({
  standalone: true,
  selector: 'app-add-note-form',
  templateUrl: './add-note-form.component.html',
  imports: [ReactiveFormsModule, SharedModule],
})
export class AddNoteFormComponent extends UkFormComponent implements OnInit {
  @Input()
  storedNote: string;

  @Input()
  options: FormRadioOption[];

  @Output()
  handleSubmit = new EventEmitter<string>();

  @Output()
  handleCancel = new EventEmitter<string>();

  formRadioGroupInfo: FormRadioGroupInfo;

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  ngOnInit(): void {
    super.ngOnInit();
  }

  protected doSubmit() {
    this.handleSubmit.emit(this.formGroup.get('noteDescription').value);
  }

  protected getFormModel(): any {
    return {
      noteDescription: [
        this.storedNote,
        {
          updateOn: 'change',
          validators: [Validators.required, Validators.maxLength(1024)],
        },
      ],
    };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      noteDescription: {
        required: 'Add note description',
        maxlength: 'Note description should not exceed 1024 characters',
      },
    };
  }

  onContinue() {
    this.onSubmit();
  }

  onCancel() {
    this.handleCancel.emit(this.formGroup.get('noteDescription').value);
  }

  protected readonly NotificationType = NotificationType;
}
