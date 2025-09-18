import { Routes } from '@angular/router';
import { RecoveryMethodsChangeRoutePaths } from './recovery-methods-change.models';
import { RecoveryMethodChangedConfirmationComponent } from './components/recovery-method-changed-confirmation/recovery-method-changed-confirmation.component';
import { UpdateRecoveryEmailFormContainerComponent } from './components/update-recovery-email-form/update-recovery-email-form-container.component';
import { UpdateRecoveryEmailVerificationContainerComponent } from './components/update-recovery-email-verification/update-recovery-email-verification-container.component';
import { RemoveRecoveryEmailFormContainerComponent } from './components/remove-recovery-email-form/remove-recovery-email-form-container.component';
import { RemoveRecoveryPhoneFormContainerComponent } from './components/remove-recovery-phone-form/remove-recovery-phone-form-container.component';
import { UpdateRecoveryPhoneFormContainerComponent } from './components/update-recovery-phone-form/update-recovery-phone-form-container.component';
import { UpdateRecoveryPhoneVerificationContainerComponent } from './components/update-recovery-phone-verification/update-recovery-phone-verification-container.component';

export const recoveryMethodsChangeRoutes: Routes = [
  /*
   * UPDATE RECOVERY PHONE
   */
  {
    path: RecoveryMethodsChangeRoutePaths.UPDATE_RECOVERY_PHONE,
    component: UpdateRecoveryPhoneFormContainerComponent,
  },
  {
    path: RecoveryMethodsChangeRoutePaths.UPDATE_RECOVERY_PHONE_VERIFICATION,
    component: UpdateRecoveryPhoneVerificationContainerComponent,
  },
  {
    path: RecoveryMethodsChangeRoutePaths.UPDATE_RECOVERY_PHONE_CONFIRMATION,
    component: RecoveryMethodChangedConfirmationComponent,
    data: { message: 'The recovery phone number has been updated' },
  },

  /*
   * REMOVE RECOVERY PHONE
   */
  {
    path: RecoveryMethodsChangeRoutePaths.REMOVE_RECOVERY_PHONE,
    component: RemoveRecoveryPhoneFormContainerComponent,
  },
  {
    path: RecoveryMethodsChangeRoutePaths.REMOVE_RECOVERY_PHONE_CONFIRMATION,
    component: RecoveryMethodChangedConfirmationComponent,
    data: { message: 'The recovery phone number has been removed' },
  },

  /*
   * UPDATE RECOVERY EMAIL
   */
  {
    path: RecoveryMethodsChangeRoutePaths.UPDATE_RECOVERY_EMAIL,
    component: UpdateRecoveryEmailFormContainerComponent,
  },
  {
    path: RecoveryMethodsChangeRoutePaths.UPDATE_RECOVERY_EMAIL_VERIFICATION,
    component: UpdateRecoveryEmailVerificationContainerComponent,
  },
  {
    path: RecoveryMethodsChangeRoutePaths.UPDATE_RECOVERY_EMAIL_CONFIRMATION,
    component: RecoveryMethodChangedConfirmationComponent,
    data: { message: 'The recovery email address has been updated' },
  },

  /*
   * REMOVE RECOVERY EMAIL
   */
  {
    path: RecoveryMethodsChangeRoutePaths.REMOVE_RECOVERY_EMAIL,
    component: RemoveRecoveryEmailFormContainerComponent,
  },
  {
    path: RecoveryMethodsChangeRoutePaths.REMOVE_RECOVERY_EMAIL_CONFIRMATION,
    component: RecoveryMethodChangedConfirmationComponent,
    data: { message: 'The recovery email address has been removed' },
  },
];
