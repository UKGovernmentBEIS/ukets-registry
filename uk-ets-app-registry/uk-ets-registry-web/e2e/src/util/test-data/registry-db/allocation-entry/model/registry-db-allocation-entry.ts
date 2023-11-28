import { RegistryDbAllocationEntryBuilder } from './registry-db-allocation-entry.builder';

export class RegistryDbAllocationEntry {
  id: string;
  allocation_year_id: string;
  compliant_entity_id: string;
  type: string;
  entitlement: string;
  allocated: string;
  returned: string;
  reversed: string;

  constructor(builder: RegistryDbAllocationEntryBuilder) {
    this.id = builder.getId;
    this.allocation_year_id = builder.getAllocationYearId;
    this.compliant_entity_id = builder.getCompliantEntityId;
    this.type = builder.getType;
    this.entitlement = builder.getEntitlement;
    this.allocated = builder.getAllocated;
    this.returned = builder.getReturned;
    this.reversed = builder.getReversed;
  }
}
