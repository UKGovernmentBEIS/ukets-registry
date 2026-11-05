import { Status } from '../status';

interface AccountContact {
  fullName: string;
  email: string;
  phoneNumber: ContactPhoneNumbers;
  invitedOn?: string;
  invitationStatus: boolean;
}

export type MetsContactType = 'PRIMARY' | 'SECONDARY' | 'FINANCIAL' | 'SERVICE';

export type MetsContactOperatorType =
  | 'OPERATOR_ADMIN'
  | 'OPERATOR'
  | 'CONSULTANT_AGENT'
  | 'EMITTER';

export interface MetsContact extends AccountContact {
  contactTypes: MetsContactType[];
  operatorType: MetsContactOperatorType;
}

export type RegistryContactType = 'PRIMARY' | 'ALTERNATIVE';

export interface RegistryContact extends AccountContact {
  contactType: RegistryContactType;
}

export interface ContactPhoneNumbers {
  countryCode1: string;
  phoneNumber1: string;
  countryCode2: string;
  phoneNumber2: string;
  empty?: boolean;
}

export interface AccountContactSendInvitationDTO {
  metsContacts: MetsContact[];
  registryContacts: RegistryContact[];
}

export interface ContactInvitationStatusDTO {
  email: string;
  invitationStatus: boolean;
}

export interface RegistryContactInvitationStatusDTO
  extends ContactInvitationStatusDTO {
  contactType: RegistryContactType;
}

export interface AccountContactChangeInvitationStatusDTO {
  metsContacts: ContactInvitationStatusDTO[];
  registryContacts: RegistryContactInvitationStatusDTO[];
}

export const claimInvitationStatusMap: Record<string, Status> = {
  true: { color: 'green', label: 'Enabled' },
  false: { color: 'red', label: 'Disabled' },
};
