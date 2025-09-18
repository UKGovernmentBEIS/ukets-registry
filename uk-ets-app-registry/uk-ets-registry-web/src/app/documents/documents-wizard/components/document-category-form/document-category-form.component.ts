import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import {
  ReactiveFormsModule,
  UntypedFormBuilder,
  Validators,
} from '@angular/forms';
import { RegistryDocumentCategory } from '@registry-web/documents/models/document.model';
import { UkFormComponent } from '@registry-web/shared/form-controls/uk-form.component';
import {
  FormRadioOption,
  FormRadioGroupInfo,
} from '@registry-web/shared/form-controls/uk-radio-input/uk-radio.model';
import { SharedModule } from '@registry-web/shared/shared.module';

@Component({
  standalone: true,
  selector: 'app-document-category-form',
  templateUrl: './document-category-form.component.html',
  styleUrls: ['./document-category-form.component.scss'],
  imports: [ReactiveFormsModule, SharedModule],
})
export class DocumentCategoryFormComponent
  extends UkFormComponent
  implements OnInit
{
  @Input()
  storedDocumentCategory: RegistryDocumentCategory;

  @Input()
  orderOptions: number[];

  @Input()
  updateType: 'ADD' | 'UPDATE' = 'ADD';

  @Output()
  handleSubmit = new EventEmitter<string>();

  formRadioGroupInfo: FormRadioGroupInfo;

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  ngOnInit(): void {
    super.ngOnInit();
  }

  protected doSubmit() {
    this.handleSubmit.emit(this.formGroup.value);
  }

  protected getFormModel(): any {
    return {
      name: [
        this.storedDocumentCategory?.name,
        {
          updateOn: 'change',
          validators: [Validators.required],
        },
      ],
      order: [
        this.storedDocumentCategory?.order ||
          Math.max.apply(null, this.orderOptions),
        {
          validators: [Validators.required],
        },
      ],
    };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      name: {
        required: 'Add title',
      },
      order: {
        required: 'Select order',
      },
    };
  }

  onContinue() {
    this.onSubmit();
  }
}
