import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { SharedModule } from '@registry-web/shared/shared.module';
import { AllocationJobStatusRoutingModule } from './allocation-job-status-routing.module';
import { StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';
import * as fromAllocationJobStatus from './store/allocation-job-status.reducer';
import AllocationJobStatusEffect from './store/allocation-job-status.effects';
import { CancelPendingAllocationsComponent } from './components/cancel-pending-allocations';
import { AllocationJobStatusResolver } from './resolvers';

@NgModule({
  declarations: [CancelPendingAllocationsComponent],
  imports: [
    AllocationJobStatusRoutingModule,
    CommonModule,
    SharedModule,
    StoreModule.forFeature(
      fromAllocationJobStatus.allocationJobStatusFeatureKey,
      fromAllocationJobStatus.reducer
    ),
    EffectsModule.forFeature([AllocationJobStatusEffect]),
  ],
  providers: [AllocationJobStatusResolver],
})
export class AllocationJobStatusModule {}
