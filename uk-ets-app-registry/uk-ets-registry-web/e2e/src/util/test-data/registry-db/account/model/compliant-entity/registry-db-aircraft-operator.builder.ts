import { RegistryDbAircraftOperator } from './registry-db-aircraft-operator';

export class RegistryDbAircraftOperatorBuilder {
  private _monitoring_plan_expiry_date: string;
  private _monitoring_plan_identifier = '1234567890';
  private _permit_status: string;
  private _first_year = '2021';

  constructor() {}

  build() {
    return new RegistryDbAircraftOperator(this);
  }

  getMonitoring_plan_expiry_date(): string {
    return this._monitoring_plan_expiry_date;
  }

  monitoring_plan_expiry_date(value: string) {
    this._monitoring_plan_expiry_date = value;
    return this;
  }

  getMonitoring_plan_identifier(): string {
    return this._monitoring_plan_identifier;
  }

  monitoring_plan_identifier(value: string) {
    this._monitoring_plan_identifier = value
      ? value
      : this._monitoring_plan_identifier;
    return this;
  }

  getPermit_status(): string {
    return this._permit_status;
  }

  permit_status(value: string) {
    this._permit_status = value;
    return this;
  }

  getFirst_year(): string {
    return this._first_year;
  }

  first_year(value: string) {
    this._first_year = value ? value : this._first_year;
    return this;
  }
}
