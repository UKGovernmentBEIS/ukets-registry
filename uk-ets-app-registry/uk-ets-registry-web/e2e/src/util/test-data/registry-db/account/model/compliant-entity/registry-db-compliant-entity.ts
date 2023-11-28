import { RegistryDbCompliantEntityBuilder } from './registry-db-compliant-entity.builder';

export class RegistryDbCompliantEntity {
  id: string;
  identifier: string;
  status: string;
  account_id: string;
  start_year: string;
  end_year: string;
  has_been_compliant: boolean;
  regulator: string;
  changed_regulator: string;

  constructor(builder: RegistryDbCompliantEntityBuilder) {
    this.status = builder.getStatus();
    this.account_id = builder.getAccount_id();
    this.start_year = builder.getStart_year();
    this.end_year = builder.getEnd_year();
    this.has_been_compliant = builder.getHas_been_compliant();
    this.regulator = builder.getRegulator();
    this.changed_regulator = builder.getChanged_regulator();
  }
}
