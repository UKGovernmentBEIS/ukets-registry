import {
  Component,
  ChangeDetectionStrategy,
  OnInit,
  Input,
  Output,
  EventEmitter,
} from '@angular/core';
import { RouterModule } from '@angular/router';
import {
  DocumentUpdateType,
  DocumentUpdateTypeLabel,
} from '@registry-web/documents/models/document-update-type.model';
import {
  RegistryDocumentCategory,
  SaveRegistryDocumentDTO,
  UpdateRegistryDocumentCategoryDTO,
  UpdateRegistryDocumentDTO,
} from '@registry-web/documents/models/document.model';
import { DocumentsWizardPath } from '@registry-web/documents/models/documents-wizard-path.model';
import { SharedModule } from '@registry-web/shared/shared.module';
import { SummaryListItem } from '@registry-web/shared/summary-list/summary-list.info';

@Component({
  standalone: true,
  imports: [RouterModule, SharedModule],
  selector: 'app-check-update-and-submit',
  templateUrl: './check-update-and-submit.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CheckUpdateAndSubmitComponent {
  @Input() categories: RegistryDocumentCategory[];
  @Input() storedDocument: SaveRegistryDocumentDTO;
  @Input() storedDocumentCategory: UpdateRegistryDocumentCategoryDTO;
  @Input() storedUpdateType: DocumentUpdateType;
  @Input() uploading: boolean;
  @Input() fileUpdated: boolean;

  @Output() readonly navigateToEmitter = new EventEmitter<string>();
  @Output() readonly submitRequest = new EventEmitter<any>();

  navigateTo(value: string) {
    this.navigateToEmitter.emit(value);
  }

  get typeSummaryList(): SummaryListItem[] {
    const summaryListItems = [
      {
        key: {
          label: 'Type of update',
          class: 'govuk-heading-m',
        },
        value: [
          {
            label: '',
          },
        ],
        action: {
          label: 'Change',
          visible: true,
          visuallyHidden: '',
          clickEvent: '',
        },
      },
      {
        key: {
          label: DocumentUpdateTypeLabel[this.storedUpdateType],
          class: 'font-weight-normal',
        },
        value: [
          {
            label: '',
          },
          {
            label: '',
          },
        ],
      },
    ];
    return summaryListItems;
  }

  get valuesSummaryList(): SummaryListItem[] {
    let summaryListItems = [];

    switch (this.storedUpdateType) {
      case DocumentUpdateType.ADD_DOCUMENT_CATEGORY:
        summaryListItems = this.createAddDocumentCategorySummarylistItems();
        break;
      case DocumentUpdateType.UPDATE_DOCUMENT_CATEGORY:
        summaryListItems = this.createUpdateDocumentCategorySummarylistItems();
        break;
      case DocumentUpdateType.DELETE_DOCUMENT_CATEGORY:
        summaryListItems = this.createDeleteDocumentCategorySummarylistItems();
        break;
      case DocumentUpdateType.ADD_DOCUMENT:
        summaryListItems = this.createAddDocumentSummarylistItems();
        break;
      case DocumentUpdateType.UPDATE_DOCUMENT:
        summaryListItems = this.createUpdateDocumentSummarylistItems();
        break;
      case DocumentUpdateType.DELETE_DOCUMENT:
        summaryListItems = this.createDeleteDocumentSummarylistItems();
        break;
      default:
        break;
    }

    return summaryListItems;
  }

  private createAddDocumentCategorySummarylistItems() {
    return [
      {
        key: {
          label: 'Category',
          class: 'govuk-heading-m',
        },
        value: [
          {
            label: '',
          },
        ],
        action: {
          label: 'Change',
          visible: true,
          visuallyHidden: '',
          clickEvent: DocumentsWizardPath.ADD_DOCUMENT_CATEGORY,
        },
      },
      {
        key: { label: 'Title' },
        value: [
          {
            label: 'Display order',
            class: 'govuk-summary-list__key',
          },
          {
            label: '',
            class: '',
          },
        ],
      },
      {
        key: {
          label: this.storedDocumentCategory?.name,
          class: 'font-weight-normal',
        },
        value: [
          {
            label: String(this.storedDocumentCategory?.order),
            class: 'font-weight-normal',
          },
          {
            label: '',
            class: '',
          },
        ],
      },
    ];
  }

  private createUpdateDocumentSummarylistItems() {
    const originalCategory = this.categories.find(
      (c) => c.id === this.storedDocument.categoryId
    );

    const originalDocument = originalCategory.documents.find(
      (d) => d.id === this.storedDocument.id
    );

    return [
      {
        key: {
          label: 'Document',
          class: 'govuk-heading-m',
        },
        value: [
          {
            label: '',
          },
        ],
        action: {
          label: 'Change',
          visible: true,
          visuallyHidden: '',
          clickEvent: DocumentsWizardPath.UPDATE_DOCUMENT,
        },
      },
      {
        key: { label: 'Field' },
        value: [
          {
            label: 'Current value',
            class: 'govuk-summary-list__key',
          },
          {
            label: 'Changed value',
            class: 'govuk-summary-list__key',
          },
        ],
      },
      {
        key: {
          label: 'File',
        },
        value: [
          {
            label: originalDocument.name,
            class: '',
          },
          {
            label: this.fileUpdated ? this.storedDocument.file?.name : null,
            class: this.fileUpdated ? 'focused-text' : '',
          },
        ],
      },
      {
        key: {
          label: 'Document title',
        },
        value: [
          {
            label: originalDocument.title,
            class: '',
          },
          {
            label:
              this.storedDocument.title !== originalDocument.title
                ? this.storedDocument.title
                : null,
            class:
              this.storedDocument.title !== originalDocument.title
                ? 'focused-text'
                : '',
          },
        ],
      },
      {
        key: {
          label: 'Display order',
        },
        value: [
          {
            label: originalDocument.order,
            class: '',
          },
          {
            label:
              originalDocument.order != this.storedDocument.order
                ? this.storedDocument.order
                : null,
            class:
              originalDocument.order != this.storedDocument.order
                ? 'focused-text'
                : '',
          },
        ],
      },
    ];
  }

  private createUpdateDocumentCategorySummarylistItems() {
    const originalCategory = this.categories.find(
      (c) => c.id === this.storedDocumentCategory.id
    );
    return [
      {
        key: {
          label: 'Category',
          class: 'govuk-heading-m',
        },
        value: [
          {
            label: '',
          },
        ],
        action: {
          label: 'Change',
          visible: true,
          visuallyHidden: '',
          clickEvent: DocumentsWizardPath.UPDATE_DOCUMENT_CATEGORY_DETAILS,
        },
      },
      {
        key: { label: 'Field' },
        value: [
          {
            label: 'Current value',
            class: 'govuk-summary-list__key',
          },
          {
            label: 'Changed value',
            class: 'govuk-summary-list__key',
          },
        ],
      },
      {
        key: {
          label: 'Title',
        },
        value: [
          {
            label: originalCategory.name,
            class: '',
          },
          {
            label:
              originalCategory.name !== this.storedDocumentCategory.name
                ? this.storedDocumentCategory.name
                : null,
            class:
              originalCategory.name !== this.storedDocumentCategory.name
                ? 'focused-text'
                : '',
          },
        ],
      },
      {
        key: {
          label: 'Order',
        },
        value: [
          {
            label: originalCategory.order,
            class: '',
          },
          {
            label:
              originalCategory.order != this.storedDocumentCategory.order
                ? this.storedDocumentCategory.order
                : null,
            class:
              originalCategory.order != this.storedDocumentCategory.order
                ? 'focused-text'
                : '',
          },
        ],
      },
    ];
  }

  private createAddDocumentSummarylistItems() {
    return [
      {
        key: {
          label: 'Document',
          class: 'govuk-heading-m',
        },
        value: [
          {
            label: '',
          },
          {
            label: '',
          },
        ],
        action: {
          label: 'Change',
          visible: true,
          visuallyHidden: '',
          clickEvent: DocumentsWizardPath.ADD_DOCUMENT,
        },
      },
      {
        key: { label: 'Category' },
        value: [
          {
            label: 'File',
            class: 'govuk-summary-list__key',
          },
          {
            label: 'Document title',
            class: 'govuk-summary-list__key',
          },
          {
            label: 'Display order',
            class: 'govuk-summary-list__key',
          },
        ],
      },
      {
        key: {
          label: this.displayCategoryName(this.storedDocument.categoryId),
          class: 'font-weight-normal',
        },
        value: [
          {
            label: (this.storedDocument.file as File)?.name,
            class: 'font-weight-normal',
          },
          {
            label: this.storedDocument?.title,
            class: 'font-weight-normal',
          },
          {
            label: String(this.storedDocument?.order),
            class: 'font-weight-normal',
          },
        ],
      },
    ];
  }

  private createDeleteDocumentCategorySummarylistItems() {
    return [
      {
        key: {
          label: 'Category',
          class: 'govuk-heading-m',
        },
        value: [
          {
            label: '',
          },
        ],
        action: {
          label: 'Change',
          visible: true,
          visuallyHidden: '',
          clickEvent: DocumentsWizardPath.DELETE_DOCUMENT_CATEGORY,
        },
      },
      {
        key: {
          label: 'You will delete the following document category',
          class: 'width-100 font-weight-normal no-border display-block',
        },
        value: [],
      },
      {
        key: { label: 'Title' },
        value: [
          {
            label: 'Display order',
            class: 'govuk-summary-list__key',
          },
          {
            label: '',
            class: '',
          },
        ],
      },
      {
        key: {
          label: this.storedDocumentCategory?.name,
          class: 'font-weight-normal',
        },
        value: [
          {
            label: String(this.storedDocumentCategory?.order),
            class: 'font-weight-normal',
          },
          {
            label: '',
            class: '',
          },
        ],
      },
    ];
  }

  private createDeleteDocumentSummarylistItems() {
    const category = this.categories.find(
      (c) => c.id === this.storedDocument.categoryId
    );
    const document = category.documents.find(
      (d) => d.id === this.storedDocument.id
    );
    return [
      {
        key: {
          label: 'Document',
          class: 'govuk-heading-m',
        },
        value: [
          {
            label: '',
          },
          {
            label: '',
          },
        ],
        action: {
          label: 'Change',
          visible: true,
          visuallyHidden: '',
          clickEvent: DocumentsWizardPath.DELETE_DOCUMENT,
        },
      },
      {
        key: {
          label: 'You will delete the following document',
          class: 'width-100 font-weight-normal no-border display-block',
        },
        value: [],
      },
      {
        key: { label: 'Category' },
        value: [
          {
            label: 'File',
            class: 'govuk-summary-list__key',
          },
          {
            label: 'Document title',
            class: 'govuk-summary-list__key',
          },
          {
            label: 'Display order',
            class: 'govuk-summary-list__key',
          },
        ],
      },
      {
        key: {
          label: category.name,
          class: 'font-weight-normal',
        },
        value: [
          {
            label: document.name,
            class: 'font-weight-normal',
          },
          {
            label: document.title,
            class: 'font-weight-normal',
          },
          {
            label: document.order,
            class: 'font-weight-normal',
          },
        ],
      },
    ];
  }

  private displayCategoryName(categoryId: number) {
    return this.categories?.find((c) => c.id === categoryId)?.name;
  }

  submitChanges() {
    this.submitRequest.emit(this.storedUpdateType);
  }
}
