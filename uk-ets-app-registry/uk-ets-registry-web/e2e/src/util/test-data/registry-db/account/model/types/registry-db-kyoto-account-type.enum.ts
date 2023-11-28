export enum KyotoAccountType {
  /**
   * The party holding account.
   */
  PARTY_HOLDING_ACCOUNT,

  /**
   * The former operator holding account.
   */
  FORMER_OPERATOR_HOLDING_ACCOUNT,

  /**
   * The person holding account.
   */
  PERSON_HOLDING_ACCOUNT,

  /**
   * The previous period surplus reserve account.
   */
  PREVIOUS_PERIOD_SURPLUS_RESERVE_ACCOUNT,

  /**
   * The net source cancellation account.
   */
  NET_SOURCE_CANCELLATION_ACCOUNT,

  /**
   * The non compliance cancellation account.
   */
  NON_COMPLIANCE_CANCELLATION_ACCOUNT,

  /**
   * The voluntary cancellation account.
   */
  VOLUNTARY_CANCELLATION_ACCOUNT,

  /**
   * The mandatory cancellation account.
   */
  MANDATORY_CANCELLATION_ACCOUNT,

  /**
   * The Art37Ter cancellation account.
   */
  ARTICLE_3_7_TER_CANCELLATION_ACCOUNT,

  /**
   * The ambition increase cancellation account.
   */
  AMBITION_INCREASE_CANCELLATION_ACCOUNT,

  /**
   * The retirement account.
   */
  RETIREMENT_ACCOUNT,

  /**
   * The TCER replacement account for expiry.
   */
  TCER_REPLACEMENT_ACCOUNT_FOR_EXPIRY,

  /**
   * The LCER replacement account for expiry.
   */
  LCER_REPLACEMENT_ACCOUNT_FOR_EXPIRY,

  /**
   * The LCER replacement account for reversal of storage.
   */
  LCER_REPLACEMENT_ACCOUNT_FOR_REVERSAL_OF_STORAGE,

  /**
   * The LCER replacement account for non submission of certification report.
   */
  LCER_REPLACEMENT_ACCOUNT_FOR_NON_SUBMISSION_OF_CERTIFICATION_REPORT,
}

export function getKyotoCode(kyotoAccountType: KyotoAccountType): number {
  switch (kyotoAccountType) {
    case KyotoAccountType.PARTY_HOLDING_ACCOUNT:
      return 100;
    case KyotoAccountType.FORMER_OPERATOR_HOLDING_ACCOUNT:
      return 120;
    case KyotoAccountType.PERSON_HOLDING_ACCOUNT:
      return 121;
    case KyotoAccountType.PREVIOUS_PERIOD_SURPLUS_RESERVE_ACCOUNT:
      return 130;
    case KyotoAccountType.NET_SOURCE_CANCELLATION_ACCOUNT:
      return 210;
    case KyotoAccountType.NON_COMPLIANCE_CANCELLATION_ACCOUNT:
      return 220;
    case KyotoAccountType.VOLUNTARY_CANCELLATION_ACCOUNT:
      return 230;
    case KyotoAccountType.MANDATORY_CANCELLATION_ACCOUNT:
      return 250;
    case KyotoAccountType.ARTICLE_3_7_TER_CANCELLATION_ACCOUNT:
      return 270;
    case KyotoAccountType.AMBITION_INCREASE_CANCELLATION_ACCOUNT:
      return 280;
    case KyotoAccountType.RETIREMENT_ACCOUNT:
      return 300;
    case KyotoAccountType.TCER_REPLACEMENT_ACCOUNT_FOR_EXPIRY:
      return 411;
    case KyotoAccountType.LCER_REPLACEMENT_ACCOUNT_FOR_EXPIRY:
      return 421;
    case KyotoAccountType.LCER_REPLACEMENT_ACCOUNT_FOR_REVERSAL_OF_STORAGE:
      return 422;
    case KyotoAccountType.LCER_REPLACEMENT_ACCOUNT_FOR_NON_SUBMISSION_OF_CERTIFICATION_REPORT:
      return 423;
  }
}
