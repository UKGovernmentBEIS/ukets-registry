import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { PasswordStrengthValidatorService } from '@uk-password-control/validation';
import { SharedModule } from '@shared/shared.module';
import {
  PasswordStrengthMeterComponent,
  UkPasswordInputComponent,
} from '@uk-password-control/components';
import { UkPasswordControlEffects } from '@uk-password-control/store/effects';
import * as fromPasswordStrengthReducer from '@uk-password-control/store/reducers';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';

@NgModule({
  declarations: [UkPasswordInputComponent, PasswordStrengthMeterComponent],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    SharedModule,
    StoreModule.forFeature(
      fromPasswordStrengthReducer.passwordStrengthFeatureKey,
      fromPasswordStrengthReducer.reducer
    ),
    EffectsModule.forFeature([UkPasswordControlEffects]),
  ],
  exports: [UkPasswordInputComponent],
  providers: [PasswordStrengthValidatorService],
})
export class UkPasswordControlModule {}
