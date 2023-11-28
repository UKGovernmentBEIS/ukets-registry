import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EmergencyOtpChangeRoutingModule } from './emergency-otp-change-routing.module';
import { EffectsModule } from '@ngrx/effects';
import { EmergencyOtpChangeEffects } from './effects/emergency-otp-change.effects';
import { SharedModule } from '@shared/shared.module';
import { ReactiveFormsModule } from '@angular/forms';
import { StoreModule } from '@ngrx/store';
import * as fromEmergencyOtpChange from './reducers/emergency-otp-change.reducer';
import {
  ConfirmEmergencyOtpChangeComponent,
  ConfirmEmergencyOtpChangeContainerComponent,
  EmailEntryComponent,
  EmailEntryContainerComponent,
  EmailEntrySubmittedComponent,
  EmailEntrySubmittedContainerComponent,
  EmergencyOtpChangeInitComponent,
  EmergencyOtpChangeInitContainerComponent,
} from '@user-management/emergency-otp-change/components';

@NgModule({
  declarations: [
    EmergencyOtpChangeInitComponent,
    EmergencyOtpChangeInitContainerComponent,
    EmailEntryContainerComponent,
    EmailEntryComponent,
    EmailEntrySubmittedComponent,
    EmailEntrySubmittedContainerComponent,
    ConfirmEmergencyOtpChangeComponent,
    ConfirmEmergencyOtpChangeContainerComponent,
  ],
  imports: [
    CommonModule,
    EmergencyOtpChangeRoutingModule,
    StoreModule.forFeature(
      fromEmergencyOtpChange.emergencyOtpChangeFeatureKey,
      fromEmergencyOtpChange.reducer
    ),
    EffectsModule.forFeature([EmergencyOtpChangeEffects]),
    SharedModule,
    ReactiveFormsModule,
  ],
})
export class EmergencyOtpChangeModule {}
