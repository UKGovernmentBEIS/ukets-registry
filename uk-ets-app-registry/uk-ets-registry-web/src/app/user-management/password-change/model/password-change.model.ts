/**
 * The request object to be submitted.
 */
export interface PasswordChangeRequest {
  currentPassword: string;
  newPassword: string;
  otp: string;
}
