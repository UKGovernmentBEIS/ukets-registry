import {
  GovernmentDetails,
  IndividualDetails,
  OrganisationDetails,
} from '@shared/model/account';

export class AccountHolderInfoChanged {
  details?: IndividualDetails | OrganisationDetails | GovernmentDetails;
  address?: unknown;
  emailAddress?: unknown;
  phoneNumber?: unknown;
}
