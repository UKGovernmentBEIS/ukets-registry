import { RegistryDbInstallationOwnership } from './registry-db-installation-ownership';

export class RegistryDbInstallationOwnershipBuilder {
  private id: string;
  private account_id: string;
  private installation_id: string;
  private ownership_date: string;
  private permit_identifier: string;
  private status: string;

  constructor() {}

  build() {
    return new RegistryDbInstallationOwnership(this);
  }

  get getId(): string {
    return this.id;
  }

  setId(value: string) {
    this.id = value ? value : this.id;
    return this;
  }

  get getAccount_id(): string {
    return this.account_id;
  }

  setAccountId(value: string) {
    this.account_id = value ? value : this.account_id;
    return this;
  }

  get getInstallation_id(): string {
    return this.installation_id;
  }

  setInstallation_id(value: string) {
    this.installation_id = value ? value : this.installation_id;
    return this;
  }

  get getOwnership_date(): string {
    return this.ownership_date;
  }

  setOwnership_date(value: string) {
    this.ownership_date = value ? value : this.ownership_date;
    return this;
  }

  get getPermit_identifier(): string {
    return this.permit_identifier;
  }

  setPermit_identifier(value: string) {
    this.permit_identifier = value ? value : this.permit_identifier;
    return this;
  }

  get getStatus(): string {
    return this.status;
  }

  setStatus(value: string) {
    this.status = value ? value : this.status;
    return this;
  }
}
