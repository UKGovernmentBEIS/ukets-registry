import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  AllocationTableSubmittedComponent,
  AllocationTableSubmittedContainerComponent,
  AllocationTableUploadContainerComponent,
  CancelAllocationTableUploadContainerComponent
} from '@allocation-table/components';
import { AllocationTableRoutingModule } from './allocation-table-routing.module';
import { AllocationTableUploadComponent } from '@allocation-table/components';
import { SharedModule } from '@shared/shared.module';
import { ReactiveFormsModule } from '@angular/forms';
import { CheckRequestAndSubmitContainerComponent } from '@allocation-table/components';
import { CheckRequestAndSubmitComponent } from '@allocation-table/components';
import { StoreModule } from '@ngrx/store';
import * as fromAllocationTableReducer from './reducers';
import { EffectsModule } from '@ngrx/effects';
import { AllocationTableEffects } from '@allocation-table/effects/allocation-table.effects';

@NgModule({
  declarations: [
    AllocationTableUploadContainerComponent,
    AllocationTableUploadComponent,
    CheckRequestAndSubmitContainerComponent,
    CheckRequestAndSubmitComponent,
    AllocationTableSubmittedComponent,
    AllocationTableSubmittedContainerComponent,
    CancelAllocationTableUploadContainerComponent
  ],
  imports: [
    CommonModule,
    AllocationTableRoutingModule,
    SharedModule,
    ReactiveFormsModule,
    StoreModule.forFeature(
      fromAllocationTableReducer.allocationTableReducerFeatureKey,
      fromAllocationTableReducer.reducer
    ),
    EffectsModule.forFeature([AllocationTableEffects])
  ]
})
export class AllocationTableModule {}
