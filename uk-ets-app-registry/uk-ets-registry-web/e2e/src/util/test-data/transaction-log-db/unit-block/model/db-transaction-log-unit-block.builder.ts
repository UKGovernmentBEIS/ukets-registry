import { DbTransactionLogUnitBlock } from './db-transaction-log-unit-block';

export class DbTransactionLogUnitBlockBuilder {
  private _id: string;
  private _start_block: string;
  private _end_block: string;
  private _unit_type: string;
  private _account_identifier: string;
  private _acquisition_date: string;
  private _last_modified_date: string;
  private _year: string;

  constructor() {}

  build() {
    return new DbTransactionLogUnitBlock(this);
  }

  get getUnitBlock_id(): string {
    return this._id;
  }

  unit_block_id(value: string) {
    this._id = value;
    return this;
  }

  get getUnitBlock_start_block(): string {
    return this._start_block;
  }

  unit_block_start_block(value: string) {
    this._start_block = value;
    return this;
  }

  get getUnitBlock_end_block(): string {
    return this._end_block;
  }

  unit_block_end_block(value: string) {
    this._end_block = value;
    return this;
  }

  get getUnitBlock_unit_type(): string {
    return this._unit_type;
  }

  unit_block_unit_type(value: string) {
    this._unit_type = value;
    return this;
  }

  get getUnitBlock_account_identifier(): string {
    return this._account_identifier;
  }

  unit_block_account_identifier(value: string) {
    this._account_identifier = value;
    return this;
  }

  get getUnitBlock_acquisition_date(): string {
    return this._acquisition_date;
  }

  unit_block_acquisition_date(value: string) {
    this._acquisition_date = value;
    return this;
  }

  get getUnitBlock_last_modified_date(): string {
    return this._last_modified_date;
  }

  unit_block_last_modified_date(value: string) {
    this._last_modified_date = value;
    return this;
  }

  get getUnitBlock_year(): string {
    return this._year;
  }

  unit_block_year(value: string) {
    this._year = value;
    return this;
  }
}
