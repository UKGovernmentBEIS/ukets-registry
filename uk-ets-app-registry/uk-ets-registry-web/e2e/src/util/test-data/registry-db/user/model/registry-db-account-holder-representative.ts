import { RegistryDbAccountHolderRepresentativeBuilder } from './registry-db-account-holder-representative.builder';

export class RegistryDbAccountHolderRepresentative {
  id: string;
  first_name: string;
  last_name: string;
  also_known_as: string;
  birth_date: string;
  contact_id: string;
  account_holder_id: string;
  account_contact_type: string;

  constructor(builder: RegistryDbAccountHolderRepresentativeBuilder) {
    this.first_name = builder.getFirst_name();
    this.last_name = builder.getLast_name();
    this.also_known_as = builder.getAlso_known_as();
    this.birth_date = builder.getBirth_date();
    this.contact_id = builder.getContact_id();
    this.account_holder_id = builder.getAccount_holder_id();
    this.account_contact_type = builder.getAccount_contact_type();
  }
}
