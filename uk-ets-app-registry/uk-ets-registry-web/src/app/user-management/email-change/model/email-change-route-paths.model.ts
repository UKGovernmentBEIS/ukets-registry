export enum EmailChangeRoutePath {
  BASE_PATH = 'email-change',
  ENTER_NEW_EMAIL_PATH = '',
  ENTER_OTP_CODE_PATH = 'otp',
  EMAIL_CHANGE_REQUEST_VERIFICATION_PATH = 'verification',
  EMAIL_CHANGE_CONFIRMATION_PATH = 'confirmation/:token'
}
