import { RegistryDbAccountOwnershipBuilder } from './registry-db-account-ownership.builder';

export class RegistryDbAccountOwnership {
  id: string;
  account_id: string;
  account_holder_id: string;
  status: string;
  date_of_ownership: string;

  constructor(builder: RegistryDbAccountOwnershipBuilder) {
    this.id = builder.getId;
    this.account_id = builder.getAccount_id;
    this.account_holder_id = builder.getAccountHolder_id;
    this.status = builder.getStatus;
    this.date_of_ownership = builder.getDateOfOwnership;
  }
}
