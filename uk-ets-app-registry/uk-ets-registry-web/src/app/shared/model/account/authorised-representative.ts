import { User } from '@shared/user';
import { Contact } from '../contact';
import { Status } from '@shared/model/status';
import { FormRadioOption } from '@shared/form-controls/uk-radio-input/uk-radio.model';

export enum ARAccessRights {
  ROLE_BASED = 'ROLE_BASED',
  INITIATE_AND_APPROVE = 'INITIATE_AND_APPROVE',
  APPROVE = 'APPROVE',
  INITIATE = 'INITIATE',
  READ_ONLY = 'READ_ONLY',
  SURRENDER_INITIATE_AND_APPROVE = 'SURRENDER_INITIATE_AND_APPROVE',
}

export const AccessRightsLabelMap = new Map<ARAccessRights, string>([
  [ARAccessRights.ROLE_BASED, 'Role based'],
  [ARAccessRights.INITIATE_AND_APPROVE, 'Initiate and Approve'],
  [ARAccessRights.APPROVE, 'Approve'],
  [ARAccessRights.INITIATE, 'Initiate'],
  [ARAccessRights.SURRENDER_INITIATE_AND_APPROVE, 'Surrender only'],
  [ARAccessRights.READ_ONLY, 'Read Only'],
]);

export const AccessRightLabelHintMap = new Map<
  ARAccessRights,
  { text: string; hint: string }
>([
  [
    ARAccessRights.ROLE_BASED,
    {
      text: AccessRightsLabelMap.get(ARAccessRights.ROLE_BASED),
      hint: '',
    },
  ],
  [
    ARAccessRights.INITIATE_AND_APPROVE,
    {
      text: AccessRightsLabelMap.get(ARAccessRights.INITIATE_AND_APPROVE),
      hint: 'Authorised Representative can initiate or approve amendments to the Trusted Account List and any type of transaction, including surrenders and returns of allocation.',
    },
  ],
  [
    ARAccessRights.APPROVE,
    {
      text: AccessRightsLabelMap.get(ARAccessRights.APPROVE),
      hint: 'Authorised Representative can approve amendments to the Trusted Account List and any type of transaction, including surrenders and returns of allocation, but cannot initiate them.',
    },
  ],
  [
    ARAccessRights.INITIATE,
    {
      text: AccessRightsLabelMap.get(ARAccessRights.INITIATE),
      hint: 'Authorised Representative can initiate amendments to the Trusted Account List and any type of transaction, including surrenders and returns of allocation, but cannot approve them.',
    },
  ],
  [
    ARAccessRights.SURRENDER_INITIATE_AND_APPROVE,
    {
      text: AccessRightsLabelMap.get(
        ARAccessRights.SURRENDER_INITIATE_AND_APPROVE
      ),
      hint: 'Authorised Representative can initiate or approve Surrender and Return of Allocation transactions only. They cannot perform any other type of transaction or make amendments to the Trusted Account List.',
    },
  ],
  [
    ARAccessRights.READ_ONLY,
    {
      text: AccessRightsLabelMap.get(ARAccessRights.READ_ONLY),
      hint: 'Authorised Representative can view the account but cannot initiate or approve transactions or changes to the account.',
    },
  ],
]);

const arAccessRadioOptionStatesMap = new Map<
  ARAccessRights,
  { enabled: boolean; isHidden: boolean }
>([
  [ARAccessRights.ROLE_BASED, { enabled: false, isHidden: true }],
  [ARAccessRights.INITIATE_AND_APPROVE, { enabled: true, isHidden: false }],
  [ARAccessRights.APPROVE, { enabled: true, isHidden: false }],
  [ARAccessRights.INITIATE, { enabled: true, isHidden: false }],
  [
    ARAccessRights.SURRENDER_INITIATE_AND_APPROVE,
    { enabled: true, isHidden: false },
  ],
  [ARAccessRights.READ_ONLY, { enabled: true, isHidden: false }],
]);

export const arAccessRightsRadioOptionMap: Map<
  ARAccessRights,
  FormRadioOption
> = Array.from(AccessRightLabelHintMap.entries()).reduce(
  (map, [key, value]) =>
    map.set(key, {
      label: value.text,
      hint: value.hint,
      value: key.toString(),
      enabled: arAccessRadioOptionStatesMap.get(key)?.enabled,
      isHidden: arAccessRadioOptionStatesMap.get(key)?.isHidden,
    }),
  new Map<ARAccessRights, FormRadioOption>()
);

export enum ARSelectionType {
  FROM_LIST = 'FROM_LIST',
  BY_ID = 'BY_ID',
}

export class AuthorisedRepresentative {
  urid: string;
  firstName: string;
  lastName: string;
  right: ARAccessRights;
  state: AccountAccessState;
  user: User;
  contact: Contact;
}

export interface LoadARsActionPayload {
  ARs: User[];
}

export type AccountAccessState =
  | 'ACTIVE'
  | 'SUSPENDED'
  | 'REMOVED'
  | 'REQUESTED'
  | 'REJECTED';

export const accountAccessStateMap: Record<AccountAccessState, Status> = {
  ACTIVE: { color: 'green', label: 'Active' },
  REJECTED: { color: 'red', label: 'Rejected' },
  REMOVED: { color: 'red', label: 'Removed' },
  REQUESTED: { color: 'blue', label: 'Requested' },
  SUSPENDED: { color: 'red', label: 'Suspended' },
};
