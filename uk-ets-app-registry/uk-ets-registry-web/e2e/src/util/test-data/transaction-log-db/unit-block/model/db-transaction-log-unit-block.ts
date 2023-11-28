import { DbTransactionLogUnitBlockBuilder } from './db-transaction-log-unit-block.builder';

export class DbTransactionLogUnitBlock {
  id: string;
  start_block: string;
  end_block: string;
  unit_type: string;
  account_identifier: string;
  acquisition_date: string;
  last_modified_date: string;
  year: string;

  constructor(builder: DbTransactionLogUnitBlockBuilder) {
    this.id = builder.getUnitBlock_id;
    this.start_block = builder.getUnitBlock_start_block;
    this.end_block = builder.getUnitBlock_end_block;
    this.unit_type = builder.getUnitBlock_unit_type;
    this.account_identifier = builder.getUnitBlock_account_identifier;
    this.acquisition_date = builder.getUnitBlock_acquisition_date;
    this.last_modified_date = builder.getUnitBlock_last_modified_date;
    this.year = builder.getUnitBlock_year;
  }
}
