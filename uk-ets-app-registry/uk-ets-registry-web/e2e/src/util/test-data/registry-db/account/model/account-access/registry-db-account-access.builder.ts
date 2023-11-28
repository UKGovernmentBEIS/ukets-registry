import { RegistryDbAccountAccess } from './registry-db-account-access';

export class RegistryDbAccountAccessBuilder {
  private _state: string;
  private _account_id: string;
  private _user_id: string;
  private _type: string;
  private _access_right: string;

  constructor() {
    // default constructor values (will be overridden if different values set):
    this._state = 'ACTIVE';
    this._access_right = 'INITIATE_AND_APPROVE';
  }

  build() {
    return new RegistryDbAccountAccess(this);
  }

  getState(): string {
    return this._state;
  }

  state(value: string) {
    this._state = value ? value : this._state;
    return this;
  }

  getAccount_id(): string {
    return this._account_id;
  }

  account_id(value: string) {
    this._account_id = value;
    return this;
  }

  getUser_id(): string {
    return this._user_id;
  }

  user_id(value: string) {
    this._user_id = value;
    return this;
  }

  getType(): string {
    return this._type;
  }

  type(value: string) {
    this._type = value ? value : this._state;
    return this;
  }

  getAccessRight(): string {
    return this._access_right;
  }

  accessRight(value: string) {
    this._access_right = value ? value : this._state;
    return this;
  }
}
