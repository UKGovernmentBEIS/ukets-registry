import { Component, Input, OnInit, Output, EventEmitter } from '@angular/core';
import {
  ReactiveFormsModule,
  UntypedFormBuilder,
  Validators,
} from '@angular/forms';
import { RouterModule } from '@angular/router';
import { UkFormComponent } from '@registry-web/shared/form-controls/uk-form.component';
import {
  FormRadioGroupInfo,
  FormRadioOption,
} from '@registry-web/shared/form-controls/uk-radio-input/uk-radio.model';
import { NoteType } from '@registry-web/shared/model/note';
import { SharedModule } from '@registry-web/shared/shared.module';

@Component({
  standalone: true,
  imports: [SharedModule, RouterModule, ReactiveFormsModule],
  selector: 'app-select-entity-form',
  templateUrl: './select-entity-form.component.html',
})
export class SelectEntityFormComponent
  extends UkFormComponent
  implements OnInit
{
  @Input()
  options: FormRadioOption[];

  @Input()
  storedType: NoteType;

  @Output()
  handleSubmit = new EventEmitter<NoteType>();

  @Output()
  handleCancel = new EventEmitter<NoteType>();

  formRadioGroupInfo: FormRadioGroupInfo;

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.formRadioGroupInfo = {
      radioGroupHeading: 'Select entity',
      radioGroupHeadingCaption: 'Add Note',
      radioGroupHint: 'Select one option',
      key: 'noteType',
      options: this.options,
    };
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
