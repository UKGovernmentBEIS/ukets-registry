export const RecoveryMethodsChangeRoutePaths = {
  BASE_PATH: 'recovery-methods',
  UPDATE_RECOVERY_PHONE: 'update-recovery-phone',
  UPDATE_RECOVERY_PHONE_VERIFICATION: 'update-recovery-phone-verification',
  UPDATE_RECOVERY_PHONE_CONFIRMATION: 'update-recovery-phone-confirmation',
  REMOVE_RECOVERY_PHONE: 'remove-recovery-phone',
  REMOVE_RECOVERY_PHONE_CONFIRMATION: 'remove-recovery-phone-confirmation',
  UPDATE_RECOVERY_EMAIL: 'update-recovery-email',
  UPDATE_RECOVERY_EMAIL_VERIFICATION: 'update-recovery-email-verification',
  UPDATE_RECOVERY_EMAIL_CONFIRMATION: 'update-recovery-email-confirmation',
  REMOVE_RECOVERY_EMAIL: 'remove-recovery-email',
  REMOVE_RECOVERY_EMAIL_CONFIRMATION: 'remove-recovery-email-confirmation',
};

export interface UpdateRecoveryPhoneRequest {
  newRecoveryCountryCode: string;
  newRecoveryPhoneNumber: string;
  otpCode: string;
}

export interface ResetRecoveryPhoneStateRequest {
  newRecoveryCountryCode: string;
  newRecoveryPhoneNumber: string;
}

export interface ResendUpdateRecoveryPhoneSecurityCodeRequest {
  newRecoveryCountryCode: string;
  newRecoveryPhoneNumber: string;
}

export interface UpdateRecoveryPhoneVerificationRequest {
  newRecoveryCountryCode: string;
  newRecoveryPhoneNumber: string;
  securityCode: string;
}

export interface UpdateRecoveryPhoneResponse {
  countryCode: string;
  phoneNumber: string;
  expiresInMillis: number;
}

export interface RemoveRecoveryPhoneRequest {
  otpCode: string;
}

export interface UpdateRecoveryEmailRequest {
  newRecoveryEmailAddress: string;
  otpCode: string;
}

export interface UpdateRecoveryEmailResponse {
  email: string;
  expiresInMillis: number;
}

export interface ResendUpdateRecoveryEmailSecurityCodeRequest {
  newRecoveryEmailAddress: string;
}

export interface UpdateRecoveryEmailVerificationRequest {
  newRecoveryEmailAddress: string;
  securityCode: string;
}

export interface RemoveRecoveryEmailRequest {
  otpCode: string;
}

export interface ResetRecoveryEmailStateRequest {
  newRecoveryEmailAddress: string;
}
