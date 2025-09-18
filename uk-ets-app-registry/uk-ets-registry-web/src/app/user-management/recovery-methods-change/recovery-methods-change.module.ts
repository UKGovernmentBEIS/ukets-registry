import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from '@shared/shared.module';
import { RouterModule } from '@angular/router';
import { recoveryMethodsChangeRoutes } from './recovery-methods-change.routes';
import { RecoveryMethodChangedConfirmationComponent } from './components/recovery-method-changed-confirmation/recovery-method-changed-confirmation.component';
import { UpdateRecoveryEmailFormComponent } from './components/update-recovery-email-form/update-recovery-email-form.component';
import { UpdateRecoveryEmailFormContainerComponent } from './components/update-recovery-email-form/update-recovery-email-form-container.component';
import { EffectsModule } from '@ngrx/effects';
import { UpdateRecoveryEmailEffects } from './store/update-recovery-email.effects';
import { StoreModule } from '@ngrx/store';
import { recoveryMethodsChangeFeature } from './store/recovery-methods-change.reducer';
import { UpdateRecoveryEmailVerificationContainerComponent } from './components/update-recovery-email-verification/update-recovery-email-verification-container.component';
import { UpdateRecoveryEmailVerificationComponent } from './components/update-recovery-email-verification/update-recovery-email-verification.component';
import { UpdateRecoveryPhoneEffects } from './store/update-recovery-phone.effects';
import { RemoveRecoveryEmailEffects } from './store/remove-recovery-email.effects';
import { RemoveRecoveryPhoneEffects } from './store/remove-recovery-phone.effects';
import { RemoveRecoveryEmailFormComponent } from './components/remove-recovery-email-form/remove-recovery-email-form.component';
import { RemoveRecoveryEmailFormContainerComponent } from './components/remove-recovery-email-form/remove-recovery-email-form-container.component';
import { RemoveRecoveryPhoneFormComponent } from './components/remove-recovery-phone-form/remove-recovery-phone-form.component';
import { RemoveRecoveryPhoneFormContainerComponent } from './components/remove-recovery-phone-form/remove-recovery-phone-form-container.component';
import { UpdateRecoveryPhoneFormComponent } from './components/update-recovery-phone-form/update-recovery-phone-form.component';
import { UpdateRecoveryPhoneFormContainerComponent } from './components/update-recovery-phone-form/update-recovery-phone-form-container.component';
import { UpdateRecoveryPhoneVerificationContainerComponent } from './components/update-recovery-phone-verification/update-recovery-phone-verification-container.component';
import { UpdateRecoveryPhoneVerificationComponent } from './components/update-recovery-phone-verification/update-recovery-phone-verification.component';
import { TimerComponent } from '@user-management/recovery-methods-change/components/timer/timer.component';

@NgModule({
  declarations: [
    UpdateRecoveryPhoneFormContainerComponent,
    UpdateRecoveryPhoneFormComponent,
    UpdateRecoveryPhoneVerificationContainerComponent,
    UpdateRecoveryPhoneVerificationComponent,
    RemoveRecoveryPhoneFormContainerComponent,
    RemoveRecoveryPhoneFormComponent,
    UpdateRecoveryEmailFormContainerComponent,
    UpdateRecoveryEmailFormComponent,
    UpdateRecoveryEmailVerificationContainerComponent,
    UpdateRecoveryEmailVerificationComponent,
    RemoveRecoveryEmailFormContainerComponent,
    RemoveRecoveryEmailFormComponent,
    RecoveryMethodChangedConfirmationComponent,
    TimerComponent,
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(recoveryMethodsChangeRoutes),
    ReactiveFormsModule,
    SharedModule,
    StoreModule.forFeature(recoveryMethodsChangeFeature),
    EffectsModule.forFeature([
      UpdateRecoveryPhoneEffects,
      RemoveRecoveryPhoneEffects,
      UpdateRecoveryEmailEffects,
      RemoveRecoveryEmailEffects,
    ]),
  ],
})
export class RecoveryMethodsChangeModule {}
