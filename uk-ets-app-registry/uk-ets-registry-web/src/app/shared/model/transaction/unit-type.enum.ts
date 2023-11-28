export enum UnitType {
  AAU = 'AAU',
  RMU = 'RMU',
  ERU_FROM_AAU = 'ERU_FROM_AAU',
  ERU_FROM_RMU = 'ERU_FROM_RMU',
  CER = 'CER',
  TCER = 'TCER',
  LCER = 'LCER',
  NON_KYOTO = 'NON_KYOTO',
  MULTIPLE = 'MULTIPLE',
  ALLOWANCE = 'ALLOWANCE',
  ALLOWANCE_CP0 = 'ALLOWANCE_CP0',
  FORMER_EUA = 'FORMER_EUA',
  ALLOWANCE_CHAPTER3 = 'ALLOWANCE_CHAPTER3',
}

export const UNIT_TYPE_LABELS: Record<
  UnitType,
  { label: string; labelPlural: string; labelLong: string }
> = {
  AAU: {
    label: 'AAU',
    labelLong: 'Assigned Amount Unit',
    labelPlural: 'Assigned Amount Units',
  },
  RMU: {
    label: 'RMU',
    labelLong: 'Removal Unit',
    labelPlural: 'Removal Units',
  },
  ERU_FROM_AAU: {
    label: 'ERU from AAU',
    labelLong: 'ERU from AAU',
    labelPlural: 'Emission Reduction Units (converted from an AAU)',
  },
  ERU_FROM_RMU: {
    label: 'ERU from RMU',
    labelLong: 'ERU from RMU',
    labelPlural: 'Emission Reduction Units (converted from an RMU)',
  },
  CER: {
    label: 'CER',
    labelLong: 'Certified Emission Reduction Unit',
    labelPlural: 'Certified Emission Reduction Units',
  },
  TCER: {
    label: 'tCER',
    labelLong: 'Temporary CER',
    labelPlural: 'Temporary CER',
  },
  LCER: {
    label: 'lCER',
    labelLong: 'Long-term CER',
    labelPlural: 'Long-term CER',
  },
  NON_KYOTO: {
    label: 'Non Kyoto',
    labelLong: 'Non-KP Unit',
    labelPlural: 'Non-KP Units',
  },
  MULTIPLE: {
    label: 'Multiple',
    labelLong: 'Multiple unit types',
    labelPlural: 'Multiple unit types',
  },
  ALLOWANCE: {
    label: 'Allowance',
    labelLong: 'Allowance',
    labelPlural: 'Allowances',
  },
  ALLOWANCE_CP0: {
    label: 'Allowance CP0',
    labelLong: 'Allowance CP0',
    labelPlural: 'Allowances CP0',
  },
  FORMER_EUA: {
    label: 'Former EUA',
    labelLong: 'Former EUA',
    labelPlural: 'Former EUA',
  },
  ALLOWANCE_CHAPTER3: {
    label: 'Allowance Chapter 3',
    labelLong: 'Allowance Chapter 3',
    labelPlural: 'Allowances Chapter 3',
  },
};

function populateUnitTypeOptions() {
  const arr = [];
  Object.entries(UNIT_TYPE_LABELS).forEach(([request_type, textValues]) =>
    arr.push({
      label: textValues.labelLong,
      value: request_type,
      labelPlural: textValues.labelPlural,
    })
  );
  return arr;
}
//TODO: remove this type completely - use the UNIT_TYPE_LABELS. We keep it for now to lower the impact of the removal of UNIT_TYPE_OPTIONS.
export const UNIT_TYPE_OPTIONS = [
  {
    label: '',
    labelPlural: '',
    value: null,
  },
].concat(populateUnitTypeOptions());
