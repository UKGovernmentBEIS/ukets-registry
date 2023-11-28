import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SelectYearComponent } from '@exclusion-status-update-wizard/select-year';
import { CheckUpdateStatusComponent } from '@exclusion-status-update-wizard/check-update-status';
import { SelectYearContainerComponent } from '@exclusion-status-update-wizard/select-year';
import { CancelUpdateExclusionStatusContainerComponent } from '@exclusion-status-update-wizard/cancel-update-exclusion-status';
import { CheckUpdateStatusContainerComponent } from '@exclusion-status-update-wizard/check-update-status/';
import { SubmittedUpdateExclusionStatusContainerComponent } from '@exclusion-status-update-wizard/submitted-update-exclusion-status';
import { SharedModule } from '@registry-web/shared/shared.module';
import { ExclusionStatusUpdateWizardService } from '@exclusion-status-update-wizard/services';
import { ReactiveFormsModule } from '@angular/forms';
import { SelectExclusionStatusComponent } from '@exclusion-status-update-wizard/select-exclusion-status';
import { StoreModule } from '@ngrx/store';
import * as fromUpdateExclusionStatus from '@exclusion-status-update-wizard/reducers';
import { EffectsModule } from '@ngrx/effects';
import { UpdateExclusionStatusEffects } from '@exclusion-status-update-wizard/effects';
import { SelectExclusionStatusContainerComponent } from '@exclusion-status-update-wizard/select-exclusion-status';
import { InstallationPipe, AircraftOperatorPipe } from '@shared/pipes';
import { ExclusionStatusUpdateWizardRoutingModule } from '@exclusion-status-update-wizard/exclusion-status-update-wizard-routing.module';

@NgModule({
  declarations: [
    SelectYearComponent,
    SelectYearContainerComponent,
    CancelUpdateExclusionStatusContainerComponent,
    CheckUpdateStatusComponent,
    CheckUpdateStatusContainerComponent,
    SubmittedUpdateExclusionStatusContainerComponent,
    SelectExclusionStatusComponent,
    SelectExclusionStatusContainerComponent,
  ],
  imports: [
    CommonModule,
    SharedModule,
    ReactiveFormsModule,
    ExclusionStatusUpdateWizardRoutingModule,
    StoreModule.forFeature(
      fromUpdateExclusionStatus.updateExclusionStatusFeatureKey,
      fromUpdateExclusionStatus.reducer
    ),
    EffectsModule.forFeature([UpdateExclusionStatusEffects]),
  ],
  providers: [
    ExclusionStatusUpdateWizardService,
    InstallationPipe,
    AircraftOperatorPipe,
  ],
})
export class ExclusionStatusUpdateWizardModule {}
