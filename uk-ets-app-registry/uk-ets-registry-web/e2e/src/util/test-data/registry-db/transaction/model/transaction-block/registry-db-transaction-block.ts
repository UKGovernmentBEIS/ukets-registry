import { RegistryDbTransactionBlockBuilder } from './registry-db-transaction-block.builder';

export class RegistryDbTransactionBlock {
  id: string;
  start_block: string;
  end_block: string;
  unit_type: string;
  originating_country_code: string;
  original_period: string;
  applicable_period: string;
  environmental_activity: string;
  expiry_date: string;
  transaction_id: string;
  project_number: string;
  project_track: string;
  sop: boolean;

  constructor(builder: RegistryDbTransactionBlockBuilder) {
    this.start_block = builder.getStart_block();
    this.end_block = builder.getEnd_block();
    this.unit_type = builder.getUnit_type();
    this.originating_country_code = builder.getOriginating_country_code();
    this.original_period = builder.getOriginal_period();
    this.applicable_period = builder.getApplicable_period();
    this.environmental_activity = builder.getEnvironmental_activity();
    this.expiry_date = builder.getExpiry_date();
    this.transaction_id = builder.getTransaction_id();
    this.project_number = builder.getProject_number();
    this.project_track = builder.getProject_track();
    this.sop = builder.getSop();
  }
}
