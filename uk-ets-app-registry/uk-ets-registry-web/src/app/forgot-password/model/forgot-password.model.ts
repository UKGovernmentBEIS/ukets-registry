export interface ValidateTokenResponse {
  success: boolean;
}

export interface ResetPasswordRequest {
  token: string;
  otp: string;
  newPasswd: string;
}

export interface ResetPasswordResponse {
  email: string;
  success: boolean;
}
