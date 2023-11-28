import { RegistryDbAccountHolderRepresentative } from './registry-db-account-holder-representative';

export class RegistryDbAccountHolderRepresentativeBuilder {
  private _first_name: string;
  private _last_name: string;
  private _also_known_as = 'AKA';
  private _birth_date = '01/01/1987';
  private _contact_id: string;
  private _account_holder_id: string;
  private _account_contact_type: string;

  constructor() {}

  build() {
    return new RegistryDbAccountHolderRepresentative(this);
  }

  getFirst_name(): string {
    return this._first_name;
  }

  first_name(value: string) {
    this._first_name = value;
    return this;
  }

  getLast_name(): string {
    return this._last_name;
  }

  last_name(value: string) {
    this._last_name = value;
    return this;
  }

  getAlso_known_as(): string {
    return this._also_known_as;
  }

  also_known_as(value: string) {
    this._also_known_as = value ? value : this._also_known_as;
    return this;
  }

  getBirth_date(): string {
    return this._birth_date;
  }

  birth_date(value: string) {
    this._birth_date = value ? value : this._birth_date;
    return this;
  }

  getContact_id(): string {
    return this._contact_id;
  }

  contact_id(value: string) {
    this._contact_id = value;
    return this;
  }

  getAccount_holder_id(): string {
    return this._account_holder_id;
  }

  account_holder_id(value: string) {
    this._account_holder_id = value;
    return this;
  }

  getAccount_contact_type(): string {
    return this._account_contact_type;
  }

  account_contact_type(value: string) {
    this._account_contact_type = value ? value : this._account_contact_type;
    return this;
  }
}
