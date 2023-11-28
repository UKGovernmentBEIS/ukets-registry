export interface EmailChange {
  urid: string;
  newEmail: string;
  callerUrl?: string;
  requestId?: string;
  tokenExpired?: boolean;
}

export interface EmailChangeConfirmation {
  tokenExpired: boolean;
  requestId: string;
}

export interface EmailChangeRequest {
  urid: string;
  newEmail: string;
  otp: string;
}
