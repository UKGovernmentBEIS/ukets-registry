import { RegistryDbAccountHolderBuilder } from './registry-db-account-holder.builder';

export class RegistryDbAccountHolder {
  id: string;
  identifier: string;
  name: string;
  birth_date: string;
  birth_country: string;
  registration_number: string;
  // vat_number: string;
  // justification: string;
  type: string;
  contact_id: string;
  no_reg_justification: string;
  first_name: string;
  last_name: string;

  constructor(builder: RegistryDbAccountHolderBuilder) {
    this.name = builder.getName();
    this.birth_date = builder.getBirth_date();
    this.birth_country = builder.getBirth_country();
    this.registration_number = builder.getRegistration_number();
    // this.vat_number = builder.getVat_number();
    // this.justification = builder.getJustification();
    this.type = builder.getType();
    this.contact_id = builder.getContact_id();
    this.no_reg_justification = builder.getNo_reg_justification();
    this.first_name = builder.getFirst_name();
    this.last_name = builder.getLast_name();
  }
}
