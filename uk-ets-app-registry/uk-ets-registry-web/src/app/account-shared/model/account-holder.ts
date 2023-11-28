import {
  AccountHolder,
  IndividualDetails,
  OrganisationDetails,
} from '@shared/model/account';

export type AccountHolderTypeAheadSearchResult = {
  identifier: AccountHolder['id'];
} & Pick<AccountHolder, 'type'> &
  Pick<IndividualDetails, 'name' | 'firstName' | 'lastName'> &
  Pick<OrganisationDetails, 'name'>;
