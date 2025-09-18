import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  UntypedFormGroup,
  ValidationErrors,
  UntypedFormBuilder,
  Validators,
  ReactiveFormsModule,
  AbstractControl,
  ValidatorFn,
} from '@angular/forms';
import { RouterModule } from '@angular/router';
import { DocumentUpdateType } from '@registry-web/documents/models/document-update-type.model';
import {
  RegistryDocumentCategory,
  UpdateRegistryDocumentCategoryDTO,
} from '@registry-web/documents/models/document.model';
import { ErrorDetail } from '@registry-web/shared/error-summary';
import { FormRadioGroupInfo } from '@registry-web/shared/form-controls/uk-radio-input/uk-radio.model';
import { SharedModule } from '@registry-web/shared/shared.module';
import { UkValidationMessageHandler } from '@registry-web/shared/validation';
import { of } from 'rxjs';

@Component({
  standalone: true,
  selector: 'app-select-category-form',
  imports: [CommonModule, ReactiveFormsModule, RouterModule, SharedModule],
  templateUrl: './select-category-form.component.html',
})
export class SelectCategoryFormComponent implements OnInit {
  @Input() categories: RegistryDocumentCategory[];
  @Input() storedCategoryId: number;
  @Input() storedUpdateType: DocumentUpdateType;
  @Output() readonly selectedCategory = new EventEmitter<number>();
  @Output() readonly errorDetails = new EventEmitter<ErrorDetail[]>();

  selectCategoryFormGroup: UntypedFormGroup;

  validationErrorMessage: ValidationErrors = {};
  private validationMessages: { [key: string]: { [key: string]: string } };
  private genericValidator: UkValidationMessageHandler;

  selectCategoryGroupInfo: FormRadioGroupInfo;
  constructor(private formBuilder: UntypedFormBuilder) {}

  ngOnInit() {
    const options = this.categories.map((c) => ({
      label: c.name,
      value: c.id,
      hint: ' ',
      enabled: true,
    }));

    this.selectCategoryGroupInfo = {
      radioGroupHeading: 'Select category',
      radioGroupHeadingCaption: 'Update documents',
      key: 'selectCategory',
      radioGroupHint: 'Select one option',
      subGroups: [
        {
          heading: '',
          options: options,
        },
      ],
    };

    this.selectCategoryFormGroup = this.formBuilder.group(
      {
        selectCategory: [
          this.storedCategoryId,
          [
            Validators.required,
            DocumentCategoryValidators.deleteCategoryWithDocumentsValidator(
              this.storedUpdateType,
              this.categories
            ),
          ],
        ],
      },
      { updateOn: 'submit' }
    );

    this.validationMessages = {
      selectCategory: {
        required: 'Choose a category',
        deleteWitDocuments:
          'All documents under the category must be removed prior to the deletion of the category',
      },
    };
    this.genericValidator = new UkValidationMessageHandler(
      this.validationMessages
    );
  }

  onContinue() {
    this.selectCategoryFormGroup.markAllAsTouched();
    if (this.selectCategoryFormGroup.valid) {
      this.selectedCategory.emit(
        this.selectCategoryFormGroup.get('selectCategory').value
      );
    } else {
      this.validationErrorMessage = this.genericValidator.processMessages(
        this.selectCategoryFormGroup
      );
      this.errorDetails.emit(
        this.genericValidator.mapMessagesToErrorDetails(
          this.validationErrorMessage
        )
      );
    }
  }

  showErrors(): boolean {
    return (
      this.selectCategoryFormGroup.invalid &&
      this.selectCategoryFormGroup.touched
    );
  }
}

class DocumentCategoryValidators {
  static deleteCategoryWithDocumentsValidator(
    updateType: DocumentUpdateType,
    categories: RegistryDocumentCategory[]
  ): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (updateType === DocumentUpdateType.DELETE_DOCUMENT_CATEGORY) {
        const category = categories?.find((c) => c.id == control.value);
        if (!!category && category?.documents?.length !== 0)
          return { deleteWitDocuments: true };
      }
      return null;
    };
  }
}
