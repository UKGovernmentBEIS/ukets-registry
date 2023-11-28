export enum EmergencyPasswordOtpChangeRoutes {
  ROOT = 'emergency-password-otp-change',
  INIT = 'init',
  EMAIL_ENTRY = 'email-entry',
  EMAIL_SUBMITTED = 'email-submitted',
  EMAIL_VERIFY = 'email-verify'
}

export interface EmergencyPasswordOtpChangeTaskResponse {
  requestId: string;
  tokenExpired: boolean;
}
