import { RegistryDbContact } from './registry-db-contact';

export class RegistryDbContactBuilder {
  private _line_1 = 'Test address 7';
  private _line_2 = 'Second address line';
  private _line_3 = 'Third address line';
  private _post_code = '12345';
  private _city = 'London';
  private _country = 'UK';
  private _phone_number_1 = '1434634996';
  private _phone_number_1_country = 'UK (44)';
  private _phone_number_2 = '1434634997';
  private _phone_number_2_country = 'UK (44)';
  private _email_address = 'dont@care.com';

  constructor() {}

  build() {
    return new RegistryDbContact(this);
  }

  get getLine_1(): string {
    return this._line_1;
  }

  line_1(value: string) {
    this._line_1 = value ? value : this._line_1;
    return this;
  }

  get getLine_2(): string {
    return this._line_2;
  }

  line_2(value: string) {
    this._line_2 = value ? value : this._line_2;
    return this;
  }

  get getLine_3(): string {
    return this._line_3;
  }

  line_3(value: string) {
    this._line_3 = value ? value : this._line_3;
    return this;
  }

  get getPost_code(): string {
    return this._post_code;
  }

  post_code(value: string) {
    this._post_code = value ? value : this._post_code;
    return this;
  }

  get getCity(): string {
    return this._city;
  }

  city(value: string) {
    this._city = value ? value : this._city;
    return this;
  }

  get getCountry(): string {
    return this._country;
  }

  country(value: string) {
    this._country = value ? value : this._country;
    return this;
  }

  get getPhone_number_1(): string {
    return this._phone_number_1;
  }

  phone_number_1(value: string) {
    this._phone_number_1 = value ? value : this._phone_number_1;
    return this;
  }

  get getPhone_number_1_country(): string {
    return this._phone_number_1_country;
  }

  phone_number_1_country(value: string) {
    this._phone_number_1_country = value ? value : this._phone_number_1_country;
    return this;
  }

  get getPhone_number_2(): string {
    return this._phone_number_2;
  }

  phone_number_2(value: string) {
    this._phone_number_2 = value ? value : this._phone_number_2;
    return this;
  }

  get getPhone_number_2_country(): string {
    return this._phone_number_2_country;
  }

  phone_number_2_country(value: string) {
    this._phone_number_2_country = value ? value : this._phone_number_2_country;
    return this;
  }

  get getEmail_address(): string {
    return this._email_address;
  }

  email_address(value: string) {
    this._email_address = value ? value : this._email_address;
    return this;
  }
}
