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
  [
    ARAccessRights.INITIATE_AND_APPROVE,
    'Initiate and approve transactions and Trusted Account List (TAL) updates',
  ],
  [
    ARAccessRights.APPROVE,
    'Approve transfers and Trusted Account List (TAL) updates',
  ],
  [
    ARAccessRights.INITIATE,
    'Initiate transfers and Trusted Account List (TAL) updates',
  ],
  [
    ARAccessRights.SURRENDER_INITIATE_AND_APPROVE,
    'Initiate and approve Surrender of allowances and Return of excess allocation transactions',
  ],
  [ARAccessRights.READ_ONLY, 'Read only'],
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
      hint: 'This will allow the representative to initiate and approve transactions and changes to the trusted account list',
    },
  ],
  [
    ARAccessRights.APPROVE,
    {
      text: AccessRightsLabelMap.get(ARAccessRights.APPROVE),
      hint: 'This will allow the representative to approve transactions and changes to the trusted account list',
    },
  ],
  [
    ARAccessRights.INITIATE,
    {
      text: AccessRightsLabelMap.get(ARAccessRights.INITIATE),
      hint: 'This will allow the representative to initiate transactions and changes to the trusted account list',
    },
  ],
  [
    ARAccessRights.SURRENDER_INITIATE_AND_APPROVE,
    {
      text: AccessRightsLabelMap.get(
        ARAccessRights.SURRENDER_INITIATE_AND_APPROVE
      ),
      hint: 'This will allow the representative to initiate and approve Surrender of allowances and Return of excess allocation transactions',
    },
  ],
  [
    ARAccessRights.READ_ONLY,
    {
      text: AccessRightsLabelMap.get(ARAccessRights.READ_ONLY),
      hint: '',
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
