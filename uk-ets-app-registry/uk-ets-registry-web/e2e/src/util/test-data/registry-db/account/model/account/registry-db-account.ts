import { RegistryDbRegistryAccountBuilder } from './registry-db-registry-account.builder';

export class RegistryDbAccount {
  id: string;
  identifier: string;
  account_name: string;
  registry_account_type: string;
  kyoto_account_type: string;
  account_status: string;
  opening_date: string;
  request_status: string;
  account_holder_id: string;
  commitment_period_code: string;
  registry_code: string;
  compliant_entity_id: string;
  check_digits: string;
  transfers_outside_tal: boolean;
  approval_second_ar_required: boolean;
  billing_address_same_as_account_holder_address: boolean;
  contact_id: string;
  changed_account_holder_id: string;
  compliance_status: string;
  balance: string;
  full_identifier: string;
  type_label: string;
  unit_type: string;
  single_person_approval_required: boolean;

  constructor(builder: RegistryDbRegistryAccountBuilder) {
    this.identifier = builder.getIdentifier();
    this.account_name = builder.getAccount_name();
    this.registry_account_type = builder.getRegistry_account_type();
    this.kyoto_account_type = builder.getKyoto_account_type();
    this.account_status = builder.getAccount_status();
    this.opening_date = builder.getOpening_date();
    this.request_status = builder.getRequest_Status();
    this.account_holder_id = builder.getAccount_holder_id();
    this.commitment_period_code = builder.getCommitment_period_code();
    this.registry_code = builder.getRegistry_code();
    this.compliant_entity_id = builder.getCompliant_entity_id();
    this.check_digits = builder.getCheck_digits();
    this.transfers_outside_tal = builder.getTransfers_outside_tal();
    this.approval_second_ar_required = builder.getApproval_second_ar_required();
    this.billing_address_same_as_account_holder_address =
      builder.getBilling_address_same_as_account_holder_address();
    this.contact_id = builder.getContact_id();
    this.changed_account_holder_id = builder.getChanged_account_holder_id();
    this.compliance_status = builder.getCompliance_status();
    this.balance = builder.getBalance();
    this.full_identifier = builder.getFull_identifier();
    this.type_label = builder.getType_label();
    this.unit_type = builder.getUnit_Type();
    this.single_person_approval_required =
      builder.getSingle_person_approval_required();
  }
}
