import { NgModule } from '@angular/core';
import {
  CancelUpdateAllocationStatusComponent,
  CheckUpdateRequestComponent,
  CheckUpdateRequestContainerComponent,
  UpdateAllocationStatusFormComponent,
  UpdateAllocationStatusFormContainerComponent,
  UpdateAllocationStatusWizardComponent
} from '@allocation-status/components';
import { AllocationStatusRoutingModule } from '@allocation-status/allocation-status-routing.module';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from '@shared/shared.module';
import { StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';
import { AllocationStatusEffect } from '@allocation-status/effects';
import { AccountModule } from '@account-management/account/account.module';
import * as fromAllocationStatus from './reducers';
import { AllocationStatusResolver } from '@allocation-status/resolvers/allocation-status.resolver';
import { AccountAllocationService } from '@account-management/service/account-allocation.service';

@NgModule({
  declarations: [
    UpdateAllocationStatusFormComponent,
    UpdateAllocationStatusFormContainerComponent,
    CheckUpdateRequestComponent,
    CheckUpdateRequestContainerComponent,
    UpdateAllocationStatusWizardComponent,
    CancelUpdateAllocationStatusComponent
  ],
  imports: [
    AllocationStatusRoutingModule,
    CommonModule,
    ReactiveFormsModule,
    SharedModule,
    StoreModule.forFeature(
      fromAllocationStatus.allocationStatusFeatureKey,
      fromAllocationStatus.reducer
    ),
    EffectsModule.forFeature([AllocationStatusEffect]),
    AccountModule
  ],
  providers: [AccountAllocationService, AllocationStatusResolver]
})
export class AllocationStatusModule {}
