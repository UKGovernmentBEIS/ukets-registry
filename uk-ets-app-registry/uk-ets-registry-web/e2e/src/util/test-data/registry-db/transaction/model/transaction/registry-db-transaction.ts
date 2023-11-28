import { RegistryDbTransactionBuilder } from './registry-db-transaction.builder';

export class RegistryDbTransaction {
  id: string;
  identifier: string;
  type: string;
  status: string;
  quantity: string;
  acquiring_account_identifier: string;
  acquiring_account_type: string;
  acquiring_account_registry_code: string;
  acquiring_account_full_identifier: string;
  transferring_account_identifier: string;
  transferring_account_type: string;
  transferring_account_registry_code: string;
  transferring_account_full_identifier: string;
  started: string;
  last_updated: string;
  unit_type: string;
  execution_date: string;
  notification_identifier: string;

  constructor(builder: RegistryDbTransactionBuilder) {
    this.identifier = builder.getIdentifier();
    this.type = builder.getType();
    this.status = builder.getStatus();
    this.quantity = builder.getQuantity();
    this.acquiring_account_identifier = builder.getAcquiring_account_identifier();
    this.acquiring_account_type = builder.getAcquiring_account_type();
    this.acquiring_account_registry_code = builder.getAcquiring_account_registry_code();
    this.acquiring_account_full_identifier = builder.getAcquiring_account_full_identifier();
    this.transferring_account_identifier = builder.getTransferring_account_identifier();
    this.transferring_account_type = builder.getTransferring_account_type();
    this.transferring_account_registry_code = builder.getTransferring_account_registry_code();
    this.transferring_account_full_identifier = builder.getTransferring_account_full_identifier();
    this.started = builder.getStarted();
    this.last_updated = builder.getLast_updated();
    this.unit_type = builder.getUnit_type();
    this.execution_date = builder.getExecution_date();
    this.notification_identifier = builder.getNotification_identifier();
  }
}
