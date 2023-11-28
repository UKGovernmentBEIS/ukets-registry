import { DbEmissionsEntry } from './registry-db-emissions-entry';

export class RegistryDbEmissionsEntryBuilder {
  private _id = '100000001';
  private _filename = '';
  private _compliant_entity_id = '';
  private _year = '';
  private _emissions = '';
  private _upload_date = '';

  constructor() {}

  build() {
    return new DbEmissionsEntry(this);
  }

  get getId(): string {
    return this._id;
  }

  id(value: string) {
    this._id = value ? value : this._id;
    return this;
  }

  get getFilename(): string {
    return this._filename;
  }

  filename(value: string) {
    this._filename = value ? value : this._filename;
    return this;
  }

  get getCompliantEntityId(): string {
    return this._compliant_entity_id;
  }

  compliant_entity_id(value: string) {
    this._compliant_entity_id = value ? value : this._compliant_entity_id;
    return this;
  }

  get getYear(): string {
    return this._year;
  }

  year(value: string) {
    this._year = value ? value : this._year;
    return this;
  }

  get getEmissions(): string {
    return this._emissions;
  }

  emissions(value: string) {
    this._emissions = value ? value : this._emissions;
    return this;
  }

  get getUploadDate(): string {
    return this._upload_date;
  }

  upload_date(value: string) {
    this._upload_date = value ? value : this._upload_date;
    return this;
  }
}
