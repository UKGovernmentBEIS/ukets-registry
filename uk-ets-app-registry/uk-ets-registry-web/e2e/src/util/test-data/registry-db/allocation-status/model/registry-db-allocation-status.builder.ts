import { RegistryDbAllocationStatus } from './registry-db-allocation-status';

export class RegistryDbAllocationStatusBuilder {
  private id: string;
  private allocation_year_id: string;
  private compliant_entity_id: string;
  private status: string;

  constructor() {}

  build() {
    return new RegistryDbAllocationStatus(this);
  }

  get getId(): string {
    return this.id;
  }

  setId(value: string) {
    this.id = value ? value : this.id;
    return this;
  }

  get getAllocationYearId(): string {
    return this.allocation_year_id;
  }

  setAllocationYearId(value: string) {
    this.allocation_year_id = value ? value : this.allocation_year_id;
    return this;
  }

  get getCompliantEntityId(): string {
    return this.compliant_entity_id;
  }

  setCompliantEntityId(value: string) {
    this.compliant_entity_id = value ? value : this.compliant_entity_id;
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
