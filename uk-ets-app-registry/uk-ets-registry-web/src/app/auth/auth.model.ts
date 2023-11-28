import { ARAccessRights } from '../shared/model/account/authorised-representative';

export interface AuthModel {
  readonly authenticated: boolean;
  readonly showLoading: boolean;
  readonly id: string;
  readonly urid: string;
  readonly sessionUuid: string;
  readonly username: string;
  readonly roles: string[];
  readonly firstName: string;
  readonly lastName: string;
  readonly knownAs: string;
}

export interface AccountAccess {
  readonly accountFullIdentifier: string;
  readonly accessRight: ARAccessRights;
}
