import { DbTransactionLogAccount } from './db-transaction-log-account';

export class DbTransactionLogAccountBuilder {
  private _id: string;
  private _identifier: string;
  private _account_name: string;
  private _commitment_period_code: string;
  private _full_identifier: string;
  private _check_digits: string;
  private _opening_date: string;

  constructor() {}

  build() {
    return new DbTransactionLogAccount(this);
  }

  get getAccount_id(): string {
    return this._id;
  }

  account_id(value: string) {
    this._id = value;
    return this;
  }

  get getAccount_identifier(): string {
    return this._identifier;
  }

  account_identifier(value: string) {
    this._identifier = value;
    return this;
  }

  get getAccount_account_name(): string {
    return this._account_name;
  }

  account_account_name(value: string) {
    this._account_name = value;
    return this;
  }

  get getAccount_commitment_period_code(): string {
    return this._commitment_period_code;
  }

  account_commitment_period_code(value: string) {
    this._commitment_period_code = value;
    return this;
  }

  get getAccount_full_identifier(): string {
    return this._full_identifier;
  }

  account_full_identifier(value: string) {
    this._full_identifier = value;
    return this;
  }

  get getAccount_check_digits(): string {
    return this._check_digits;
  }

  account_check_digits(value: string) {
    this._check_digits = value;
    return this;
  }

  get getAccount_opening_date(): string {
    return this._opening_date;
  }

  account_opening_date(value: string) {
    this._opening_date = value;
    return this;
  }
}
