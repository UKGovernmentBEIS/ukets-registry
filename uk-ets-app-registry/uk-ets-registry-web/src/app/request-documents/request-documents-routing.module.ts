import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginGuard } from '@shared/guards';
import { CancelRequestDocumentsContainerComponent } from '@request-documents/wizard/components/cancel-request-documents';
import { SelectDocumentsContainerComponent } from '@request-documents/wizard/components/select-documents';
import { SelectRecipientContainerComponent } from '@request-documents/wizard/components/select-recipient';
import { DocumentsRequestSubmittedContainerComponent } from '@request-documents/wizard/components/documents-request-submitted';
import { CheckDocumentsRequestResolver } from '@request-documents/wizard/resolvers/check-documents-request.resolver';
import { CheckDocumentsRequestContainerComponent } from '@request-documents/wizard/components/check-documents-request';
import { UserDocumentAssigningCommentContainerComponent } from '@request-documents/wizard/components/user-document-assigning-comment';
import { SetDocumentRequestDeadlineContainerComponent } from './wizard/components/set-document-request-deadline-container/set-document-request-deadline-container.component';

const routes: Routes = [
  {
    path: '',
    canActivate: [LoginGuard],
    component: SelectDocumentsContainerComponent,
    title: 'Documents',
  },
  {
    path: 'select-documents',
    canActivate: [LoginGuard],
    component: SelectDocumentsContainerComponent,
  },
  {
    path: 'check-documents-request',
    canActivate: [LoginGuard],
    resolve: { goBackPath: CheckDocumentsRequestResolver },
    component: CheckDocumentsRequestContainerComponent,
  },
  {
    path: 'cancel-request',
    canActivate: [LoginGuard],
    component: CancelRequestDocumentsContainerComponent,
  },
  {
    path: 'select-recipient',
    canActivate: [LoginGuard],
    component: SelectRecipientContainerComponent,
  },
  {
    path: 'set-deadline-ah',
    canActivate: [LoginGuard],
    component: SetDocumentRequestDeadlineContainerComponent,
  },
  {
    path: 'set-deadline-user',
    canActivate: [LoginGuard],
    component: SetDocumentRequestDeadlineContainerComponent,
  },
  {
    path: 'assigning-user-comment',
    canActivate: [LoginGuard],
    component: UserDocumentAssigningCommentContainerComponent,
  },
  {
    path: 'request-submitted',
    canActivate: [LoginGuard],
    component: DocumentsRequestSubmittedContainerComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class RequestDocumentsRoutingModule {}
