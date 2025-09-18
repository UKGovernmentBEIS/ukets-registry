import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { DocumentsWizardPath } from '@registry-web/documents/models/documents-wizard-path.model';
import { canGoBack } from '@registry-web/shared/shared.action';
import { navigateToCancelDocumentUpdateRequest } from '../../store/documents-wizard.actions';
import { SharedModule } from '@registry-web/shared/shared.module';
import { SelectDocumentFormComponent } from '../select-document-form/select-document-form.component';
import * as DocumentsWizardActions from '../../store/documents-wizard.actions';
import { RegistryDocumentCategory } from '@registry-web/documents/models/document.model';
import { selectCategories } from '@registry-web/documents/store/documents.selectors';
import { Observable } from 'rxjs';
import {
  selectDocumentId,
  selectCategoryId,
} from '../../store/documents-wizard.selector';

@Component({
  standalone: true,
  selector: 'app-delete-document-container',
  imports: [CommonModule, SharedModule, SelectDocumentFormComponent],
  template: `<app-select-document-form
      [storedDocumentId]="storedDocumentId$ | async"
      [storedCategoryId]="storedCategoryId$ | async"
      [categories]="categories$ | async"
      (selectedDocument)="onContinue($event)"
    ></app-select-document-form>
    <app-cancel-request-link
      (goToCancelScreen)="onCancel()"
    ></app-cancel-request-link>`,
})
export class DeleteDocumentContainerComponent implements OnInit {
  constructor(private store: Store, private route: ActivatedRoute) {}

  categories$: Observable<RegistryDocumentCategory[]>;
  storedDocumentId$: Observable<number>;
  storedCategoryId$: Observable<number>;

  ngOnInit() {
    this.store.dispatch(
      canGoBack({
        goBackRoute: `${DocumentsWizardPath.BASE_PATH}`,
        extras: { skipLocationChange: true },
      })
    );

    this.categories$ = this.store.select(selectCategories);
    this.storedDocumentId$ = this.store.select(selectDocumentId);
    this.storedCategoryId$ = this.store.select(selectCategoryId);
  }

  onContinue(selected: {
    id: number;
    categoryId: number;
    order: number;
    title: string;
  }) {
    this.store.dispatch(DocumentsWizardActions.setDocument(selected));
  }

  onCancel() {
    this.store.dispatch(
      navigateToCancelDocumentUpdateRequest({
        route: this.route.snapshot['_routerState'].url,
      })
    );
  }
}
