import { RegistryDbTransaction } from './registry-db-transaction';

export class RegistryDbTransactionBuilder {
  private _identifier: string;
  private _type: string;
  private _status: string;
  private _quantity: string;
  private _acquiring_account_identifier: string;
  private _acquiring_account_type: string;
  private _acquiring_account_registry_code: string;
  private _acquiring_account_full_identifier: string;
  private _transferring_account_identifier: string;
  private _transferring_account_type: string;
  private _transferring_account_registry_code: string;
  private _transferring_account_full_identifier: string;
  private _started: string;
  private _last_updated: string;
  private _unit_type: string;
  private _execution_date: string;
  private _notification_identifier: string;

  constructor() {}

  build() {
    return new RegistryDbTransaction(this);
  }

  getIdentifier(): string {
    return this._identifier;
  }

  identifier(value: string) {
    this._identifier = value;
    return this;
  }

  getType(): string {
    return this._type;
  }

  type(value: string) {
    this._type = value;
    return this;
  }

  getStatus(): string {
    return this._status;
  }

  status(value: string) {
    this._status = value;
    return this;
  }

  getQuantity(): string {
    return this._quantity;
  }

  quantity(value: string) {
    this._quantity = value;
    return this;
  }

  getAcquiring_account_identifier(): string {
    return this._acquiring_account_identifier;
  }

  acquiring_account_identifier(value: string) {
    this._acquiring_account_identifier = value;
    return this;
  }

  getAcquiring_account_type(): string {
    return this._acquiring_account_type;
  }

  acquiring_account_type(value: string) {
    this._acquiring_account_type = value;
    return this;
  }

  getAcquiring_account_registry_code(): string {
    return this._acquiring_account_registry_code;
  }

  acquiring_account_registry_code(value: string) {
    this._acquiring_account_registry_code = value;
    return this;
  }

  getAcquiring_account_full_identifier(): string {
    return this._acquiring_account_full_identifier;
  }

  acquiring_account_full_identifier(value: string) {
    this._acquiring_account_full_identifier = value;
    return this;
  }

  getTransferring_account_identifier(): string {
    return this._transferring_account_identifier;
  }

  transferring_account_identifier(value: string) {
    this._transferring_account_identifier = value;
    return this;
  }

  getTransferring_account_type(): string {
    return this._transferring_account_type;
  }

  transferring_account_type(value: string) {
    this._transferring_account_type = value;
    return this;
  }

  getTransferring_account_registry_code(): string {
    return this._transferring_account_registry_code;
  }

  transferring_account_registry_code(value: string) {
    this._transferring_account_registry_code = value;
    return this;
  }

  getTransferring_account_full_identifier(): string {
    return this._transferring_account_full_identifier;
  }

  transferring_account_full_identifier(value: string) {
    this._transferring_account_full_identifier = value;
    return this;
  }

  getStarted(): string {
    return this._started;
  }

  started(value: string) {
    this._started = value;
    return this;
  }

  getLast_updated(): string {
    return this._last_updated;
  }

  last_updated(value: string) {
    this._last_updated = value;
    return this;
  }

  getUnit_type(): string {
    return this._unit_type;
  }

  unit_type(value: string) {
    this._unit_type = value;
    return this;
  }

  getExecution_date(): string {
    return this._execution_date;
  }

  execution_date(value: string) {
    this._execution_date = value;
    return this;
  }

  getNotification_identifier(): string {
    return this._notification_identifier;
  }

  notification_identifier(value: string) {
    this._notification_identifier = value;
    return this;
  }
}
