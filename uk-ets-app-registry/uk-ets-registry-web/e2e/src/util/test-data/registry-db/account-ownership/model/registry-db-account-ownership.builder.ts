import { RegistryDbAccountOwnership } from './registry-db-account-ownership';

export class RegistryDbAccountOwnershipBuilder {
  private id: string;
  private account_id: string;
  private account_holder_id: string;
  private status: string;
  private date_of_ownership: string;

  constructor() {}

  build() {
    return new RegistryDbAccountOwnership(this);
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

  get getAccountHolder_id(): string {
    return this.account_holder_id;
  }

  setAccountHolder_id(value: string) {
    this.account_holder_id = value ? value : this.account_holder_id;
    return this;
  }

  get getStatus(): string {
    return this.status;
  }

  setStatus(value: string) {
    this.status = value ? value : this.status;
    return this;
  }

  get getDateOfOwnership(): string {
    return this.date_of_ownership;
  }

  setDateOfOwnership(value: string) {
    this.date_of_ownership = value ? value : this.date_of_ownership;
    return this;
  }
}
