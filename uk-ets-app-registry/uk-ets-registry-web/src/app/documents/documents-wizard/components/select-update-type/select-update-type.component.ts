import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  UntypedFormGroup,
  ValidationErrors,
  UntypedFormBuilder,
  Validators,
  ReactiveFormsModule,
} from '@angular/forms';
import { RouterModule } from '@angular/router';
import { DocumentUpdateType } from '@registry-web/documents/models/document-update-type.model';
import { RegistryDocumentCategory } from '@registry-web/documents/models/document.model';
import { ErrorDetail } from '@registry-web/shared/error-summary';
import { FormRadioGroupInfo } from '@registry-web/shared/form-controls/uk-radio-input/uk-radio.model';
import { SharedModule } from '@registry-web/shared/shared.module';
import { UkValidationMessageHandler } from '@registry-web/shared/validation';

@Component({
  standalone: true,
  selector: 'app-select-update-type',
  imports: [CommonModule, ReactiveFormsModule, RouterModule, SharedModule],
  templateUrl: './select-update-type.component.html',
})
export class SelectUpdateTypeComponent implements OnInit {
  @Input() storedUpdateType: DocumentUpdateType;
  @Input() categories: RegistryDocumentCategory[];
  @Output() readonly selectedUpdateType = new EventEmitter<any>();
  @Output() readonly errorDetails = new EventEmitter<ErrorDetail[]>();

  updateTypeFormGroup: UntypedFormGroup;

  validationErrorMessage: ValidationErrors = {};
  private validationMessages: { [key: string]: { [key: string]: string } };
  private genericValidator: UkValidationMessageHandler;

  updateTypeGroupInfo: FormRadioGroupInfo;
  constructor(private formBuilder: UntypedFormBuilder) {}

  ngOnInit() {
    this.updateTypeGroupInfo = {
      radioGroupHeading: 'Select type of update',
      radioGroupHeadingCaption: 'Update documents',
      key: 'updateType',
      radioGroupHint: 'Select one option',
      subGroups: [
        {
          heading: '',
          options: [
            {
              label: 'Add document',
              value: DocumentUpdateType.ADD_DOCUMENT,
              hint: ' ',
              enabled: true,
            },
            {
              label: 'Update document',
              value: DocumentUpdateType.UPDATE_DOCUMENT,
              hint: ' ',
              enabled: true,
            },
            {
              label: 'Delete document',
              value: DocumentUpdateType.DELETE_DOCUMENT,
              hint: '',
              enabled: true,
            },
          ],
        },
        { heading: '', options: [] },
        {
          heading: '',
          options: [
            {
              label: 'Add document category',
              value: DocumentUpdateType.ADD_DOCUMENT_CATEGORY,
              hint: ' ',
              enabled: true,
            },
            {
              label: 'Update document category',
              value: DocumentUpdateType.UPDATE_DOCUMENT_CATEGORY,
              hint: ' ',
              enabled: true,
            },
            {
              label: 'Delete document category',
              value: DocumentUpdateType.DELETE_DOCUMENT_CATEGORY,
              hint: ' ',
              enabled: true,
            },
          ],
        },
      ],
    };

    if (this.categories.length === 0) {
      this.updateTypeGroupInfo.subGroups = [
        {
          heading: '',
          options: [
            {
              label: 'Add document category',
              value: DocumentUpdateType.ADD_DOCUMENT_CATEGORY,
              hint: ' ',
              enabled: true,
            },
          ],
        },
      ];
    }

    this.updateTypeFormGroup = this.formBuilder.group(
      {
        updateType: [this.storedUpdateType, Validators.required],
      },
      { updateOn: 'submit' }
    );

    this.validationMessages = {
      updateType: {
        required: 'Choose an update type',
      },
    };
    this.genericValidator = new UkValidationMessageHandler(
      this.validationMessages
    );
  }

  onContinue() {
    this.updateTypeFormGroup.markAllAsTouched();
    if (this.updateTypeFormGroup.valid) {
      this.selectedUpdateType.emit(
        this.updateTypeFormGroup.get('updateType').value
      );
    } else {
      this.validationErrorMessage = this.genericValidator.processMessages(
        this.updateTypeFormGroup
      );
      this.errorDetails.emit(
        this.genericValidator.mapMessagesToErrorDetails(
          this.validationErrorMessage
        )
      );
    }
  }

  showErrors(): boolean {
    return this.updateTypeFormGroup.invalid && this.updateTypeFormGroup.touched;
  }
}
