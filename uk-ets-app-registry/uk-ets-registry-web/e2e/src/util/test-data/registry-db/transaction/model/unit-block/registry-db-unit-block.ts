import { RegistryDbUnitBlockBuilder } from './registry-db-unit-block.builder';

export class RegistryDbUnitBlock {
  id: string;
  start_block: string;
  end_block: string;
  unit_type: string;
  originating_country_code: string;
  original_period: string;
  applicable_period: string;
  account_identifier: string;
  reserved_for_transaction: string;
  acquisition_date: string;
  environmental_activity: string;
  expiry_date: string;
  last_modified_date: string;
  project_number: string;
  project_track: string;
  sop: boolean;

  constructor(builder: RegistryDbUnitBlockBuilder) {
    this.start_block = builder.getStart_block();
    this.end_block = builder.getEnd_block();
    this.unit_type = builder.getUnit_type();
    this.originating_country_code = builder.getOriginating_country_code();
    this.original_period = builder.getOriginal_period();
    this.applicable_period = builder.getApplicable_period();
    this.account_identifier = builder.getAccount_identifier();
    this.reserved_for_transaction = builder.getReserved_for_transaction();
    this.acquisition_date = builder.getAcquisition_date();
    this.environmental_activity = builder.getEnvironmental_activity();
    this.expiry_date = builder.getExpiry_date();
    this.last_modified_date = builder.getLast_modified_date();
    this.project_number = builder.getProject_number();
    this.project_track = builder.getProject_track();
    this.sop = builder.getSop();
  }
}
