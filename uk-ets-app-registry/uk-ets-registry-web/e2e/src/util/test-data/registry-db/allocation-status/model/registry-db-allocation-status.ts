import { RegistryDbAllocationStatusBuilder } from './registry-db-allocation-status.builder';

export class RegistryDbAllocationStatus {
  id: string;
  allocation_year_id: string;
  compliant_entity_id: string;
  status: string;

  constructor(builder: RegistryDbAllocationStatusBuilder) {
    this.id = builder.getId;
    this.allocation_year_id = builder.getAllocationYearId;
    this.compliant_entity_id = builder.getCompliantEntityId;
    this.status = builder.getStatus;
  }
}
