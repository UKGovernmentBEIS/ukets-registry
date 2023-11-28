export enum TransactionType {
  IssueOfAAUsAndRMUs = 'IssueOfAAUsAndRMUs',
  IssueAllowances = 'IssueAllowances',
  TransferToSOPforFirstExtTransferAAU = 'TransferToSOPforFirstExtTransferAAU',
  CancellationKyotoUnits = 'CancellationKyotoUnits',
  MandatoryCancellation = 'MandatoryCancellation',
  Art37Cancellation = 'Art37Cancellation',
  AmbitionIncreaseCancellation = 'AmbitionIncreaseCancellation',
  InternalTransfer = 'InternalTransfer',
  ExternalTransfer = 'ExternalTransfer',
  CarryOver_AAU = 'CarryOver_AAU',
  CarryOver_CER_ERU_FROM_AAU = 'CarryOver_CER_ERU_FROM_AAU',
  SurrenderAllowances = 'SurrenderAllowances',
  InboundTransfer = 'InboundTransfer',
  CentralTransferAllowances = 'CentralTransferAllowances',
  TransferAllowances = 'TransferAllowances',
  BalanceInstallationTransferAllowances = 'BalanceInstallationTransferAllowances',
  ClosureTransfer = 'ClosureTransfer',
  DeletionOfAllowances = 'DeletionOfAllowances',
  AuctionDeliveryAllowances = 'AuctionDeliveryAllowances',
  Retirement = 'Retirement',
  ConversionCP1 = 'ConversionCP1',
  ConversionA = 'ConversionA',
  ConversionB = 'ConversionB',
  TransferToSOPForConversionOfERU = 'TransferToSOPForConversionOfERU',
  ExpiryDateChange = 'ExpiryDateChange',
  Replacement = 'Replacement',
  AllocateAllowances = 'AllocateAllowances',
  ExcessAllocation = 'ExcessAllocation',
  ExcessAuction = 'ExcessAuction',
  ReverseAllocateAllowances = 'ReverseAllocateAllowances',
  ReverseSurrenderAllowances = 'ReverseSurrenderAllowances',
  ReverseDeletionOfAllowances = 'ReverseDeletionOfAllowances',
  IssuanceDecoupling = 'IssuanceDecoupling',
  ExternalTransferCP0 = 'ExternalTransferCP0',
  AllocationOfFormerEUA = 'AllocationOfFormerEUA',
  IssuanceCP0 = 'IssuanceCP0',
  RetirementCP0 = 'RetirementCP0',
  IssuanceOfFormerEUA = 'IssuanceOfFormerEUA',
  CancellationCP0 = 'CancellationCP0',
  RetirementOfSurrenderedFormerEUA = 'RetirementOfSurrenderedFormerEUA',
  ConversionOfSurrenderedFormerEUA = 'ConversionOfSurrenderedFormerEUA',
  CorrectiveTransactionForReversal_1 = 'CorrectiveTransactionForReversal_1',
  Correction = 'Correction',
  SurrenderKyotoUnits = 'SurrenderKyotoUnits',
  CancellationAgainstDeletion = 'CancellationAgainstDeletion',
  ReversalSurrenderKyoto = 'ReversalSurrenderKyoto',
  SetAside = 'SetAside',
}

interface TransactionTypeValues {
  /**
   * Label property for showing transaction type descriptions per case
   */
  label: { defaultLabel: string; transactionProposalLabel: string };

  /**
   * If ITL Notification Id input field is displayed for this transaction type
   */
  hasTransactionITLNotificationId: boolean;

  /**
   * If ITL Notification Id input field is mandatory for this transaction type
   */
  isMandatoryITLNotificationIdForTransaction: boolean;

  /**
   * If the transaction type is ETS
   */
  isETSTransaction: boolean;

  isTransactionReversed?: boolean;

  showAccountNameInsteadOfNumber?: boolean;

  hideAccountNumber?: boolean;

  showAcquiringDescription?: boolean;

  isKPTransactionOnlyForAdmin?: boolean;

  isCentralTransfer?: boolean;

  hideTip?: boolean;

  cannotGoBackToTransactionType?: boolean;

  isIssuance?: boolean;

  order?: number;
}

export const TRANSACTION_TYPES_VALUES: Record<
  TransactionType,
  TransactionTypeValues
