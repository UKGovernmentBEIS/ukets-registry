import { ARAccessRights } from '@shared/model/account';

export interface ArUpdateRequest {
  candidateUrid?: string;
  replaceeUrid?: string;
  accessRight?: ARAccessRights;
  comment?: string;
}
