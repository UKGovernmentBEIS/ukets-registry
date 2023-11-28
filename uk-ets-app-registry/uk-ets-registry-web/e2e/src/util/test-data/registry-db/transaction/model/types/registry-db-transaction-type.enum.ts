export enum RegistryDbTransactionTypeEnum {
  IssueOfAAUsAndRMUs = 'IssueOfAAUsAndRMUs',
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
  DeletionOfAllowances = 'DeletionOfAllowances',
}

export function getTransactionType(value: string) {
  switch (value) {
    // Issue KP units: for this transaction type only senior admins can see the task
    case 'Issue KP units':
      return RegistryDbTransactionTypeEnum.IssueOfAAUsAndRMUs;
    // Internal Transfer: task are shown for admins and also the AR of the account
    case 'Internal Transfer':
      return RegistryDbTransactionTypeEnum.InternalTransfer;
    case 'Surrender Allowances':
      return RegistryDbTransactionTypeEnum.SurrenderAllowances;
    case 'Deletion of Allowances':
      return RegistryDbTransactionTypeEnum.DeletionOfAllowances;
    default:
      return null;
  }
}
