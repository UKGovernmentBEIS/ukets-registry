import { LoginGuard } from '@registry-web/shared/guards';
import { DocumentsWizardPath } from '../models/documents-wizard-path.model';
import { AddDocumentContainerComponent } from './components/add-document/add-document-container.component';
import { AddDocumentCategoryContainerComponent } from './components/add-document-category/add-document-category-container.component';
import { UpdateDocumentContainerComponent } from './components/update-document/update-document-container.component';
import { UpdateDocumentCategoryContainerComponent } from './components/update-document-category/update-document-category-container.component';
import { DeleteDocumentContainerComponent } from './components/delete-document/delete-document-container.component';
import { DeleteDocumentCategoryContainerComponent } from './components/delete-document-category/delete-document-category-container';
import { SelectUpdateTypeContainerComponent } from './components/select-update-type/select-update-type-container.component';
import { CancelDocumentUpdateContainerComponent } from './components/cancel-document-update/cancel-document-update-container.component';
import { CheckUpdateAndSubmitContainerComponent } from './components/check-update-and-submit/check-update-and-submit-container.component';
import { DocumentUpdateSuccessContainerComponent } from './components/document-update-success/document-update-success-container.component';
import { DocumentsWizardGuard } from './providers/documents-wizard.guard';
import { UploadFileContainerComponent } from './components/upload-file/upload-file-container.component';
import { ChooseDisplayOrderContainerComponent } from './components/choose-display-order/choose-display-order-container.component';
import { UpdateDocumentCategoryDetailsContainerComponent } from './components/update-document-category-details/update-document-category-details-container.component';

export const DOCUMENTS_WIZARD_ROUTES = [
  {
    path: '',
    canActivate: [LoginGuard],
    canDeactivate: [DocumentsWizardGuard],
    component: SelectUpdateTypeContainerComponent,
  },
  {
    path: DocumentsWizardPath.ADD_DOCUMENT,
    canActivate: [LoginGuard],
    canDeactivate: [DocumentsWizardGuard],
    component: AddDocumentContainerComponent,
  },
  {
    path: DocumentsWizardPath.ADD_DOCUMENT_CATEGORY,
    canActivate: [LoginGuard],
    canDeactivate: [DocumentsWizardGuard],
    component: AddDocumentCategoryContainerComponent,
  },
  {
    path: DocumentsWizardPath.UPDATE_DOCUMENT,
    canActivate: [LoginGuard],
    canDeactivate: [DocumentsWizardGuard],
    component: UpdateDocumentContainerComponent,
  },
  {
    path: DocumentsWizardPath.UPDATE_DOCUMENT_CATEGORY,
    canActivate: [LoginGuard],
    canDeactivate: [DocumentsWizardGuard],
    component: UpdateDocumentCategoryContainerComponent,
  },
  {
    path: DocumentsWizardPath.DELETE_DOCUMENT,
    canActivate: [LoginGuard],
    canDeactivate: [DocumentsWizardGuard],
    component: DeleteDocumentContainerComponent,
  },
  {
    path: DocumentsWizardPath.DELETE_DOCUMENT_CATEGORY,
    canActivate: [LoginGuard],
    canDeactivate: [DocumentsWizardGuard],
    component: DeleteDocumentCategoryContainerComponent,
  },
  {
    path: DocumentsWizardPath.UPLOAD_DOCUMENT,
    canActivate: [LoginGuard],
    canDeactivate: [DocumentsWizardGuard],
    component: UploadFileContainerComponent,
  },
  {
    path: DocumentsWizardPath.CHOOSE_DISPLAY_ORDER,
    canActivate: [LoginGuard],
    canDeactivate: [DocumentsWizardGuard],
    component: ChooseDisplayOrderContainerComponent,
  },
  {
    path: DocumentsWizardPath.UPDATE_DOCUMENT_CATEGORY_DETAILS,
    canActivate: [LoginGuard],
    canDeactivate: [DocumentsWizardGuard],
    component: UpdateDocumentCategoryDetailsContainerComponent,
  },
  {
    path: DocumentsWizardPath.CANCEL_DOCUMENT_UPDATE,
    canActivate: [LoginGuard],
    canDeactivate: [DocumentsWizardGuard],
    component: CancelDocumentUpdateContainerComponent,
  },
  {
    path: DocumentsWizardPath.CHECK_UPDATE_AND_SUBMIT,
    canActivate: [LoginGuard],
    canDeactivate: [DocumentsWizardGuard],
    component: CheckUpdateAndSubmitContainerComponent,
  },
  {
    path: DocumentsWizardPath.SUCCESS,
    canActivate: [LoginGuard],
    canDeactivate: [DocumentsWizardGuard],
    component: DocumentUpdateSuccessContainerComponent,
  },
];
