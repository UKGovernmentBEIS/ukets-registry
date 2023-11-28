import { RegistryDbAccount } from './registry-db-account';

export class RegistryDbRegistryAccountBuilder {
  private _identifier: string;
  private _account_name: string;
  private _registry_account_type: string;
  private _kyoto_account_type: string;
  private _account_status = 'OPEN';
  private _opening_date: string;
  private _request_status = 'ACTIVE';
  private _account_holder_id: string;
  private _commitment_period_code: string;
  private _registry_code: string;
  private _compliant_entity_id: string;
  private _check_digits: string;
  private _transfers_outside_tal = true;
  private _approval_second_ar_required = false;
  private _billing_address_same_as_account_holder_address: boolean;
  private _contact_id: string;
  private _changed_account_holder_id: string;
  private _compliance_status: string;
  private _balance: string;
  private _unit_type: string;
  private _full_identifier: string;
  private _type_label: string;
  private _account_contact_type: string;
  private _single_person_approval_required: boolean;

  constructor() {}

  build() {
    this._account_name = `${this.getType_label()} ${String(
      this.getIdentifier()
    )}`;
    return new RegistryDbAccount(this);
  }

  getIdentifier(): string {
    return this._identifier;
  }

  identifier(value: string) {
    this._identifier = value;
    return this;
  }

  getAccount_name(): string {
    return this._account_name;
  }

  account_name(value: string) {
    this._account_name = value ? value : this._account_name;
    return this;
  }

  getRegistry_account_type(): string {
    return this._registry_account_type;
  }

  registry_account_type(value: string) {
    this._registry_account_type = value;
    return this;
  }

  getKyoto_account_type(): string {
    return this._kyoto_account_type;
  }

  kyoto_account_type(value: string) {
    this._kyoto_account_type = value;
    return this;
  }

  getAccount_status(): string {
    return this._account_status;
  }

  account_status(value: string) {
    this._account_status = value ? value : this._account_status;
    return this;
  }

  getOpening_date(): string {
    return this._opening_date;
  }

  opening_date(value: string) {
    this._opening_date = value;
    return this;
  }

  getRequest_Status(): string {
    return this._request_status;
  }

  request_status(value: string) {
    this._request_status = value ? value : this._request_status;
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
    this._account_contact_type = value;
    return this;
  }

  getCommitment_period_code(): string {
    return this._commitment_period_code;
  }

  commitment_period_code(value: string) {
    this._commitment_period_code = value ? value : this._commitment_period_code;
    return this;
  }

  getRegistry_code(): string {
    return this._registry_code;
  }

  registry_code(value: string) {
    this._registry_code = value;
    return this;
  }

  getCompliant_entity_id(): string {
    return this._compliant_entity_id;
  }

  compliant_entity_id(value: string) {
    this._compliant_entity_id = value;
    return this;
  }

  getCheck_digits(): string {
    return this._check_digits;
  }

  check_digits(value: string) {
    this._check_digits = value;
    return this;
  }

  getTransfers_outside_tal(): boolean {
    return this._transfers_outside_tal;
  }

  transfers_outside_tal(value: boolean) {
    if (value === null || value === undefined) {
      console.log(
        `For transfers_outside_tal, the default value '${this._transfers_outside_tal}' will be used.`
      );
    } else {
      this._transfers_outside_tal = value;
    }
    return this;
  }

  getApproval_second_ar_required(): boolean {
    return this._approval_second_ar_required;
  }

  approval_second_ar_required(value: boolean) {
    if (value === null || value === undefined) {
      console.log(
        `For approval_second_ar_required, the default value '${this.approval_second_ar_required}' will be used.`
      );
    } else {
      this._approval_second_ar_required = value;
    }
    return this;
  }

  getBilling_address_same_as_account_holder_address(): boolean {
    return this._billing_address_same_as_account_holder_address;
  }

  billing_address_same_as_account_holder_address(value: boolean) {
    this._billing_address_same_as_account_holder_address = value
      ? value
      : this._billing_address_same_as_account_holder_address;
    return this;
  }

  getContact_id(): string {
    return this._contact_id;
  }

  contact_id(value: string) {
    this._contact_id = value;
    return this;
  }

  getChanged_account_holder_id(): string {
    return this._changed_account_holder_id;
  }

  changed_account_holder_id(value: string) {
    this._changed_account_holder_id = value;
    return this;
  }

  getCompliance_status(): string {
    return this._compliance_status;
  }

  compliance_status(value: string) {
    this._compliance_status = value;
    return this;
  }

  getBalance(): string {
    return this._balance;
  }

  balance(value: string) {
    this._balance = value;
    return this;
  }

  getFull_identifier(): string {
    return this._full_identifier;
  }

  full_identifier(value: string) {
    this._full_identifier = value;
    return this;
  }

  getType_label(): string {
    return this._type_label;
  }

  type_label(value: string) {
    this._type_label = value;
    return this;
  }

  getUnit_Type(): string {
    return this._unit_type;
  }

  unit_type(value: string) {
    this._unit_type = value;
    return this;
  }

  getSingle_person_approval_required(): boolean {
    return this._single_person_approval_required;
  }

  single_person_approval_required(value: boolean) {
    this._single_person_approval_required = value;
    return this;
  }
}
