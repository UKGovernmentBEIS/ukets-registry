import { RegistryDbInstallationBuilder } from './registry-db-installation.builder';

export class RegistryDbInstallation {
  compliant_entity_id: string;
  installation_name: string;
  activity_type: string;
  permit_identifier: string;
  permit_entry_into_force_date: string;
  permit_expiry_date: string;
  permit_status: string;
  first_year: string;

  constructor(builder: RegistryDbInstallationBuilder) {
    this.installation_name = builder.getInstallation_name();
    this.activity_type = builder.getActivity_type();
    this.permit_identifier = builder.getPermit_identifier();
    this.permit_entry_into_force_date = builder.getPermit_entry_into_force_date();
    this.permit_expiry_date = builder.getPermit_expiry_date();
    this.permit_status = builder.getPermit_status();
    this.first_year = builder.getFirst_year();
  }
}
