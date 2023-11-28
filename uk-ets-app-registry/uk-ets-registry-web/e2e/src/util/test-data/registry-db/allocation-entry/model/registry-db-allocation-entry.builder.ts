import { RegistryDbAllocationEntry } from './registry-db-allocation-entry';

export class RegistryDbAllocationEntryBuilder {
  private id: string;
  private allocation_year_id: string;
  private compliant_entity_id;
  private type: string;
  private entitlement: string;
  private allocated: string;
  private returned: string;
  private reversed: string;

  constructor() {}

  build() {
    return new RegistryDbAllocationEntry(this);
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

  get getType(): string {
    return this.type;
  }

  setType(value: string) {
    this.type = value ? value : this.type;
    return this;
  }

  get getEntitlement(): string {
    return this.entitlement;
  }

  setEntitlement(value: string) {
    this.entitlement = value ? value : this.entitlement;
    return this;
  }

  get getAllocated(): string {
    return this.allocated;
  }

  setAllocated(value: string) {
    this.allocated = value ? value : this.allocated;
    return this;
  }

  get getReturned(): string {
    return this.returned;
  }

  setReturned(value: string) {
    this.returned = value ? value : this.returned;
    return this;
  }

  get getReversed(): string {
    return this.reversed;
  }

  setReversed(value: string) {
    this.reversed = value ? value : this.reversed;
    return this;
  }
}
