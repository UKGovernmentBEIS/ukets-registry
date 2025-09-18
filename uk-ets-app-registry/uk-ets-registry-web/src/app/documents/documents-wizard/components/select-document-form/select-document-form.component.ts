import { CommonModule } from '@angular/common';
import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  Output,
  SimpleChanges,
} from '@angular/core';
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
import { RegistryDocumentCategory } from '@registry-web/documents/models/document.model';
import { ErrorDetail } from '@registry-web/shared/error-summary';
import { FormRadioGroupInfo } from '@registry-web/shared/form-controls/uk-radio-input/uk-radio.model';
import { SharedModule } from '@registry-web/shared/shared.module';
import { UkValidationMessageHandler } from '@registry-web/shared/validation';
import { Subject, takeUntil } from 'rxjs';

@Component({
  standalone: true,
  selector: 'app-select-document-form',
  imports: [CommonModule, ReactiveFormsModule, RouterModule, SharedModule],
  templateUrl: './select-document-form.component.html',
  styleUrls: ['./select-document-form.component.scss'],
})
export class SelectDocumentFormComponent
  implements OnInit, OnDestroy, OnChanges
{
  @Input() categories: RegistryDocumentCategory[];
  @Input() storedCategoryId: number;
  @Input() storedDocumentId: number;
  @Input() storedUpdateType: DocumentUpdateType;
  @Output() readonly selectedDocument = new EventEmitter<{
    id: number;
    categoryId: number;
    order: number;
    title: string;
    name: string;
  }>();
  @Output() readonly errorDetails = new EventEmitter<ErrorDetail[]>();

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['categories'] || changes['storedDocumentId']) {
      this.setSelectedCategory();
    }
  }

  private _unsubscribe = new Subject();

  selectDocumentFormGroup: UntypedFormGroup;

  selectedCategory: RegistryDocumentCategory;

  validationErrorMessage: ValidationErrors = {};
  private validationMessages: { [key: string]: { [key: string]: string } };
  private genericValidator: UkValidationMessageHandler;

  selectDocumentGroupInfo: FormRadioGroupInfo;
  constructor(private formBuilder: UntypedFormBuilder) {}

  ngOnInit() {
    const options = this.categories.map((c) => ({
      label: c.name,
      value: c.id,
      hint: ' ',
      enabled: true,
    }));

    this.selectDocumentGroupInfo = {
      radioGroupHeading: 'Select document',
      radioGroupHeadingCaption: 'Update documents',
      key: 'selectDocument',
      radioGroupHint: 'Select one option',
      subGroups: [
        {
          heading: '',
          options: options,
        },
      ],
    };

    this.selectDocumentFormGroup = this.formBuilder.group(
      {
        selectDocument: [this.storedDocumentId, [Validators.required]],
        categoryId: [this.storedCategoryId],
      },
      { updateOn: 'submit' }
    );

    this.validationMessages = {
      selectDocument: {
        required: 'Choose a document',
      },
    };
    this.genericValidator = new UkValidationMessageHandler(
      this.validationMessages
    );
  }

  selectCategory(category: RegistryDocumentCategory) {
    this.selectedCategory = category;
    this.selectDocumentFormGroup.get('categoryId').setValue(category.id);
    this.selectDocumentFormGroup.get('selectDocument').setValue(null);
    this.selectDocumentFormGroup.updateValueAndValidity();
  }

  onContinue() {
    this.selectDocumentFormGroup.markAllAsTouched();
    const originalDocument = this.categories
      .find(
        (c) =>
          c.id === Number(this.selectDocumentFormGroup.get('categoryId').value)
      )
      ?.documents.find(
        (d) =>
          d.id ===
          Number(this.selectDocumentFormGroup.get('selectDocument').value)
      );

    if (this.selectDocumentFormGroup.valid) {
      this.selectedDocument.emit({
        id: Number(this.selectDocumentFormGroup.get('selectDocument').value),
        categoryId: Number(
          this.selectDocumentFormGroup.get('categoryId').value
        ),
        order: originalDocument?.order,
        title: originalDocument?.title,
        name: originalDocument?.name,
      });
    } else {
      this.validationErrorMessage = this.genericValidator.processMessages(
        this.selectDocumentFormGroup
      );
      this.errorDetails.emit(
        this.genericValidator.mapMessagesToErrorDetails(
          this.validationErrorMessage
        )
      );
    }
  }

  setSelectedCategory() {
    this.selectedCategory = this.categories?.find(
      (c) => c.id === this.storedCategoryId
    );
  }

  getErrors(): string[] {
    return Object.keys(this.validationErrorMessage).map(
      (key) => this.validationErrorMessage[key]
    );
  }

  get showErrors(): boolean {
    return (
      this.selectDocumentFormGroup.invalid &&
      this.selectDocumentFormGroup.touched
    );
  }

  ngOnDestroy(): void {
    this._unsubscribe.next(null);
    this._unsubscribe.complete();
  }
}
