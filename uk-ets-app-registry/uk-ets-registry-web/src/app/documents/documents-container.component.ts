import { Component, OnInit } from '@angular/core';
import { DocumentsComponent } from './documents.component';
import { Store } from '@ngrx/store';
import * as DocumentsActions from './store/documents.actions';
import { Observable } from 'rxjs';
import { RegistryDocumentCategory } from './models/document.model';
import { selectCategories } from './store/documents.selectors';
import { CommonModule } from '@angular/common';
import { clearGoBackRoute } from '@registry-web/shared/shared.action';
import { isSeniorAdmin } from '@registry-web/auth/auth.selector';

@Component({
  standalone: true,
  selector: 'app-documents-container',
  imports: [DocumentsComponent, CommonModule],
  template: `<app-documents
    [categories]="categories$ | async"
    [isSeniorAdmin]="isSeniorAdmin$ | async"
    (downloadDocumentClick)="downloadDocument($event)"
    (updateDocumentsWizardClick)="goToUpdateDocumetsWizard()"
  ></app-documents>`,
})
export class DocumentsContainerComponent implements OnInit {
  constructor(private store: Store) {}

  categories$: Observable<RegistryDocumentCategory[]>;
  isSeniorAdmin$: Observable<boolean>;

  ngOnInit() {
    this.store.dispatch(clearGoBackRoute());
    this.categories$ = this.store.select(selectCategories);
    this.isSeniorAdmin$ = this.store.select(isSeniorAdmin);
  }

  goToUpdateDocumetsWizard() {
    this.store.dispatch(DocumentsActions.navigateToUpdateDocumentsWizard());
  }

  downloadDocument(documentId: number) {
    this.store.dispatch(DocumentsActions.fetchDocumentFile({ documentId }));
  }
}
