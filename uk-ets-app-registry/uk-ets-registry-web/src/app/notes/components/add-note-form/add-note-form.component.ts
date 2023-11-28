import { Component, Input, OnInit, Output, EventEmitter } from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { UkFormComponent } from '@registry-web/shared/form-controls/uk-form.component';
import { FormRadioGroupInfo } from '@registry-web/shared/form-controls/uk-radio-input/uk-radio.model';

@Component({
  selector: 'app-add-note-form',
  templateUrl: './add-note-form.component.html',
})
export class AddNoteFormComponent extends UkFormComponent implements OnInit {
  @Input()
  storedNote: string;

  @Output()
  handleSubmit = new EventEmitter<string>();

  @Output()
  handleCancel = new EventEmitter<string>();

  formRadioGroupInfo: FormRadioGroupInfo = {
    radioGroupHeading: 'Select entity',
    radioGroupHeadingCaption: 'Add Note',
    radioGroupHint: 'Select one option',
    key: 'noteDescription',
    options: [
      { label: 'Account', value: 'ACCOUNT', enabled: true },
      { label: 'Account Holder', value: 'ACCOUNT_HOLDER', enabled: true },
    ],
  };

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
          validators: [Validators.required],
        },
      ],
    };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      noteDescription: {
        required: 'Add note description',
      },
    };
  }

  onContinue() {
    this.onSubmit();
  }

  onCancel() {
    this.handleCancel.emit(this.formGroup.get('noteDescription').value);
  }
}
