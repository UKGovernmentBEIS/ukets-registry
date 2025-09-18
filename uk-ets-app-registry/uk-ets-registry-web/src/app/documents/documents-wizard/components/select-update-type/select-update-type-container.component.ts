import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { SelectUpdateTypeComponent } from './select-update-type.component';
import { DocumentUpdateType } from '@registry-web/documents/models/document-update-type.model';
import * as DocumentsWizardActions from '../../store/documents-wizard.actions';
import { canGoBack, clearErrors } from '@registry-web/shared/shared.action';
import { Observable } from 'rxjs';
import { selectUpdateType } from '../../store/documents-wizard.selector';
import { RegistryDocumentCategory } from '@registry-web/documents/models/document.model';
import { selectCategories } from '@registry-web/documents/store/documents.selectors';

@Component({
  standalone: true,
  selector: 'app-select-update-type-container',
  imports: [CommonModule, SelectUpdateTypeComponent],
  template: `<app-select-update-type
    [storedUpdateType]="updateType$ | async"
    [categories]="categories$ | async"
    (selectedUpdateType)="onContinue($event)"
  ></app-select-update-type>`,
})
export class SelectUpdateTypeContainerComponent implements OnInit {
  constructor(private store: Store) {}

  updateType$: Observable<DocumentUpdateType>;
  categories$: Observable<RegistryDocumentCategory[]>;

  ngOnInit() {
    this.clearErrors();
    this.store.dispatch(
      canGoBack({
        goBackRoute: '/documents',
      })
    );

    this.updateType$ = this.store.select(selectUpdateType);
    this.categories$ = this.store.select(selectCategories);
  }

  private clearErrors() {
    this.store.dispatch(clearErrors());
  }

  onContinue(selectedUpdateType: DocumentUpdateType) {
    this.store.dispatch(
      DocumentsWizardActions.setUpdateType({ selectedUpdateType })
    );
  }
}
