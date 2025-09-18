import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@shared/shared.module';
import { RequestDocumentsRoutingModule } from './request-documents-routing.module';
import { EffectsModule } from '@ngrx/effects';
import { RequestDocumentsEffects } from './wizard/effects';
import { CancelRequestDocumentsContainerComponent } from './wizard/components/cancel-request-documents';
import {
  SelectDocumentsComponent,
  SelectDocumentsContainerComponent,
} from './wizard/components/select-documents';
import {
  SelectRecipientComponent,
  SelectRecipientContainerComponent,
} from './wizard/components/select-recipient';
import { StoreModule } from '@ngrx/store';
import * as fromRequestDocumentReducer from '@request-documents/wizard/reducers';
import { ReactiveFormsModule } from '@angular/forms';
import { RequestDocumentsNavigationEffects } from '@request-documents/wizard/effects/request-documents-navigation.effects';
import { CancelRequestDocumentsComponent } from '@request-documents/wizard/components/cancel-request-documents';
import {
  DocumentsRequestSubmittedComponent,
  DocumentsRequestSubmittedContainerComponent,
} from '@request-documents/wizard/components/documents-request-submitted';
import {
  CheckDocumentsRequestComponent,
  CheckDocumentsRequestContainerComponent,
} from '@request-documents/wizard/components/check-documents-request';
import { CheckDocumentsRequestResolver } from '@request-documents/wizard/resolvers/check-documents-request.resolver';
import {
  UserDocumentAssigningCommentComponent,
  UserDocumentAssigningCommentContainerComponent,
} from '@request-documents/wizard/components/user-document-assigning-comment';
import { SetDocumentRequestDeadlineContainerComponent } from './wizard/components/set-document-request-deadline-container/set-document-request-deadline-container.component';

@NgModule({
  declarations: [
    CancelRequestDocumentsComponent,
    CancelRequestDocumentsContainerComponent,
    CheckDocumentsRequestContainerComponent,
    CheckDocumentsRequestComponent,
    DocumentsRequestSubmittedContainerComponent,
    DocumentsRequestSubmittedComponent,
    SelectDocumentsContainerComponent,
    SelectDocumentsComponent,
    SelectRecipientContainerComponent,
    SetDocumentRequestDeadlineContainerComponent,
    SelectRecipientComponent,
    UserDocumentAssigningCommentContainerComponent,
    UserDocumentAssigningCommentComponent,
  ],
  imports: [
    CommonModule,
    SharedModule,
    RequestDocumentsRoutingModule,
    StoreModule.forFeature(
      fromRequestDocumentReducer.requestDocumentFeatureKey,
      fromRequestDocumentReducer.reducer
    ),
    EffectsModule.forFeature([
      RequestDocumentsEffects,
      RequestDocumentsNavigationEffects,
    ]),
    ReactiveFormsModule,
  ],
  providers: [CheckDocumentsRequestResolver],
})
export class RequestDocumentsModule {}
