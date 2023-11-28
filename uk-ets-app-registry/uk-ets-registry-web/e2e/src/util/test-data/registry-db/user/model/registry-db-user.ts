import { RegistryDbUserBuilder } from './registry-db-user.builder';

export class RegistryDbUser {
  id: string;
  urid: string;
  iam_identifier: string;
  enrolment_key: string;
  enrolment_key_date: string;
  state: string;
  first_name: string;
  last_name: string;
  disclosed_name: string;

  constructor(builder: RegistryDbUserBuilder) {
    this.urid = builder.get_urid;
    this.iam_identifier = builder.get_iam_identifier;
    this.enrolment_key = builder.get_enrolment_key;
    this.enrolment_key_date = builder.get_enrolment_key_date;
    this.state = builder.get_state;
    this.first_name = builder.get_first_name;
    this.last_name = builder.get_last_name;
    this.disclosed_name = builder.get_disclosed_name;
  }
}
