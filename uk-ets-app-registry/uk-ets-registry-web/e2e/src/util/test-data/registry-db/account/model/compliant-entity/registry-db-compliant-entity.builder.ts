import { RegistryDbCompliantEntity } from './registry-db-compliant-entity';

export class RegistryDbCompliantEntityBuilder {
  private _status: string;
  private _account_id: string;
  private _start_year = '2021';
  private _end_year: string;
  private _has_been_compliant: boolean;
  private _regulator = 'SEPA';
  private _changed_regulator: string;

  constructor() {}

  build() {
    return new RegistryDbCompliantEntity(this);
  }

  getStatus(): string {
    return this._status;
  }

  status(value: string) {
    this._status = value;
    return this;
  }

  getAccount_id(): string {
    return this._account_id;
  }

  account_id(value: string) {
    this._account_id = value;
    return this;
  }

  getStart_year(): string {
    return this._start_year;
  }

  start_year(value: string) {
    this._start_year = value ? value : this._start_year;
    return this;
  }

  getEnd_year(): string {
    return this._end_year;
  }

  end_year(value: string) {
    this._end_year = value;
    return this;
  }

  getHas_been_compliant(): boolean {
    return this._has_been_compliant;
  }

  has_been_compliant(value: boolean) {
    this._has_been_compliant = value;
    return this;
  }

  getRegulator(): string {
    return this._regulator;
  }

  regulator(value: string) {
    this._regulator = value ? value : this._regulator;
    return this;
  }

  getChanged_regulator(): string {
    return this._changed_regulator;
  }

  changed_regulator(value: string) {
    this._changed_regulator = value;
    return this;
  }
}
