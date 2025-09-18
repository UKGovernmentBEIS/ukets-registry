import { UkDate } from '../uk-date';

export enum Regulator {
  EA = 'EA',
  NRW = 'NRW',
  SEPA = 'SEPA',
  DAERA = 'DAERA',
  OPRED = 'OPRED',
}

export enum OperatorType {
  INSTALLATION = 'INSTALLATION',
  INSTALLATION_TRANSFER = 'INSTALLATION_TRANSFER',
  AIRCRAFT_OPERATOR = 'AIRCRAFT_OPERATOR',
  MARITIME_OPERATOR = 'MARITIME_OPERATOR',
}

export const operatorTypeMap: Record<OperatorType, string> = {
  [OperatorType.INSTALLATION]: 'Installation',
  [OperatorType.INSTALLATION_TRANSFER]: 'Installation transfer',
  [OperatorType.AIRCRAFT_OPERATOR]: 'Aircraft Operator',
  [OperatorType.MARITIME_OPERATOR]: 'Maritime Operator',
};

export enum InstallationActivityType {
  COMBUSTION_OF_FUELS = 'Combustion of fuels',
  REFINING_OF_MINERAL_OIL = 'Refining of mineral oil',
  PRODUCTION_OF_COKE = 'Production of coke',
  METAL_ORE_ROASTING_OR_SINTERING = 'Metal ore (including sulphide ore) roasting or sintering, including palletisation',
  PRODUCTION_OF_PIG_IRON_OR_STEEL = 'Production of pig iron or steel',
  PRODUCTION_OR_PROCESSING_OF_FERROUS_METALS = 'Production or processing of ferrous metals (including ferro-alloys)',
  PRODUCTION_OF_PRIMARY_ALUMINIUM = 'Production of primary aluminium',
  PRODUCTION_OF_SECONDARY_ALUMINIUM = 'Production of secondary aluminium',
  PRODUCTION_OR_PROCESSING_OF_NON_FERROUS_METALS = 'Production or processing of non-ferrous metals',
  PRODUCTION_OF_CEMENT_CLINKER = 'Production of cement clinker in rotary kilns',
  PRODUCTION_OF_LIME_OR_CALCINATION_OF_DOLOMITE_MAGNESITE = 'Production of lime or calcination of dolomite or magnesite',
  MANUFACTURE_OF_GLASS = 'Manufacture of glass including glass fibre',
  MANUFACTURE_OF_CERAMICS = 'Manufacture of ceramic products by firing',
  MANUFACTURE_OF_MINERAL_WOOL = 'Manufacture of mineral wool insulation material',
  PRODUCTION_OR_PROCESSING_OF_GYPSUM_OR_PLASTERBOARD = 'Drying or calcination of gypsum or production of plaster boards ' +
    'and other gypsum products',
  PRODUCTION_OF_PULP = 'Production of pulp from timber or other fibrous materials',
  PRODUCTION_OF_PAPER_OR_CARDBOARD = 'Production of paper or cardboard',
  PRODUCTION_OF_CARBON_BLACK = 'Production of carbon black involving the carbonisation of organic substances',
  PRODUCTION_OF_NITRIC_ACID = 'Production of nitric acid',
  PRODUCTION_OF_ADIPIC_ACID = 'Production of adipic acid',
  PRODUCTION_OF_GLYOXAL_AND_GLYOXYLIC_ACID = 'Production of glyoxal and glyoxylic acid',
  PRODUCTION_OF_AMMONIA = 'Production of ammonia',
  PRODUCTION_OF_BULK_CHEMICALS = 'Production of bulk organic chemicals',
  PRODUCTION_OF_HYDROGEN_AND_SYNTHESIS_GAS = 'Production of hydrogen (H2) and synthesis gas',
  PRODUCTION_OF_SODA_ASH_AND_SODIUM_BICARBONATE = 'Production of soda ash (Na2CO3) and sodium bicarbonate (NaHCO3)',
  CAPTURE_OF_GREENHOUSE_GASES = 'Capture of greenhouse gases from other installations',
  TRANSPORT_OF_GREENHOUSE_GASES_UNDER_DIRECTIVE_2009_31_EC = 'Transport of greenhouse gases by pipelines',
  STORAGE_OF_GREENHOUSE_GASES_UNDER_DIRECTIVE_2009_31_EC = 'Geological storage of greenhouse gases',
}

export interface Operator {
  type: OperatorType;
  identifier?: number;
  regulator: Regulator;
  changedRegulator?: Regulator;
  firstYear: string;
  lastYear?: string;
  lastYearChanged?: boolean;
  emitterId: string;
}

export interface Installation extends Operator {
  type: OperatorType.INSTALLATION;
  name: string;
  activityType: InstallationActivityType;
  permit: {
    id: string;
    date?: UkDate;
    permitIdUnchanged?: boolean;
  };
}

export interface InstallationTransfer extends Operator {
  type: OperatorType.INSTALLATION_TRANSFER;
  name: string;
  permit: {
    id: string;
    permitIdUnchanged?: boolean;
  };
  acquiringAccountHolderIdentifier: number;
}

export interface AircraftOperator extends Operator {
  type: OperatorType.AIRCRAFT_OPERATOR;
  monitoringPlan: {
    id: string;
  };
}

export interface MaritimeOperator extends Operator {
  type: OperatorType.MARITIME_OPERATOR;
  monitoringPlan: {
    id: string;
  };
  imo: string;
}

export interface InstallationSearchResult {
  identifier: number;
  installationName: string;
  permitIdentifier: string;
  emitterId: string;
}
