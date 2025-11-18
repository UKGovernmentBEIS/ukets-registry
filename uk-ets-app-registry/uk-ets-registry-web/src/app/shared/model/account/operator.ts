import { UkDate } from '../uk-date';
import { SelectableOption } from '@shared/form-controls/uk-select-input/uk-select.model';

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
  activityTypes: InstallationActivityType[];
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

export interface DropdownGroup {
  header: string;
  options: MultiCheckBox[];
}

export interface MultiCheckBox extends SelectableOption {
  hint?: string;
}

export function getEntriesValues(keys: string[]): string {
  const labels = keys.map((key) => {
    for (const group of groupedStatusOptions) {
      const match = group.options.find((opt) => opt.value === key);
      if (match) return match.label;
    }
    return '';
  });

  return labels.filter((label) => label).join('; ');
}

export const groupedStatusOptions: DropdownGroup[] = [
  {
    header: 'Carbon capture and storage',
    options: [
      {
        value: 'CAPTURE_OF_GREENHOUSE_GASES',
        label: 'Capture of greenhouse gases from other installations',
        hint: 'Capture is transportation and geological storageat a permitted site',
      },
      {
        value: 'STORAGE_OF_GREENHOUSE_GASES_UNDER_DIRECTIVE_2009_31_EC',
        label:
          'Storage of greenhouse gases under Directive 2009/31/EC (Carbon dioxide)',
      },
      {
        value: 'TRANSPORT_OF_GREENHOUSE_GASES_UNDER_DIRECTIVE_2009_31_EC',
        label:
          'Transport of greenhouse gases under Directive 2009/31/EC (Carbon dioxide)',
      },
    ],
  },
  {
    header: 'Chemicals',
    options: [
      {
        value: 'PRODUCTION_OF_ADIPIC_ACID',
        label: 'Adipic acid production (Carbon dioxide and nitrus oxide)',
      },
      {
        value: 'PRODUCTION_OF_AMMONIA',
        label: 'Ammonia production (Carbon dioxide)',
      },
      {
        value: 'PRODUCTION_OF_BULK_CHEMICALS',
        label: 'Bulk organic chemical production (Carbon dioxide)',
        hint: 'By cracking, reforming, partial or full oxidation or similar processes. At installations with a production capacity exceeding 100 tones per day.',
      },
      {
        value: 'PRODUCTION_OF_CARBON_BLACK',
        label: 'Carbon black production (Carbon dioxide)',
        hint: 'Involving the carbonisation of organic substances such as oils, tars, cracker and distillation residues. At installations with a total rated thermal input exceeding 20MW.',
      },
      {
        value: 'PRODUCTION_OF_GLYOXAL_AND_GLYOXYLIC_ACID',
        label:
          'Glyoxal and glyoxylic acid production (Carbon dioxide and nitrus oxide)',
      },
      {
        value: 'PRODUCTION_OF_HYDROGEN_AND_SYNTHESIS_GAS',
        label: 'Hydrogen and synthesis gas production (Carbon dioxide)',
        hint: 'By reforming or partial oxidation. At installations with a production capacity exceeding 25 tones per day.',
      },
      {
        value: 'PRODUCTION_OF_NITRIC_ACID',
        label: 'Nitric acid production (Carbon dioxide and nitrus oxide)',
      },
      {
        value: 'PRODUCTION_OF_SODA_ASH_AND_SODIUM_BICARBONATE',
        label:
          'Soda ash (Na2CO3) and sodium biocarbonate (NaHCO3) production (Carbon dioxide)',
      },
    ],
  },
  {
    header: 'Combastion',
    options: [
      {
        value: 'COMBUSTION_OF_FUELS',
        label: 'Combustion (Carbon dioxide)',
        hint: 'At installations with a total rated thermal input exceeding 20MW. Installations excluded are those for the incineration of municipal or hazardous waste.',
      },
      {
        value: 'UPSTREAM',
        label: 'Upstream GHG Removal (Carbon dioxide)',
        hint: 'If undertaking upstream GHG removal of constituent greenhouse gases from petroleum (whether by a chemical or physical process) at an upstream site that is vented without a combustion process, this box must be selected.',
      },
    ],
  },
  {
    header: 'Glass and mineral wool',
    options: [
      {
        value: 'MANUFACTURE_OF_GLASS',
        label: 'Glass manufacturing (Carbon dioxide)',
        hint: 'Including glass fibre. At installations with a melting capacity exceeding 20 tones per day.',
      },
      {
        value: 'MANUFACTURE_OF_MINERAL_WOOL',
        label: 'Mineral wool manufacturing (Carbon dioxide)',
        hint: 'At installations manufacturing insulation material using glass, rock or slag with a melting capacity exceeding 20 tonnes per day',
      },
    ],
  },
  {
    header: 'Metals',
    options: [
      {
        value: 'PRODUCTION_OF_COKE',
        label: 'Coke production (Carbon dioxide)',
      },
      {
        value: 'PRODUCTION_OR_PROCESSING_OF_FERROUS_METALS',
        label: 'Ferrous metals production or processing (Carbon dioxide)',
        hint: 'Including ferroalloys. At installations with a total rated thermal input exceeding 20MW. Processing includes rolling mills, re-heaters, annealing furnaces, smitheries, foundries, coating and pickling.',
      },
      {
        value: 'METAL_ORE_ROASTING_OR_SINTERING',
        label: 'Metal ore roasting or sintering (Carbon dioxide)',
        hint: 'Including sulphide ore, and pelletisation of the ores',
      },
      {
        value: 'PRODUCTION_OR_PROCESSING_OF_NON_FERROUS_METALS',
        label: 'Non-ferrous metals production or processing (Carbon dioxide)',
        hint: 'Including production of alloys, refining and foundry casting. At installations with a total rated thermal input exceeding 20MW, and including any fuels used as reducing agents.',
      },
      {
        value: 'PRODUCTION_OF_PIG_IRON_OR_STEEL',
        label: 'Pig iron or steel production (Carbon dioxide)',
        hint: 'Including continuous casting. Primary or secondary fusion, at a capacity exceeding 2,5 tonnes per hour.',
      },
      {
        value: 'PRODUCTION_OF_PRIMARY_ALUMINIUM',
        label:
          'Primary aluminium production (Carbon dioxide and perfuorocarbons)',
      },
      {
        value: 'PRODUCTION_OF_SECONDARY_ALUMINIUM',
        label: 'Secondary aluminium production (Carbon dioxide)',
        hint: ' At installations with a total rated thermal input exceeding 20MW',
      },
    ],
  },
  {
    header: 'Minerals',
    options: [
      {
        value: 'PRODUCTION_OF_CEMENT_CLINKER',
        label: 'Cement clinker production (Carbon dioxide)',
        hint: 'In rotary kilns with a production capacity exceeding 500 tonnes per day, or in other furnaces with a production capacity exceeding 50 tonnes per day',
      },
      {
        value: 'MANUFACTURE_OF_CERAMICS',
        label: 'Ceramics manufacturing (Carbon dioxide)',
        hint: 'With a production capacity exceeding 75 tones per day of products manufactured by firing, including roofing tiles, bricks, refractory bricks, tiles, stoneware and porcelain',
      },
      {
        value: 'PRODUCTION_OR_PROCESSING_OF_GYPSUM_OR_PLASTERBOARD',
        label:
          'Gypsum or plasterboard production or processing (Carbon dioxide)',
        hint: 'Including drying or calcination of gypsum and other gypsum products. At installations with a total rated thermal input exceeding 20MW.',
      },
      {
        value: 'PRODUCTION_OF_LIME_OR_CALCINATION_OF_DOLOMITE_MAGNESITE',
        label: 'Lime or calcination of dolomite or magnesite (Carbon dioxide)',
        hint: 'In rotary kilns or other furnaces with a production capacity exceeding 50 tonnes per day',
      },
    ],
  },
  {
    header: 'Pulp and paper',
    options: [
      {
        value: 'PRODUCTION_OF_PAPER_OR_CARDBOARD',
        label: 'Paper or cardboard production (Carbon dioxide)',
        hint: 'At installations with a production capacity exceeding 20 tonnes per day',
      },
      {
        value: 'PRODUCTION_OF_PULP',
        label: 'Pulp production (Carbon dioxide)',
        hint: 'Pulp from timber or other fibrous materials',
      },
    ],
  },
  {
    header: 'Refining',
    options: [
      {
        value: 'REFINING_OF_MINERAL_OIL',
        label: 'Mineral oil refining (Carbon dioxide)',
      },
    ],
  },
  {
    header: 'Waste',
    options: [
      {
        value: 'WASTE',
        label: 'Waste (Carbon dioxide)',
        hint: 'Including incineration and combustion of waste and other energy recovery from waste',
      },
    ],
  },
];
