// Enum type for phone number validation results returned by PhoneNumberUtil.isPossibleNumberWithReason function
export enum PhoneNumberValidationResult {
  IS_POSSIBLE = 0,
  INVALID_COUNTRY_CODE = 1,
  TOO_SHORT = 2,
  TOO_LONG = 3,
  IS_POSSIBLE_LOCAL_ONLY = 4,
  INVALID_LENGTH = 5
}
