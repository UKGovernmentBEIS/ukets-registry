interface AccountContact {
  fullName: string;
  email: string;
  phoneNumber: ContactPhoneNumbers;
  invitedOn?: string;
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
