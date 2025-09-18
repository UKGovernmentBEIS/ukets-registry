import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { RequestAllocationRoutingModule } from './request-allocation-routing.module';
import { EffectsModule } from '@ngrx/effects';
import { RequestAllocationEffects } from '@request-allocation/effects';
import { StoreModule } from '@ngrx/store';
import * as fromRequestAllocationReducer from './reducers';
import { SharedModule } from '@shared/shared.module';
import { ReactiveFormsModule } from '@angular/forms';
import {
  AllocationRequestSubmittedComponent,
  AllocationRequestSubmittedContainerComponent,
  CancelAllocationRequestContainerComponent,
  CheckAllocationRequestComponent,
  CheckAllocationRequestContainerComponent,
  RequestAllocationComponent,
  RequestAllocationContainerComponent,
} from '@request-allocation/components';

@NgModule({
  declarations: [
    RequestAllocationComponent,
    RequestAllocationContainerComponent,
    CheckAllocationRequestContainerComponent,
    CheckAllocationRequestComponent,
    CancelAllocationRequestContainerComponent,
    AllocationRequestSubmittedComponent,
    AllocationRequestSubmittedContainerComponent,
  ],
  imports: [
    RequestAllocationRoutingModule,
    CommonModule,
    SharedModule,
    ReactiveFormsModule,
    StoreModule.forFeature(
      fromRequestAllocationReducer.requestAllocationFeatureKey,
      fromRequestAllocationReducer.reducer
    ),
    EffectsModule.forFeature([RequestAllocationEffects]),
  ],
})
export class RequestAllocationModule {}
