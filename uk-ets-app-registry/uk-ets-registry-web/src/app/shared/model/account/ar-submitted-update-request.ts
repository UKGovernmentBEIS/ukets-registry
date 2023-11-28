import { AuthorisedRepresentativesUpdateType } from '@authorised-representatives/model';
import { AuthorisedRepresentative } from '@shared/model/account/authorised-representative';

export interface ArSubmittedUpdateRequest {
  accountIdentifier: string;
  updateType: AuthorisedRepresentativesUpdateType;
  candidate: AuthorisedRepresentative;
  replacee: AuthorisedRepresentative;
}
