import { DbAllocationYearBuilder } from './registry-db-ets-transactions.builder';

export class DbAllocationYear {
  id: string;
  year: string;
  initial_yearly_cap: string;
  consumed_yearly_cap: string;
  pending_yearly_cap: string;
  allocation_period_id: string;
  entitlement: string;
  allocated: string;
  auctioned: string;

  constructor(builder: DbAllocationYearBuilder) {
    this.id = builder.getId;
    this.year = builder.getYear;
    this.initial_yearly_cap = builder.getInitial_yearly_cap;
    this.consumed_yearly_cap = builder.getConsumed_yearly_cap;
    this.pending_yearly_cap = builder.getPending_yearly_cap;
    this.allocation_period_id = builder.getAllocation_period_id;
    this.entitlement = builder.getEntitlement;
    this.allocated = builder.getAllocated;
    this.auctioned = builder.getAuctioned;
  }
}
