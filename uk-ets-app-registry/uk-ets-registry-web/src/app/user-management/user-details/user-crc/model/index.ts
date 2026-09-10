import { UkDate } from '@shared/model/uk-date';

export type CrcDetails = {
  crcIssuanceDate: UkDate;
};

export type UserCrcInfo = { urid: string; crc: boolean } & {
  details: CrcDetails;
};

export type UserCrcUpdateRequest = {
  crc: boolean;
  crcIssuanceDate: string;
};

export const toBoolean = (value: string): boolean =>
  value.trim().toLowerCase() === 'true';

export const fromISOString = (value: string): UkDate => {
  if (!value) {
    return {
      day: null,
      month: null,
      year: null,
    };
  }
  const date = new Date(value);
  return {
    day: date.getDate().toString(),
    month: (date.getMonth() + 1).toString(),
    year: date.getFullYear().toString(),
  };
};
