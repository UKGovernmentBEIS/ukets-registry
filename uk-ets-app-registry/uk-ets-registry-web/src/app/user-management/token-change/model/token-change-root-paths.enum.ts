export enum TokenChangeRoutingPaths {
  BASE_PATH = 'token-change',
  PAGE_1_ENTER_REASON = '',
  PAGE_2_ENTER_CODE = 'otp',
  PAGE_3_VERIFY = 'verification',
  PAGE_4_EXPIRED = 'expired',
  PAGE_5_EMAIL_CLICKED = 'email-clicked/:token'
}
