import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { Observable, take } from 'rxjs';
import { ErrorDetail, ErrorSummary } from '@registry-web/shared/error-summary';
import { canGoBack, errors } from '@registry-web/shared/shared.action';
import * as DocumentsWizardActions from '../../store/documents-wizard.actions';
import { SharedModule } from '@registry-web/shared/shared.module';
import { CheckUpdateAndSubmitComponent } from './check-update-and-submit.component';
import {
  RegistryDocumentCategory,
  SaveRegistryDocumentDTO,
  UpdateRegistryDocumentCategoryDTO,
} from '@registry-web/documents/models/document.model';
import { DocumentUpdateType } from '@registry-web/documents/models/document-update-type.model';
import {
  selectDocument,
  selectDocumentCategory,
  selectFileUpdated,
  selectUpdateType,
  selectUploading,
} from '../../store/documents-wizard.selector';
import { DocumentsWizardPath } from '@registry-web/documents/models/documents-wizard-path.model';
import { selectCategories } from '@registry-web/documents/store/documents.selectors';

@Component({
  standalone: true,
  imports: [RouterModule, SharedModule, CheckUpdateAndSubmitComponent],
  selector: 'app-check-update-and-submit-container',
  template: `<app-check-update-and-submit
      [categories]="categories$ | async"
      [storedDocumentCategory]="documentCategory$ | async"
      [storedDocument]="document$ | async"
      [storedUpdateType]="updatedType$ | async"
      [uploading]="uploading$ | async"
      [fileUpdated]="fileUpdated$ | async"
      (submitRequest)="onSubmit($event)"
      (navigateToEmitter)="navigateTo($event)"
    >
    </app-check-update-and-submit>
    <app-cancel-request-link
      (goToCancelScreen)="onCancel()"
    ></app-cancel-request-link>`,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CheckUpdateAndSubmitContainerComponent implements OnInit {
  goBackRoute: string;
  categories$: Observable<RegistryDocumentCategory[]>;
  documentCategory$: Observable<UpdateRegistryDocumentCategoryDTO>;
  document$: Observable<SaveRegistryDocumentDTO>;
  updatedType$: Observable<DocumentUpdateType>;
  uploading$: Observable<boolean>;
  fileUpdated$: Observable<boolean>;

  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.categories$ = this.store.select(selectCategories);
    this.documentCategory$ = this.store.select(selectDocumentCategory);
    this.document$ = this.store.select(selectDocument);
    this.updatedType$ = this.store.select(selectUpdateType);
    this.uploading$ = this.store.select(selectUploading);
    this.fileUpdated$ = this.store.select(selectFileUpdated);

    this.updatedType$.pipe(take(1)).subscribe((updateType) => {
      switch (updateType) {
        case DocumentUpdateType.ADD_DOCUMENT_CATEGORY:
          this.goBackRoute = `${DocumentsWizardPath.BASE_PATH}/${DocumentsWizardPath.ADD_DOCUMENT_CATEGORY}`;
          break;
        case DocumentUpdateType.UPDATE_DOCUMENT_CATEGORY:
          this.goBackRoute = `${DocumentsWizardPath.BASE_PATH}/${DocumentsWizardPath.UPDATE_DOCUMENT_CATEGORY_DETAILS}`;
          break;
        case DocumentUpdateType.DELETE_DOCUMENT_CATEGORY:
          this.goBackRoute = `${DocumentsWizardPath.BASE_PATH}/${DocumentsWizardPath.DELETE_DOCUMENT_CATEGORY}`;
          break;
        case DocumentUpdateType.ADD_DOCUMENT:
          this.goBackRoute = `${DocumentsWizardPath.BASE_PATH}/${DocumentsWizardPath.CHOOSE_DISPLAY_ORDER}`;
          break;
        case DocumentUpdateType.UPDATE_DOCUMENT:
          this.goBackRoute = `${DocumentsWizardPath.BASE_PATH}/${DocumentsWizardPath.CHOOSE_DISPLAY_ORDER}`;
          break;
        case DocumentUpdateType.DELETE_DOCUMENT:
          this.goBackRoute = `${DocumentsWizardPath.BASE_PATH}/${DocumentsWizardPath.DELETE_DOCUMENT}`;
          break;
        default:
          this.goBackRoute = `${DocumentsWizardPath.BASE_PATH}`;
          break;
      }
    });
    this.store.dispatch(
      canGoBack({
        goBackRoute: this.goBackRoute,
        extras: { skipLocationChange: true },
      })
    );
  }

  navigateTo(routePath: string) {
    this.store.dispatch(
      DocumentsWizardActions.navigateTo({
        route: `${DocumentsWizardPath.BASE_PATH}/${routePath}`,
        extras: { skipLocationChange: true },
      })
    );
  }

  onCancel() {
    this.store.dispatch(
      DocumentsWizardActions.navigateToCancelDocumentUpdateRequest({
        route: this.route.snapshot['_routerState'].url,
      })
    );
  }

  onSubmit(updateType: DocumentUpdateType) {
    switch (updateType) {
      case DocumentUpdateType.ADD_DOCUMENT_CATEGORY:
        this.store.dispatch(DocumentsWizardActions.createCategory());
        break;
      case DocumentUpdateType.UPDATE_DOCUMENT_CATEGORY:
        this.store.dispatch(DocumentsWizardActions.updateCategory());
        break;
      case DocumentUpdateType.DELETE_DOCUMENT_CATEGORY:
        this.store.dispatch(DocumentsWizardActions.deleteCategory());
        break;
      case DocumentUpdateType.ADD_DOCUMENT:
        this.store.dispatch(DocumentsWizardActions.createDocument());
        break;
      case DocumentUpdateType.UPDATE_DOCUMENT:
        this.store.dispatch(DocumentsWizardActions.updateDocument());
        break;
      case DocumentUpdateType.DELETE_DOCUMENT:
        this.store.dispatch(DocumentsWizardActions.deleteDocument());
        break;
      default:
        break;
    }
  }

  onError(value: ErrorDetail) {
    const summary: ErrorSummary = {
      errors: [value],
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
