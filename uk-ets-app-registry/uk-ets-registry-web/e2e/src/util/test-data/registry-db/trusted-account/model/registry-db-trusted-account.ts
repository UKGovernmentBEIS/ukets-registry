import { RegistryDbTrustedAccountBuilder } from './registry-db-trusted-account.builder';

export class RegistryDbTrustedAccount {
  id: string;
  account_id: string;
  trusted_account_full_identifier: string;
  status: string;
  description: string;
  activation_date: string;

  constructor(builder: RegistryDbTrustedAccountBuilder) {
    this.account_id = builder.getAccount_id;
    this.trusted_account_full_identifier =
      builder.getTrusted_account_full_identifier;
    this.status = builder.getStatus;
    this.description = builder.getDescription;
    this.activation_date = builder.getActivation_date;
  }
}
