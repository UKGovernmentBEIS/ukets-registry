import { RegistryDbInstallationOwnershipBuilder } from './registry-db-installation-ownership.builder';

export class RegistryDbInstallationOwnership {
  id: string;
  account_id: string;
  installation_id: string;
  ownership_date: string;
  permit_identifier: string;
  status: string;

  constructor(builder: RegistryDbInstallationOwnershipBuilder) {
    this.id = builder.getId;
    this.account_id = builder.getAccount_id;
    this.installation_id = builder.getInstallation_id;
    this.ownership_date = builder.getOwnership_date;
    this.permit_identifier = builder.getPermit_identifier;
    this.status = builder.getStatus;
  }
}
