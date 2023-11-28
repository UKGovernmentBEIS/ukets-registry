import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StoreModule } from '@ngrx/store';
import { SharedModule } from '@shared/shared.module';
import * as fromReports from './reducers/reports.reducer';
import { EffectsModule } from '@ngrx/effects';
import { ReportsEffects } from '@reports/effects';
import {
  DownloadReportsListComponent,
  DownloadReportsListContainerComponent,
  DownloadReportsListItemComponent,
  StandardReportsComponent,
  StandardReportsContainerComponent,
  StandardReportsFiltersComponent,
} from '@reports/components';

import { NgbTypeaheadModule } from '@ng-bootstrap/ng-bootstrap';
import { ReactiveFormsModule } from '@angular/forms';
import { ReportsRoutingModule } from '@reports/reports-routing.module';

@NgModule({
  declarations: [
    DownloadReportsListContainerComponent,
    DownloadReportsListComponent,
    DownloadReportsListItemComponent,
    StandardReportsContainerComponent,
    StandardReportsComponent,
    StandardReportsFiltersComponent,
  ],
  imports: [
    CommonModule,
    SharedModule,
    ReactiveFormsModule,
    StoreModule.forFeature(fromReports.reportsFeatureKey, fromReports.reducer),
    EffectsModule.forFeature([ReportsEffects]),
    ReportsRoutingModule,
    NgbTypeaheadModule,
  ],
  exports: [StandardReportsContainerComponent],
})
export class ReportsModule {}
