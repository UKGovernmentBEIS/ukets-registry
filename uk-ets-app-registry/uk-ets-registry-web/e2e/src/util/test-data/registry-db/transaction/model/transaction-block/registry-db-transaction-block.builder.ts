import { RegistryDbTransactionBlock } from './registry-db-transaction-block';

export class RegistryDbTransactionBlockBuilder {
  private _start_block: string;
  private _end_block: string;
  private _unit_type: string;
  private _originating_country_code: string;
  private _original_period: string;
  private _applicable_period: string;
  private _environmental_activity: string;
  private _expiry_date: string;
  private _transaction_id: string;
  private _project_number: string;
  private _project_track: string;
  private _sop: boolean;

  constructor() {}

  build() {
    return new RegistryDbTransactionBlock(this);
  }

  getStart_block(): string {
    return this._start_block;
  }

  start_block(value: string) {
    this._start_block = value;
    return this;
  }

  getEnd_block(): string {
    return this._end_block;
  }

  end_block(value: string) {
    this._end_block = value;
    return this;
  }

  getUnit_type(): string {
    return this._unit_type;
  }

  unit_type(value: string) {
    this._unit_type = value;
    return this;
  }

  getOriginating_country_code(): string {
    return this._originating_country_code;
  }

  originating_country_code(value: string) {
    this._originating_country_code = value;
    return this;
  }

  getOriginal_period(): string {
    return this._original_period;
  }

  original_period(value: string) {
    this._original_period = value;
    return this;
  }

  getApplicable_period(): string {
    return this._applicable_period;
  }

  applicable_period(value: string) {
    this._applicable_period = value;
    return this;
  }

  getEnvironmental_activity(): string {
    return this._environmental_activity;
  }

  environmental_activity(value: string) {
    this._environmental_activity = value;
    return this;
  }

  getExpiry_date(): string {
    return this._expiry_date;
  }

  expiry_date(value: string) {
    this._expiry_date = value;
    return this;
  }

  getTransaction_id(): string {
    return this._transaction_id;
  }

  transaction_id(value: string) {
    this._transaction_id = value;
    return this;
  }

  getProject_number(): string {
    return this._project_number;
  }

  project_number(value: string) {
    this._project_number = value;
    return this;
  }

  getProject_track(): string {
    return this._project_track;
  }

  project_track(value: string) {
    this._project_track = value;
    return this;
  }

  getSop(): boolean {
    return this._sop;
  }

  sop(value: boolean) {
    this._sop = value;
    return this;
  }
}
