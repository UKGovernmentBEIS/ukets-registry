import { RegistryDbAccountHolder } from './registry-db-account-holder';

export class RegistryDbAccountHolderBuilder {
  private _name = 'Test Account Holder';
  private _birth_date = '01/01/1986';
  private _birth_country = 'UK';
  private _registration_number = 'UK1234567890';
  // private _vat_number = '123-456-789-0';
  // private _justification: string;
  private _type = 'ORGANISATION';
  private _contact_id: string;
  private _no_reg_justification: string;
  private _first_name: string;
  private _last_name: string;

  constructor() {}

  build() {
    return new RegistryDbAccountHolder(this);
  }

  getName(): string {
    return this._name;
  }

  name(value: string) {
    this._name = value ? value : this._name;
    return this;
  }

  getBirth_date(): string {
    return this._birth_date;
  }

  birth_date(value: string) {
    this._birth_date = value ? value : this._birth_date;
    return this;
  }

  getBirth_country(): string {
    return this._birth_country;
  }

  birth_country(value: string) {
    this._birth_country = value ? value : this._birth_country;
    return this;
  }

  getRegistration_number(): string {
    return this._registration_number;
  }

  registration_number(value: string) {
    this._registration_number = value ? value : this._registration_number;
    return this;
  }

  // getVat_number(): string {
  //   return this._vat_number;
  // }

  // vat_number(value: string) {
  //   this._vat_number = value ? value : this._vat_number;
  //   return this;
  // }

   getFirst_name(): string {
     return this._first_name;
  }

  first_name(value: string) {
    this._first_name = value ? value : this._first_name;
    return this;
  }

  getLast_name(): string {
    return this._last_name;
  }

  last_name(value: string) {
    this._last_name = value ? value : this._last_name;
    return this;
  }

  // getJustification(): string {
  //   return this._justification;
  // }

  // justification(value: string) {
  //   this._justification = value ? value : this._justification;
  //   return this;
  // }

  getType(): string {
    return this._type;
  }

  type(value: string) {
    this._type = value ? value : this._type;
    return this;
  }

  getContact_id(): string {
    return this._contact_id;
  }

  contact_id(value: string) {
    this._contact_id = value;
    return this;
  }

  getNo_reg_justification(): string {
    return this._no_reg_justification;
  }

  no_reg_justification(value: string) {
    this._no_reg_justification = value ? value : this._no_reg_justification;
    return this;
  }
}
