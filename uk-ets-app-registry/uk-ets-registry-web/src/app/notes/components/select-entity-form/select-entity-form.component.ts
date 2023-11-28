import { Component, Input, OnInit, Output, EventEmitter } from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { UkFormComponent } from '@registry-web/shared/form-controls/uk-form.component';
import { FormRadioGroupInfo } from '@registry-web/shared/form-controls/uk-radio-input/uk-radio.model';
import { NoteType, NoteTypeLabel } from '@registry-web/shared/model/note';

@Component({
  selector: 'app-select-entity-form',
  templateUrl: './select-entity-form.component.html',
})
export class SelectEntityFormComponent
  extends UkFormComponent
  implements OnInit
{
  @Input()
  storedType: NoteType;

  @Output()
  handleSubmit = new EventEmitter<NoteType>();

  @Output()
  handleCancel = new EventEmitter<NoteType>();

  formRadioGroupInfo: FormRadioGroupInfo = {
    radioGroupHeading: 'Select entity',
    radioGroupHeadingCaption: 'Add Note',
    radioGroupHint: 'Select one option',
    key: 'noteType',
    options: [
      {
        label: NoteTypeLabel[NoteType.ACCOUNT],
        value: NoteType.ACCOUNT,
        enabled: true,
      },
      {
        label: NoteTypeLabel[NoteType.ACCOUNT_HOLDER],
        value: NoteType.ACCOUNT_HOLDER,
        enabled: true,
      },
    ],
  };

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  ngOnInit(): void {
    super.ngOnInit();
  }

  protected doSubmit() {
    this.handleSubmit.emit(this.formGroup.get('noteType').value);
  }

  protected getFormModel() {
    return {
      noteType: [
        this.storedType,
        {
          updateOn: 'change',
          validators: [Validators.required],
        },
      ],
    };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      noteType: {
        required: 'Select type of note',
      },
    };
  }

  onContinue() {
    this.onSubmit();
  }

  onCancel() {
    this.handleCancel.emit(this.formGroup.get('noteType').value);
  }
}
