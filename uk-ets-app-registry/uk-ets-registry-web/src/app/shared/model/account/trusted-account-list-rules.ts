export class TrustedAccountListRules {
  // The current rule 1 - Approval of a second AR is required in transfers.
  currentRule1?: boolean;
  // The current rule 2 - Transfers are allowed for accounts outside the TAL.
  currentRule2?: boolean;
  //The current rule 3 - Whether a single person approval is required for specific transactions.
  currentRule3?: boolean;

  // Is the approval of a second authorised representative necessary to
  // execute transfers to accounts on the trusted account list?
  rule1: boolean;
  // Are transfers to accounts not on the trusted account list blocked?
  rule2: boolean;
  // Is a single person approval is required for specific transactions?
  rule3?: boolean;
}

export const DEFAULT_OHA_AOHA_TRANSACTION_RULES: TrustedAccountListRules = {
  rule1: true,
  rule2: false,
  rule3: false,
};

export const DEFAULT_KP_TRADING_TRANSACTION_RULES: TrustedAccountListRules = {
  rule1: true,
  rule2: false,
};

export function getRuleLabel(rule: boolean) {
  switch (rule) {
    case true:
      return 'Yes';
    case false:
      return 'No';
    default:
      return '';
  }
}
