import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { AutoClaimWizardRoutingModule } from '@auto-claim-wizard/auto-claim-wizard-routing.module';
import { SharedModule } from '@shared/shared.module';
import { ReactiveFormsModule } from '@angular/forms';
import {
  CancelAutoClaimContainerComponent,
  ConfirmAutoClaimContainerComponent,
  CancelAutoClaimComponent,
} from '@auto-claim-wizard/components';
import { EffectsModule } from '@ngrx/effects';
import { AutoClaimWizardEffects } from '@auto-claim-wizard/store';

@NgModule({
  declarations: [
    ConfirmAutoClaimContainerComponent,
    CancelAutoClaimContainerComponent,
    CancelAutoClaimComponent,
  ],
  imports: [
    CommonModule,
    AutoClaimWizardRoutingModule,
    SharedModule,
    ReactiveFormsModule,
    EffectsModule.forFeature([AutoClaimWizardEffects]),
  ],
})
export class AutoClaimWizardModule {}
