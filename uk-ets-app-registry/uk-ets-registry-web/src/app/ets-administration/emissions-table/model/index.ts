import { FileBase } from '@shared/model/file';

export interface EmissionsTableRequest {
  otp: string;
  fileHeader: FileBase;
}
