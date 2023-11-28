export interface ApiErrorBody {
  errorDetails: ApiErrorDetail[];
}

export interface ApiErrorDetail {
  code?: string;
  message?: string;
  identifier?: string;
  urid?: string;
  componentId?: string;
  errorFileId?: number;
  errorFilename?: string;
}
