import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UpdateSectionDetailsComponent } from './components/update-section-details/update-section-details.component';
import { UpdateSchedulerDetailsComponent } from './components/update-scheduler-details/update-scheduler-details.component';
import { CheckYourAnswersComponent } from './components/check-your-answers/check-your-answers.component';
import { UpdateSubmittedComponent } from './components/update-submitted/update-submitted.component';
import { UpdateSectionDetailsContainerComponent } from '@reports/report-publication/components/update-publication-details/components/update-section-details/update-section-details-container.component';
import { SharedModule } from '@shared/shared.module';
import { ReactiveFormsModule } from '@angular/forms';
import { UpdatePublicationDetailsRoutingModule } from '@reports/report-publication/components/update-publication-details/update-publication-details-routing.module';
import * as fromUpdatePublicationDetailsReducer from '@report-publication/components/update-publication-details/reducers/update-publication-details.reducer';
import { StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';
import { UpdatePublicationDetailsEffects } from '@report-publication/components/update-publication-details/effects/update-publication-details.effects';
import { UpdateSchedulerDetailsContainerComponent } from '@report-publication/components/update-publication-details/components/update-scheduler-details';
import { CheckYourAnswersContainerComponent } from '@report-publication/components/update-publication-details/components/check-your-answers/check-your-answers-container.component';
import { GdsDatePipe, GdsDateShortNoYearPipe } from '@shared/pipes';
import {
  CancelUpdateDetailsRequestComponent,
  CancelUpdateDetailsRequestContainerComponent,
} from '@report-publication/components/update-publication-details/components/cancel-request';

@NgModule({
  declarations: [
    UpdateSectionDetailsContainerComponent,
    UpdateSectionDetailsComponent,
    UpdateSchedulerDetailsContainerComponent,
    UpdateSchedulerDetailsComponent,
    CheckYourAnswersContainerComponent,
    CheckYourAnswersComponent,
    UpdateSubmittedComponent,
    CancelUpdateDetailsRequestContainerComponent,
    CancelUpdateDetailsRequestComponent,
  ],
  imports: [
    StoreModule.forFeature(
      fromUpdatePublicationDetailsReducer.publicationDetailsUpdateReducerFeatureKey,
      fromUpdatePublicationDetailsReducer.reducer
    ),
    EffectsModule.forFeature([UpdatePublicationDetailsEffects]),
    CommonModule,
    SharedModule,
    ReactiveFormsModule,
    UpdatePublicationDetailsRoutingModule,
  ],
  providers: [GdsDatePipe, GdsDateShortNoYearPipe],
})
export class UpdatePublicationDetailsModule {}
