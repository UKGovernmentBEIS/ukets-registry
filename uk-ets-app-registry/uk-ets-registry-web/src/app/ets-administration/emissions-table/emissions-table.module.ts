import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { EmissionsTableRoutingModule } from '@emissions-table/emissions-table-routing.module';
import { EmissionsTableUploadComponent } from '@emissions-table/components/emissions-table-upload/emissions-table-upload.component';
import { EmissionsTableUploadContainerComponent } from '@emissions-table/components/emissions-table-upload/emissions-table-upload-container.component';
import { CheckRequestAndSubmitComponent } from '@emissions-table/components/check-request-and-submit/check-request-and-submit.component';
import { CheckRequestAndSubmitContainerComponent } from '@emissions-table/components/check-request-and-submit/check-request-and-submit-container.component';
import { CancelEmissionsTableUploadContainerComponent } from '@emissions-table/components/cancel-emissions-table-upload/cancel-emissions-table-upload-container.component';
import { EmissionsTableSubmittedContainerComponent } from '@emissions-table/components/emissions-table-submitted/emissions-table-submitted-container.component';
import { SharedModule } from '@shared/shared.module';
import { ReactiveFormsModule } from '@angular/forms';
import { EffectsModule } from '@ngrx/effects';
import { EmissionsTableEffects } from '@emissions-table/store/effects';
import * as fromEmissionsTableReducer from '@emissions-table/store/reducers';
import { StoreModule } from '@ngrx/store';

@NgModule({
  declarations: [
    EmissionsTableUploadComponent,
    EmissionsTableUploadContainerComponent,
    CheckRequestAndSubmitComponent,
    CheckRequestAndSubmitContainerComponent,
    CancelEmissionsTableUploadContainerComponent,
    EmissionsTableSubmittedContainerComponent,
  ],
  imports: [
    SharedModule,
    CommonModule,
    ReactiveFormsModule,
    EmissionsTableRoutingModule,
    StoreModule.forFeature(
      fromEmissionsTableReducer.emissionsTableReducerFeatureKey,
      fromEmissionsTableReducer.reducer
    ),
    EffectsModule.forFeature([EmissionsTableEffects]),
  ],
})
export class EmissionsTableModule {}
