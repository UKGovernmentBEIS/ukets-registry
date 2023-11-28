import { RegistryDbInstallation } from './registry-db-installation';

export class RegistryDbInstallationBuilder {
  private _installation_name = 'Installation Name';
  private _activity_type = 'MANUFACTURE_OF_MINERAL_WOOL';
  private _permit_identifier = '1234567890';
  private _permit_entry_into_force_date = '1986-11-14';
  private _permit_expiry_date: string;
  private _permit_status: string;
  private _first_year = '2021';

  constructor() {}

  build() {
    return new RegistryDbInstallation(this);
  }

  getInstallation_name(): string {
    return this._installation_name;
  }

  installation_name(value: string) {
    this._installation_name = value ? value : this._installation_name;
    return this;
  }

  getActivity_type(): string {
    return this._activity_type;
  }

  activity_type(value: string) {
    this._activity_type = value ? value : this._activity_type;
    return this;
  }

  getPermit_identifier(): string {
    return this._permit_identifier;
  }

  permit_identifier(value: string) {
    this._permit_identifier = value ? value : this._permit_identifier;
    return this;
  }

  getPermit_entry_into_force_date(): string {
    return this._permit_entry_into_force_date;
  }

  permit_entry_into_force_date(value: string) {
    this._permit_entry_into_force_date = value
      ? value
      : this._permit_entry_into_force_date;
    return this;
  }

  getPermit_expiry_date(): string {
    return this._permit_expiry_date;
  }

  permit_expiry_date(value: string) {
    this._permit_expiry_date = value;
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
