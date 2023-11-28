import { RegistryDbAircraftOperatorBuilder } from './registry-db-aircraft-operator.builder';

export class RegistryDbAircraftOperator {
  compliant_entity_id: string;
  monitoring_plan_expiry_date: string;
  monitoring_plan_identifier: string;
  permit_status: string;
  first_year: string;

  constructor(builder: RegistryDbAircraftOperatorBuilder) {
    this.monitoring_plan_expiry_date = builder.getMonitoring_plan_expiry_date();
    this.monitoring_plan_identifier = builder.getMonitoring_plan_identifier();
    this.permit_status = builder.getPermit_status();
    this.first_year = builder.getFirst_year();
  }
}
