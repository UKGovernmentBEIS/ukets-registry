import { RegistryDbEmissionsEntryBuilder } from './registry-db-emissions-entry.builder';

export class DbEmissionsEntry {
  id: string;
  filename: string;
  compliant_entity_id: string;
  year: string;
  emissions: string;
  upload_date: string;

  constructor(builder: RegistryDbEmissionsEntryBuilder) {
    this.id = builder.getId;
    this.filename = builder.getFilename;
    this.compliant_entity_id = builder.getCompliantEntityId;
    this.year = builder.getYear;
    this.emissions = builder.getEmissions;
    this.upload_date = builder.getUploadDate;
  }
}
