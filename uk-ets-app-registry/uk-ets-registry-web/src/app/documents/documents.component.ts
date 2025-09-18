import { Component, EventEmitter, Input, Output } from '@angular/core';
import { RegistryDocumentCategory } from './models/document.model';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { SharedModule } from '@registry-web/shared/shared.module';

@Component({
  standalone: true,
  imports: [CommonModule, RouterModule, SharedModule],
  selector: 'app-documents',
  templateUrl: './documents.component.html',
  styleUrls: ['./documents.component.scss'],
})
export class DocumentsComponent {
  @Input()
  isSeniorAdmin: boolean;

  protected showMore: { [key: number]: boolean } = {};
  protected MAX_DOCS_SHOWN: number = 5;
  private _categories: RegistryDocumentCategory[];

  @Input()
  set categories(value: RegistryDocumentCategory[]) {
    this._categories = value;
    this.categories.forEach((c) => {
      this.showMore[c.id] = false;
    });
  }

  get categories() {
    return this._categories;
  }

  @Output()
  updateDocumentsWizardClick = new EventEmitter();

  @Output()
  downloadDocumentClick = new EventEmitter();

  updateDocumentsWizard() {
    this.updateDocumentsWizardClick.emit();
  }

  downloadDocument(documentId: number) {
    this.downloadDocumentClick.emit(documentId);
  }

  toggleShowMore(categoryId: number) {
    this.showMore[categoryId] = !this.showMore[categoryId];
  }
}
