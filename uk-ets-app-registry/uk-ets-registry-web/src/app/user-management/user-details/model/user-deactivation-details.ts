import { KeycloakUser } from '@registry-web/shared/user';
import { EnrolmentKey } from './enrolment-key.model';

export interface UserDeactivationDetails {
  userDetails: KeycloakUser;
  enrolmentKeyDetails: EnrolmentKey;
  deactivationComment: string;
  warningMessages: string[];
}
