import { RegistryDbTrustedAccount } from './registry-db-trusted-account';

export class RegistryDbTrustedAccountBuilder {
  private _account_id: string;
  private _trusted_account_full_identifier: string;
  private _status: string;
  private _description: string;
  private _activation_date: string;

  constructor() {}

  build() {
    return new RegistryDbTrustedAccount(this);
  }

  get getAccount_id(): string {
    return this._account_id;
  }

  account_id(value: string) {
    this._account_id = value;
    return this;
  }

  get getTrusted_account_full_identifier(): string {
    return this._trusted_account_full_identifier;
  }

  trusted_account_full_identifier(value: string) {
    this._trusted_account_full_identifier = value;
    return this;
  }

  get getStatus(): string {
    return this._status;
  }

  status(value: string) {
    this._status = value;
    return this;
  }

  get getDescription(): string {
    return this._description;
  }

  description(value: string) {
    this._description = value;
    return this;
  }

  get getActivation_date(): string {
    return this._activation_date;
  }

  activation_date(value: string) {
    this._activation_date = value;
    return this;
  }
}
