import { RegistryDbAllocationPhase } from './registry-db-allocation-phase';

export class RegistryDbAllocationPhaseBuilder {
  private _id = '1';
  private _code = '1';
  private _initial_phase_cap = '100000';
  private _consumed_phase_cap = '0';
  private _pending_phase_cap = '0';

  constructor() {}

  build() {
    return new RegistryDbAllocationPhase(this);
  }

  get getId(): string {
    return this._id;
  }

  id(value: string) {
    this._id = value ? value : this._id;
    return this;
  }

  get getCode(): string {
    return this._code;
  }

  code(value: string) {
    this._code = value ? value : this._code;
    return this;
  }

  get getInitialPhaseCap(): string {
    return this._initial_phase_cap;
  }

  initial_phase_cap(value: string) {
    this._initial_phase_cap = value ? value : this._initial_phase_cap;
    return this;
  }

  get getConsumedPhaseCap(): string {
    return this._consumed_phase_cap;
  }

  consumed_phase_cap(value: string) {
    this._consumed_phase_cap = value ? value : this._consumed_phase_cap;
    return this;
  }

  get getPendingPhaseCap(): string {
    return this._pending_phase_cap;
  }

  pending_phase_cap(value: string) {
    this._pending_phase_cap = value ? value : this._pending_phase_cap;
    return this;
  }
}
