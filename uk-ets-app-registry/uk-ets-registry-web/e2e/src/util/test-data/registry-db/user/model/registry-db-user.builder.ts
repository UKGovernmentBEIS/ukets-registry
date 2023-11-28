import { RegistryDbUser } from './registry-db-user';

export class RegistryDbUserBuilder {
  private _urid: string;
  private _iam_identifier: string;
  private _enrolment_key: string;
  private _enrolment_key_date: string;
  private _state: string;
  private _first_name: string;
  private _last_name: string;
  private _disclosed_name = 'Disclosed name example';

  constructor() {}

  urid(urid: string) {
    this._urid = urid;
    return this;
  }

  iam_identifier(iam_identifier: string) {
    this._iam_identifier = iam_identifier;
    return this;
  }

  enrolment_key(enrolment_key: string) {
    this._enrolment_key = enrolment_key;
    return this;
  }

  enrolment_key_date(enrolment_key_date: string) {
    this._enrolment_key_date = enrolment_key_date;
    return this;
  }

  state(state: string) {
    this._state = state;
    return this;
  }

  disclosed_name(disclosed_name: string) {
    this._disclosed_name = disclosed_name;
    return this;
  }

  first_name(first_name: string) {
    this._first_name = first_name;
    return this;
  }

  last_name(last_name: string) {
    this._last_name = last_name;
    return this;
  }

  build() {
    return new RegistryDbUser(this);
  }

  get get_urid(): string {
    return this._urid;
  }

  get get_iam_identifier(): string {
    return this._iam_identifier;
  }

  get get_enrolment_key(): string {
    return this._enrolment_key;
  }

  get get_enrolment_key_date(): string {
    return this._enrolment_key_date;
  }

  get get_state(): string {
    return this._state;
  }

  get get_first_name(): string {
    return this._first_name;
  }

  get get_disclosed_name(): string {
    return this._disclosed_name;
  }

  get get_last_name(): string {
    return this._last_name;
  }
}