> = {
  IssueOfAAUsAndRMUs: {
    label: {
      defaultLabel: 'Issuance of KP units',
      transactionProposalLabel: 'Propose transaction for issue AAU or RMU',
    },
    hasTransactionITLNotificationId: false,
    isMandatoryITLNotificationIdForTransaction: false,
    isETSTransaction: false,
    isIssuance: true,
  },
  IssueAllowances: {
    label: {
      defaultLabel: 'Issuance of Allowances',
      transactionProposalLabel:
        'Propose transaction for issuance of allowances',
    },
    hasTransactionITLNotificationId: false,
    isMandatoryITLNotificationIdForTransaction: false,
    isETSTransaction: true,
    isIssuance: true,
  },
  TransferToSOPforFirstExtTransferAAU: {
    label: {
      defaultLabel: 'Transfer to SOP for First External Transfer',
      transactionProposalLabel:
        'Propose transaction for transfer to SOP for First External Transfer of AAU',
    },
    hasTransactionITLNotificationId: false,
    isMandatoryITLNotificationIdForTransaction: false,
    isETSTransaction: false,
    isKPTransactionOnlyForAdmin: true,
  },
  CancellationKyotoUnits: {
    label: {
      defaultLabel: 'Voluntary cancellation of KP units',
      transactionProposalLabel:
        'Propose transaction for voluntary cancellation of KP units',
    },
    hasTransactionITLNotificationId: false,
    isMandatoryITLNotificationIdForTransaction: false,
    isETSTransaction: false,
    showAccountNameInsteadOfNumber: true,
  },
  MandatoryCancellation: {
    label: {
      defaultLabel: 'Mandatory cancellation of KP units',
      transactionProposalLabel:
        'Propose transaction for mandatory cancellation of KP units',
    },
    hasTransactionITLNotificationId: true,
    isMandatoryITLNotificationIdForTransaction: false,
    isETSTransaction: false,
  },
  Art37Cancellation: {
    label: {
      defaultLabel: 'Art. 3.7ter cancellation of AAU',
      transactionProposalLabel:
        'Propose transaction for art. 3.7ter cancellation of AAU',
    },
    hasTransactionITLNotificationId: false,
    isMandatoryITLNotificationIdForTransaction: false,
    isETSTransaction: false,
    isKPTransactionOnlyForAdmin: true,
  },
  AmbitionIncreaseCancellation: {
    label: {
      defaultLabel: 'Ambition increase cancellation of AAU',
      transactionProposalLabel:
        'Propose transaction for ambition increase cancellation of AAU',
    },
    hasTransactionITLNotificationId: false,
    isMandatoryITLNotificationIdForTransaction: false,
    isETSTransaction: false,
    isKPTransactionOnlyForAdmin: true,
  },
  InternalTransfer: {
    label: {
      defaultLabel: 'Internal transfer',
      transactionProposalLabel: 'Propose transaction for transfer KP units',
    },
    hasTransactionITLNotificationId: true,
    isMandatoryITLNotificationIdForTransaction: false,
    isETSTransaction: false,
  },
  ExternalTransfer: {
    label: {
      defaultLabel: 'External transfer',
      transactionProposalLabel: 'Propose transaction for transfer KP units',
    },
    hasTransactionITLNotificationId: true,
    isMandatoryITLNotificationIdForTransaction: false,
    isETSTransaction: false,
  },
  CarryOver_AAU: {
    label: {
      defaultLabel: 'Carry-over AAU',
      transactionProposalLabel: 'Propose transaction for carry-over AAU',
    },
    hasTransactionITLNotificationId: false,
    isMandatoryITLNotificationIdForTransaction: false,
    isETSTransaction: false,
  },
  CarryOver_CER_ERU_FROM_AAU: {
    label: {
      defaultLabel: 'Carry-over CER or ERU from AAU units',
      transactionProposalLabel:
        'Propose transaction for carry-over CER or ERU from AAU units',
    },
    hasTransactionITLNotificationId: false,
    isMandatoryITLNotificationIdForTransaction: false,
    isETSTransaction: false,
  },
  SurrenderAllowances: {
    label: {
      defaultLabel: 'Surrender',
      transactionProposalLabel: 'Propose transaction for surrender allowances',
    },
    hasTransactionITLNotificationId: false,
    isMandatoryITLNotificationIdForTransaction: false,
    isETSTransaction: true,
    showAccountNameInsteadOfNumber: true,
    order: 2,
  },
  InboundTransfer: {
    label: {
      defaultLabel: 'Inbound Transfer',
      transactionProposalLabel: 'Propose transaction for inbound transfer',
    },
    hasTransactionITLNotificationId: false,
    isMandatoryITLNotificationIdForTransaction: false,
    isETSTransaction: false,
    showAccountNameInsteadOfNumber: true,
  },
  CentralTransferAllowances: {
    label: {
      defaultLabel: 'Central Transfer',
      transactionProposalLabel: 'Propose transaction for central transfer',
    },
    hasTransactionITLNotificationId: false,
    isMandatoryITLNotificationIdForTransaction: false,
    isETSTransaction: true,
    isCentralTransfer: true,
    hideTip: true,
    showAccountNameInsteadOfNumber: true,
  },
  TransferAllowances: {
    label: {
      defaultLabel: 'Transfer allowances',
      transactionProposalLabel: 'Propose transaction to transfer allowances',
    },
    hasTransactionITLNotificationId: false,
    isMandatoryITLNotificationIdForTransaction: false,
    isETSTransaction: true,
    order: 1,
  },
  ClosureTransfer: {
    label: {
      defaultLabel: 'Closure Transfer',
      transactionProposalLabel:
        'Propose transaction to transfer allowances due to account closure',
    },
    hasTransactionITLNotificationId: false,
    isMandatoryITLNotificationIdForTransaction: false,
    isETSTransaction: true,
    order: 5,
  },
  AuctionDeliveryAllowances: {
    label: {
      defaultLabel: 'Transfer of allowances to auction delivery account',
      transactionProposalLabel:
        'Propose transaction for transfer of allowances to auction delivery account',
    },
    hasTransactionITLNotificationId: false,
    isMandatoryITLNotificationIdForTransaction: false,
    isETSTransaction: true,
  },
  Retirement: {
    label: {
      defaultLabel: 'Retirement',
      transactionProposalLabel: 'Propose transaction for retirement',
    },
    hasTransactionITLNotificationId: false,
    isMandatoryITLNotificationIdForTransaction: false,
    isETSTransaction: false,
    isKPTransactionOnlyForAdmin: true,
  },
  ConversionCP1: {
    label: {
      defaultLabel: 'Conversion CP1',
      transactionProposalLabel: 'Propose transaction for conversion CP1',
    },
    hasTransactionITLNotificationId: false,
    isMandatoryITLNotificationIdForTransaction: false,
    isETSTransaction: false,
    isKPTransactionOnlyForAdmin: true,
  },
  ConversionA: {
    label: {
      defaultLabel: 'AAUs/RMUs to ERUs prior to Transfer to SOP',
      transactionProposalLabel:
        'Propose transaction for conversion of AAUs or RMUs to ERUs prior to Transfer to SOP (Conversion A)',
    },
    hasTransactionITLNotificationId: false,
    isMandatoryITLNotificationIdForTransaction: false,
    isETSTransaction: false,
    isKPTransactionOnlyForAdmin: true,
  },
  ConversionB: {
    label: {
      defaultLabel: 'AAUs/RMUs to ERUs after Transfer to SOP',
      transactionProposalLabel:
        'Propose transaction for conversion of AAUs or RMUs to ERUs after the Transfer to SOP (Conversion B)',
    },
    hasTransactionITLNotificationId: false,
    isMandatoryITLNotificationIdForTransaction: false,
    isETSTransaction: false,
    isKPTransactionOnlyForAdmin: true,
  },
  TransferToSOPForConversionOfERU: {
    label: {
      defaultLabel: 'Transfer to SOP for Conversion of ERU',
      transactionProposalLabel:
        'Propose transaction for transfer to SOP for Conversion of ERU',
    },
    hasTransactionITLNotificationId: false,
    isMandatoryITLNotificationIdForTransaction: false,
    isETSTransaction: false,
    isKPTransactionOnlyForAdmin: true,
  },
  ExpiryDateChange: {
    label: {
      defaultLabel: 'Expiry Date Change of tCER or lCER',
      transactionProposalLabel:
        'Propose transaction for expiry Date Change of tCER or lCER',
    },
    hasTransactionITLNotificationId: true,
    isMandatoryITLNotificationIdForTransaction: true,
    isETSTransaction: false,
  },
  Replacement: {
    label: {
      defaultLabel: 'Replacement of tCER or lCER',
      transactionProposalLabel:
        'Propose transaction for replacement of tCER or lCER',
    },
    hasTransactionITLNotificationId: true,
    isMandatoryITLNotificationIdForTransaction: true,
    isETSTransaction: false,
  },
  AllocateAllowances: {
    label: {
      defaultLabel: 'Allocation of allowances',
      transactionProposalLabel:
        'Propose transaction for allocation of allowances',
    },
    hasTransactionITLNotificationId: false,
    isMandatoryITLNotificationIdForTransaction: false,
    isETSTransaction: true,
  },
  ExcessAllocation: {
    label: {
      defaultLabel: 'Return excess allocation',
      transactionProposalLabel:
        'Propose transaction to return of excess allocation',
    },
    hasTransactionITLNotificationId: false,
    isMandatoryITLNotificationIdForTransaction: false,
    isETSTransaction: true,
    cannotGoBackToTransactionType: true,
    showAccountNameInsteadOfNumber: true,
  },
  ExcessAuction: {
    label: {
      defaultLabel: 'Return excess auction',
      transactionProposalLabel:
        'Return of excess allocation return of excess auction',
    },
    hasTransactionITLNotificationId: false,
    isMandatoryITLNotificationIdForTransaction: false,
    isETSTransaction: true,
    showAcquiringDescription: true,
    hideAccountNumber: true,
    hideTip: true,
    order: 4,
  },
  ReverseAllocateAllowances: {
    label: {
      defaultLabel: 'Reversal of allocation of allowances',
      transactionProposalLabel: 'Reverse allocation of allowances',
    },
    hasTransactionITLNotificationId: false,
    isMandatoryITLNotificationIdForTransaction: false,
    isETSTransaction: true,
    isTransactionReversed: true,
    showAccountNameInsteadOfNumber: true,
  },
  ReverseSurrenderAllowances: {
    label: {
      defaultLabel: 'Reversal of surrender of allowances',
      transactionProposalLabel: 'Reverse surrender of allowances',
    },
    hasTransactionITLNotificationId: false,
    isMandatoryITLNotificationIdForTransaction: false,
    isETSTransaction: true,
    isTransactionReversed: true,
  },
  ReverseDeletionOfAllowances: {
    label: {
      defaultLabel: 'Reversal of deletion of allowances',
      transactionProposalLabel: 'Reverse deletion of allowances',
    },
    hasTransactionITLNotificationId: false,
    isMandatoryITLNotificationIdForTransaction: false,
    isETSTransaction: true,
    isTransactionReversed: true,
  },

  DeletionOfAllowances: {
    label: {
      defaultLabel: 'Deletion of allowances',
      transactionProposalLabel: 'Propose transaction to delete allowances',
    },
    hasTransactionITLNotificationId: false,
    isMandatoryITLNotificationIdForTransaction: false,
    isETSTransaction: true,
    showAccountNameInsteadOfNumber: true,
    order: 3,
  },
  BalanceInstallationTransferAllowances: {
    label: {
      defaultLabel: 'Balance installation transfer allowances',
      transactionProposalLabel: 'Balance installation transfer allowances',
    },
    hasTransactionITLNotificationId: false,
    isMandatoryITLNotificationIdForTransaction: false,
    isETSTransaction: true,
  },
  IssuanceDecoupling: {
    label: {
      defaultLabel: 'Issuance Decoupling',
      transactionProposalLabel: 'Propose transaction for issuance Decoupling',
    },
    hasTransactionITLNotificationId: false,
    isMandatoryITLNotificationIdForTransaction: false,
    isETSTransaction: false,
  },
  ExternalTransferCP0: {
    label: {
      defaultLabel: 'External Transfer CP0',
      transactionProposalLabel: 'Propose transaction for external Transfer CP0',
    },
    hasTransactionITLNotificationId: false,
    isMandatoryITLNotificationIdForTransaction: false,
    isETSTransaction: false,
  },
  AllocationOfFormerEUA: {
    label: {
      defaultLabel: 'Allocation of former EUA',
      transactionProposalLabel:
        'Propose transaction for allocation of former EUA',
    },
    hasTransactionITLNotificationId: false,
    isMandatoryITLNotificationIdForTransaction: false,
    isETSTransaction: false,
  },
  IssuanceCP0: {
    label: {
      defaultLabel: 'Issuance CP0',
      transactionProposalLabel: 'Propose transaction for issuance CP0',
    },
    hasTransactionITLNotificationId: false,
    isMandatoryITLNotificationIdForTransaction: false,
    isETSTransaction: false,
  },
  RetirementCP0: {
    label: {
      defaultLabel: 'Retirement CP0',
      transactionProposalLabel: 'Propose transaction for retirement CP0',
    },
    hasTransactionITLNotificationId: false,
    isMandatoryITLNotificationIdForTransaction: false,
    isETSTransaction: false,
  },
  IssuanceOfFormerEUA: {
    label: {
      defaultLabel: 'Issuance of former EUA',
      transactionProposalLabel:
        'Propose transaction for issuance of former EUA',
    },
    hasTransactionITLNotificationId: false,
    isMandatoryITLNotificationIdForTransaction: false,
    isETSTransaction: false,
  },
  CancellationCP0: {
    label: {
      defaultLabel: 'Cancellation CP0',
      transactionProposalLabel: 'Propose transaction for cancellation CP0',
    },
    hasTransactionITLNotificationId: false,
    isMandatoryITLNotificationIdForTransaction: false,
    isETSTransaction: false,
  },
  RetirementOfSurrenderedFormerEUA: {
    label: {
      defaultLabel: 'Retirement of surrendered former EUA',
      transactionProposalLabel:
        'Propose transaction for retirement of surrendered former EUA',
    },
    hasTransactionITLNotificationId: false,
    isMandatoryITLNotificationIdForTransaction: false,
    isETSTransaction: false,
  },
  ConversionOfSurrenderedFormerEUA: {
    label: {
      defaultLabel: 'Conversion of surrendered former EUA',
      transactionProposalLabel:
        'Propose transaction for conversion of surrendered former EUA',
    },
    hasTransactionITLNotificationId: false,
    isMandatoryITLNotificationIdForTransaction: false,
    isETSTransaction: false,
  },
  CorrectiveTransactionForReversal_1: {
    label: {
      defaultLabel: 'Corrective transaction for reversal 1',
      transactionProposalLabel:
        'Propose transaction for corrective transaction for reversal 1',
    },
    hasTransactionITLNotificationId: false,
    isMandatoryITLNotificationIdForTransaction: false,
    isETSTransaction: false,
  },
  Correction: {
    label: {
      defaultLabel: 'Correction',
      transactionProposalLabel: 'Propose transaction for correction',
    },
    hasTransactionITLNotificationId: false,
    isMandatoryITLNotificationIdForTransaction: false,
    isETSTransaction: false,
  },
  SurrenderKyotoUnits: {
    label: {
      defaultLabel: 'Surrender Kyoto units',
      transactionProposalLabel: 'Propose transaction for surrender Kyoto units',
    },
    hasTransactionITLNotificationId: false,
    isMandatoryITLNotificationIdForTransaction: false,
    isETSTransaction: false,
  },
  CancellationAgainstDeletion: {
    label: {
      defaultLabel: 'Cancellation against deletion',
      transactionProposalLabel:
        'Propose transaction for cancellation against deletion',
    },
    hasTransactionITLNotificationId: false,
    isMandatoryITLNotificationIdForTransaction: false,
    isETSTransaction: false,
  },
  ReversalSurrenderKyoto: {
    label: {
      defaultLabel: 'Reversal surrender Kyoto',
      transactionProposalLabel:
        'Propose transaction for reversal surrender Kyoto',
    },
    hasTransactionITLNotificationId: false,
    isMandatoryITLNotificationIdForTransaction: false,
    isETSTransaction: false,
  },
  SetAside: {
    label: {
      defaultLabel: 'Set aside',
      transactionProposalLabel: 'Propose transaction for set aside',
    },
    hasTransactionITLNotificationId: false,
    isMandatoryITLNotificationIdForTransaction: false,
    isETSTransaction: false,
  },
};
