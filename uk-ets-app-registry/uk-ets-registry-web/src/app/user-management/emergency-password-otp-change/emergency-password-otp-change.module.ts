import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  ConfirmEmergencyPasswordOtpChangeComponent,
  ConfirmEmergencyPasswordOtpChangeContainerComponent,
  EmailEntryComponent,
  EmailEntryContainerComponent,
  EmailEntrySubmittedComponent,
  EmailEntrySubmittedContainerComponent,
  EmergencyPasswordOtpChangeInitComponent,
  EmergencyPasswordOtpChangeInitContainerComponent,
} from '@user-management/emergency-password-otp-change/components';
import { EmergencyPasswordOtpChangeRoutingModule } from '@user-management/emergency-password-otp-change';
import { StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';
import { EmergencyPasswordOtpChangeEffects } from '@user-management/emergency-password-otp-change/effects';
import { ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from '@shared/shared.module';
import * as fromEmergencyPasswordOtpChange from './reducers/';

@NgModule({
  declarations: [
    EmergencyPasswordOtpChangeInitComponent,
    EmergencyPasswordOtpChangeInitContainerComponent,
    EmailEntryContainerComponent,
    EmailEntryComponent,
    EmailEntrySubmittedComponent,
    EmailEntrySubmittedContainerComponent,
    ConfirmEmergencyPasswordOtpChangeComponent,
    ConfirmEmergencyPasswordOtpChangeContainerComponent,
  ],
  imports: [
    CommonModule,
    EmergencyPasswordOtpChangeRoutingModule,
    StoreModule.forFeature(
      fromEmergencyPasswordOtpChange.emergencyPasswordOtpChangeFeatureKey,
      fromEmergencyPasswordOtpChange.reducer
    ),
    EffectsModule.forFeature([EmergencyPasswordOtpChangeEffects]),
    SharedModule,
    ReactiveFormsModule,
  ],
})
export class EmergencyPasswordOtpChangeModule {}
