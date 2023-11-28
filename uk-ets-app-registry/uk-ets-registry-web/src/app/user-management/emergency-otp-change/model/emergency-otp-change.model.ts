export enum EmergencyOtpChangeRoutes {
  ROOT = 'emergency-otp-change',
  INIT = 'init',
  EMAIL_ENTRY = 'email-entry',
  EMAIL_SUBMITTED = 'email-submitted',
  EMAIL_VERIFY = 'email-verify'
}

export interface EmergencyOtpChangeTaskResponse {
  requestId: string;
  tokenExpired: boolean;
}
