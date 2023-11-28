import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@shared/shared.module';
import { ReactiveFormsModule } from '@angular/forms';
import {
  PublicationOverviewComponent,
  SectionDetailsComponent,
  UnpublishFileComponent,
} from '@reports/report-publication/components';
import { SectionDetailsContainerComponent } from '@reports/report-publication/components/section-details/section-details-container.component';
import { PublicationOverviewContainerComponent } from '@reports/report-publication/components/publication-overview/publication-overview-container.component';
import { ReportPublicationRoutingModule } from '@reports/report-publication/report-publication-routing.module';
import { ReportPublicationService } from '@report-publication/services';
import { StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';
import { ReportPublicationEffects } from '@report-publication/effects';
import * as fromReportPublication from '@report-publication/reducers';
import { SectionDetailsHeaderComponent } from './components/section-details-header/section-details-header.component';
import { SortService } from '@registry-web/shared/search/sort/sort.service';
import { PublicationWizardsContainerComponent } from '@reports/report-publication/components/publication-wizards-container/publication-wizards-container.component';
import { GdsDatePipe, GdsDateShortNoYearPipe } from '@shared/pipes';
import { UnpublishFileContainerComponent } from '@report-publication/components/unpublish-file/unpublish-file.-container.component';

@NgModule({
  declarations: [
    SectionDetailsContainerComponent,
    SectionDetailsComponent,
    PublicationOverviewContainerComponent,
    PublicationOverviewComponent,
    PublicationWizardsContainerComponent,
    UnpublishFileContainerComponent,
    UnpublishFileComponent,
    SectionDetailsHeaderComponent,
  ],
  imports: [
    CommonModule,
    SharedModule,
    ReactiveFormsModule,
    ReportPublicationRoutingModule,
    StoreModule.forFeature(
      fromReportPublication.reportPublicationFeatureKey,
      fromReportPublication.reducer
    ),
    EffectsModule.forFeature([ReportPublicationEffects]),
  ],
  providers: [
    ReportPublicationService,
    SortService,
    GdsDatePipe,
    GdsDateShortNoYearPipe,
  ],
})
export class ReportPublicationModule {}
