import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { DocumentsWizardPath } from '@registry-web/documents/models/documents-wizard-path.model';
import { canGoBack } from '@registry-web/shared/shared.action';
import { navigateToCancelDocumentUpdateRequest } from '../../store/documents-wizard.actions';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { SelectCategoryFormComponent } from '../select-category-form/select-category-form.component';
import {
  RegistryDocumentCategory,
  UpdateRegistryDocumentCategoryDTO,
} from '@registry-web/documents/models/document.model';
import { Observable } from 'rxjs';
import { selectCategories } from '@registry-web/documents/store/documents.selectors';
import { selectCategoryId } from '../../store/documents-wizard.selector';
import { SharedModule } from '@registry-web/shared/shared.module';
import * as DocumentsWizardActions from '../../store/documents-wizard.actions';

@Component({
  standalone: true,
  selector: 'app-document-container',
  imports: [
    CommonModule,
    RouterModule,
    SharedModule,
    SelectCategoryFormComponent,
  ],
  template: `<app-select-category-form
      [storedCategoryId]="storedCategoryId$ | async"
      [categories]="categories$ | async"
      (selectedCategory)="onContinue($event)"
    ></app-select-category-form>
    <app-cancel-request-link
      (goToCancelScreen)="onCancel()"
    ></app-cancel-request-link>`,
})
export class AddDocumentContainerComponent implements OnInit {
  constructor(private store: Store, private route: ActivatedRoute) {}

  categories$: Observable<RegistryDocumentCategory[]>;
  storedCategoryId$: Observable<number>;

  ngOnInit() {
    this.store.dispatch(
      canGoBack({
        goBackRoute: `${DocumentsWizardPath.BASE_PATH}`,
        extras: { skipLocationChange: true },
      })
    );

    this.categories$ = this.store.select(selectCategories);
    this.storedCategoryId$ = this.store.select(selectCategoryId);
  }

  onContinue(selectedCategoryId: number) {
    this.store.dispatch(
      DocumentsWizardActions.setCategoryId({ selectedCategoryId })
    );
  }

  onCancel() {
    this.store.dispatch(
      navigateToCancelDocumentUpdateRequest({
        route: this.route.snapshot['_routerState'].url,
      })
    );
  }
}
