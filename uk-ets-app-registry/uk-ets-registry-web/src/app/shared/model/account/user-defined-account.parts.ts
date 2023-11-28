/**
 * a model for when the user enters a custom account number
 */
export interface UserDefinedAccountParts {
  userDefinedCountryCode: string;
  userDefinedAccountType: string;
  userDefinedAccountId: string;
  userDefinedPeriod: string;
  userDefinedCheckDigits: string;
}

export type UserDefinedAccountPartsFormatter = (
  parts: UserDefinedAccountParts
) => string | null;

export const formatUserDefinedFullAccountIdentifier: UserDefinedAccountPartsFormatter = (
  userDefinedAccountParts
) => {
  if (
    userDefinedAccountParts == null ||
    !userDefinedAccountParts.userDefinedAccountId
  ) {
    return null;
  }
  let result: string;
  result =
    userDefinedAccountParts.userDefinedCountryCode +
    '-' +
    userDefinedAccountParts.userDefinedAccountType +
    '-' +
    userDefinedAccountParts.userDefinedAccountId;
  if (userDefinedAccountParts.userDefinedPeriod) {
    result += '-' + userDefinedAccountParts.userDefinedPeriod;
  }
  if (userDefinedAccountParts.userDefinedCheckDigits) {
    result += '-' + userDefinedAccountParts.userDefinedCheckDigits;
  }
  return result;
};
