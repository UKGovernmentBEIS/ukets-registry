import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@shared/shared.module';
import { ReactiveFormsModule } from '@angular/forms';
import { StoreModule } from '@ngrx/store';
import * as fromBulkArReducer from './reducers';
import { BulkArRoutingModule } from '@registry-web/bulk-ar/bulk-ar-routing.module';
import { EffectsModule } from '@ngrx/effects';
import { BulkArEffects } from '@registry-web/bulk-ar/effects';
import { BulkArUploadComponent } from '@registry-web/bulk-ar/components/bulk-ar-upload/bulk-ar-upload.component';
import { BulkArUploadContainerComponent } from '@registry-web/bulk-ar/components/bulk-ar-upload/bulk-ar-upload-container.component';
import {
  CancelBulkArUploadComponent,
  CheckRequestAndSubmitComponent,
  CheckRequestAndSubmitContainerComponent,
} from '@registry-web/bulk-ar/components';
import { BulkArSubmittedComponent } from '@registry-web/bulk-ar/components/bulk-ar-submitted/bulk-ar-submitted.component';
import { BulkArSubmittedContainerComponent } from '@registry-web/bulk-ar/components/bulk-ar-submitted/bulk-ar-submitted-container.component';

@NgModule({
  declarations: [
    BulkArUploadComponent,
    BulkArUploadContainerComponent,
    CheckRequestAndSubmitComponent,
    CheckRequestAndSubmitContainerComponent,
    CancelBulkArUploadComponent,
    BulkArSubmittedComponent,
    BulkArSubmittedContainerComponent,
  ],
  imports: [
    CommonModule,
    SharedModule,
    ReactiveFormsModule,
    BulkArRoutingModule,
    StoreModule.forFeature(
      fromBulkArReducer.bulkArReducerFeatureKey,
      fromBulkArReducer.reducer
    ),
    EffectsModule.forFeature([BulkArEffects]),
  ],
})
export class BulkArModule {}
