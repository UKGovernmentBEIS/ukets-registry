export interface PhoneInfo {
  countryCode: string;
  phoneNumber: string;
}

export type MobileNumberVerificationStatus =
  | 'MOBILE'
  | 'FIXED_LINE_OR_MOBILE'
  | 'UNKNOWN';
