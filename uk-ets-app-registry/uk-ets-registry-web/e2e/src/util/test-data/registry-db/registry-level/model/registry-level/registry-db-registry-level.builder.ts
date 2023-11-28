import { RegistryDbRegisterLevel } from './registry-db-register-level';

export class RegistryDbRegistryLevelBuilder {
  private _type: string;
  private _unit_type: string;
  private _commitment_period: string;
  private _environmental_activity: string;
  private _initial: string;
  private _consumed: string;
  private _pending: string;

  constructor() {}

  build() {
    return new RegistryDbRegisterLevel(this);
  }

  getType(): string {
    return this._type;
  }

  type(value: string) {
    this._type = value;
    return this;
  }

  getUnitType(): string {
    return this._unit_type;
  }

  unitType(value: string) {
    this._unit_type = value;
    return this;
  }

  getCommitmentPeriod(): string {
    return this._commitment_period;
  }

  commitmentPeriod(value: string) {
    this._commitment_period = value;
    return this;
  }

  getEnvironmentalActivity(): string {
    return this._environmental_activity;
  }

  environmentalActivity(value: string) {
    this._environmental_activity = value;
    return this;
  }

  getInitial(): string {
    return this._initial;
  }

  initial(value: string) {
    this._initial = value;
    return this;
  }

  getConsumed(): string {
    return this._consumed;
  }

  consumed(value: string) {
    this._consumed = value;
    return this;
  }

  getPending(): string {
    return this._pending;
  }

  pending(value: string) {
    this._pending = value;
    return this;
  }
}
