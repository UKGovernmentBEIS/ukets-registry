import { RegistryDbTask } from './registry-db-task';

export class RegistryDbTaskBuilder {
  private _account_id: string;
  private _transaction_identifier: string;
  private _claimed_by: string;
  private _status = 'SUBMITTED_NOT_YET_APPROVED';
  private _outcome: string;
  private _type: string;
  private _request_identifier: string;
  private _initiated_by: string;
  private _completed_by: string;
  private _before: string;
  private _after: string;
  private _difference: string;
  private _parent_task_id: string;
  private _initiated_date: string;
  private _claimed_date: string;
  private _completed_date: string;
  private _user_id: string;
  private _file: string;
  private _recipient_account_number: string;

  constructor() {}

  build() {
    return new RegistryDbTask(this);
  }

  getAccount_id(): string {
    return this._account_id;
  }

  account_id(value: string) {
    this._account_id = value;
    return this;
  }

  getTransaction_identifier(): string {
    return this._transaction_identifier;
  }

  transaction_identifier(value: string) {
    this._transaction_identifier = value;
    return this;
  }

  getClaimed_by(): string {
    return this._claimed_by;
  }

  claimed_by(value: string) {
    this._claimed_by = value;
    return this;
  }

  getStatus(): string {
    return this._status;
  }

  status(value: string) {
    this._status = value ? value : this._status;
    return this;
  }

  getOutcome(): string {
    return this._outcome;
  }

  outcome(value: string) {
    this._outcome = value;
    return this;
  }

  getType(): string {
    return this._type;
  }

  type(value: string) {
    this._type = value;
    return this;
  }

  getRequest_identifier(): string {
    return this._request_identifier;
  }

  request_identifier(value: string) {
    this._request_identifier = value;
    return this;
  }

  getInitiated_by(): string {
    return this._initiated_by;
  }

  initiated_by(value: string) {
    this._initiated_by = value;
    return this;
  }

  getCompleted_by(): string {
    return this._completed_by;
  }

  completed_by(value: string) {
    this._completed_by = value;
    return this;
  }

  getBefore(): string {
    return this._before;
  }

  before(value: string) {
    this._before = value;
    return this;
  }

  getAfter(): string {
    return this._after;
  }

  after(value: string) {
    this._after = value;
    return this;
  }

  getDifference(): string {
    return this._difference;
  }

  difference(value: string) {
    this._difference = value;
    return this;
  }

  getParent_task_id(): string {
    return this._parent_task_id;
  }

  parent_task_id(value: string) {
    this._parent_task_id = value;
    return this;
  }

  getInitiated_date(): string {
    return this._initiated_date;
  }

  initiated_date(value: string) {
    this._initiated_date = value;
    return this;
  }

  getClaimed_date(): string {
    return this._claimed_date;
  }

  claimed_date(value: string) {
    this._claimed_date = value;
    return this;
  }

  getCompleted_date(): string {
    return this._completed_date;
  }

  completed_date(value: string) {
    this._completed_date = value;
    return this;
  }

  getUser_id(): string {
    return this._user_id;
  }

  user_id(value: string) {
    this._user_id = value;
    return this;
  }

  getFile(): string {
    return this._file;
  }

  file(value: string) {
    this._file = value;
    return this;
  }

  getRecipient_account_number() {
    return this._recipient_account_number;
  }

  recipient_account_number(value: string) {
    this._recipient_account_number = value;
    return this;
  }
}
