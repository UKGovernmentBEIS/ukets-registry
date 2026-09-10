import { AgentType } from '@user-management/user-details/model/agent.model';

export type PublicAgentDetails = {
  companyName: string;
  contactEmailAddress: string;
  phone: {
    countryCode: string;
    phoneNumber: string;
  };
};

export type UserAgentInfo = { urid: string; type: AgentType } & {
  details: PublicAgentDetails;
};

export type UserAgentUpdateRequest = {
  agent: AgentType;
  agentCompanyName: string;
  agentEmailAddress: string;
  agentPhoneNumberCountryCode: string;
  agentPhoneNumber: string;
};
