import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SelectPublicationFileComponent } from '@report-publication/components/upload-publication-file/components/select-publication-file/select-publication-file.component';
import { AddPublicationYearComponent } from './components/add-publication-year/add-publication-year.component';
import { SelectPublicationFileContainerComponent } from '@report-publication/components/upload-publication-file/components/select-publication-file/select-publication-file-container.component';
import { CheckAnwersAndSubmitComponent } from './components/check-anwers-and-submit/check-anwers-and-submit.component';
import { CheckAnswersAndSubmitContainerComponent } from '@report-publication/components/upload-publication-file/components/check-anwers-and-submit/check-answers-and-submit-container.component';
import { FileSubmittedComponent } from './components/file-submitted/file-submitted.component';
import { SharedModule } from '@shared/shared.module';
import { ReactiveFormsModule } from '@angular/forms';
import { UploadPublicationFileRoutingModule } from '@report-publication/components/upload-publication-file/upload-publication-file-routing.module';
import * as fromReportFileReducer from '@report-publication/components/upload-publication-file/reducers/upload-publication-file.reducer';
import { StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';
import { UploadPublicationFileEffects } from '@report-publication/components/upload-publication-file/effects/upload-publication-file.effects';
import { AddPublicationYearContainerComponent } from '@report-publication/components/upload-publication-file/components/add-publication-year/add-publication-year-container.component';
import {
  CancelPublicationFileUploadRequestComponent,
  CancelPublicationFileUploadRequestContainerComponent,
} from '@report-publication/components/upload-publication-file/components/cancel-request';

@NgModule({
  declarations: [
    SelectPublicationFileContainerComponent,
    SelectPublicationFileComponent,
    AddPublicationYearContainerComponent,
    AddPublicationYearComponent,
    CheckAnswersAndSubmitContainerComponent,
    CheckAnwersAndSubmitComponent,
    FileSubmittedComponent,
    CancelPublicationFileUploadRequestContainerComponent,
    CancelPublicationFileUploadRequestComponent,
  ],
  imports: [
    StoreModule.forFeature(
      fromReportFileReducer.publicationReportFileReducerFeatureKey,
      fromReportFileReducer.reducer
    ),
    EffectsModule.forFeature([UploadPublicationFileEffects]),
    CommonModule,
    SharedModule,
    ReactiveFormsModule,
    UploadPublicationFileRoutingModule,
  ],
})
export class UploadPublicationFileModule {}
