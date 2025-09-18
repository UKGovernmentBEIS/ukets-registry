import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { DocumentsWizardPath } from '@registry-web/documents/models/documents-wizard-path.model';
import { canGoBack } from '@registry-web/shared/shared.action';
import { SharedModule } from '@registry-web/shared/shared.module';
import {
  navigateToCancelDocumentUpdateRequest,
  patchDocumentCategory,
} from '../../store/documents-wizard.actions';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { DocumentCategoryFormComponent } from '../document-category-form/document-category-form.component';
import { Observable } from 'rxjs';
import {
  selectCategoryOrderOptions,
  selectDocumentCategory,
} from '../../store/documents-wizard.selector';
import { UpdateRegistryDocumentCategoryDTO } from '@registry-web/documents/models/document.model';

@Component({
  standalone: true,
  selector: 'app-document-category-container',
  imports: [
    CommonModule,
    RouterModule,
    SharedModule,
    DocumentCategoryFormComponent,
  ],
  template: `<app-document-category-form
      updateType="UPDATE"
      (handleSubmit)="onContinue($event)"
      [storedDocumentCategory]="selectDocumentCategory$ | async"
      [orderOptions]="orderOptions$ | async"
    ></app-document-category-form>
    <app-cancel-request-link
      (goToCancelScreen)="onCancel()"
    ></app-cancel-request-link>`,
})
export class UpdateDocumentCategoryDetailsContainerComponent implements OnInit {
  constructor(private store: Store, private route: ActivatedRoute) {}

  selectDocumentCategory$: Observable<UpdateRegistryDocumentCategoryDTO>;
  orderOptions$: Observable<number[]>;

  ngOnInit() {
    this.store.dispatch(
      canGoBack({
        goBackRoute: `${DocumentsWizardPath.BASE_PATH}/${DocumentsWizardPath.UPDATE_DOCUMENT_CATEGORY}`,
        extras: { skipLocationChange: true },
      })
    );

    this.orderOptions$ = this.store.select(selectCategoryOrderOptions);
    this.selectDocumentCategory$ = this.store.select(selectDocumentCategory);
  }

  onContinue(documentCategory: UpdateRegistryDocumentCategoryDTO) {
    this.store.dispatch(patchDocumentCategory({ documentCategory }));
  }

  onCancel() {
    this.store.dispatch(
      navigateToCancelDocumentUpdateRequest({
        route: this.route.snapshot['_routerState'].url,
      })
    );
  }
}
