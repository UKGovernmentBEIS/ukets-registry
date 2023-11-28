import { DbAllocationYear } from './registry-db-ets-transactions';

export class DbAllocationYearBuilder {
  private _id = '100000001';
  private _year = '2021';
  private _initial_yearly_cap = '100000';
  private _consumed_yearly_cap = '0';
  private _pending_yearly_cap = '0';
  private _allocation_period_id = '1';
  private _entitlement = '0';
  private _allocated = '0';
  private _auctioned = '0';

  constructor() {}

  build() {
    return new DbAllocationYear(this);
  }

  get getId(): string {
    return this._id;
  }

  id(value: string) {
    this._id = value ? value : this._id;
    return this;
  }

  get getYear(): string {
    return this._year;
  }

  year(value: string) {
    this._year = value ? value : this._year;
    return this;
  }

  get getInitial_yearly_cap(): string {
    return this._initial_yearly_cap;
  }

  initial_yearly_cap(value: string) {
    this._initial_yearly_cap = value ? value : this._initial_yearly_cap;
    return this;
  }

  get getConsumed_yearly_cap(): string {
    return this._consumed_yearly_cap;
  }

  consumed_yearly_cap(value: string) {
    this._consumed_yearly_cap = value ? value : this._consumed_yearly_cap;
    return this;
  }

  get getPending_yearly_cap(): string {
    return this._pending_yearly_cap;
  }

  pending_yearly_cap(value: string) {
    this._pending_yearly_cap = value ? value : this._pending_yearly_cap;
    return this;
  }

  get getAllocation_period_id(): string {
    return this._allocation_period_id;
  }

  allocation_period_id(value: string) {
    this._allocation_period_id = value ? value : this._allocation_period_id;
    return this;
  }

  get getEntitlement(): string {
    return this._entitlement;
  }

  entitlement(value: string) {
    this._entitlement = value ? value : this._entitlement;
    return this;
  }

  get getAllocated(): string {
    return this._allocated;
  }

  allocated(value: string) {
    this._allocated = value ? value : this._allocated;
    return this;
  }

  get getAuctioned(): string {
    return this._auctioned;
  }

  auctioned(value: string) {
    this._auctioned = value ? value : this._auctioned;
    return this;
  }
}
