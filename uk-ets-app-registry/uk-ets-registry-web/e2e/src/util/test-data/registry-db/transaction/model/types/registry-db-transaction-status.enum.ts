export enum RegistryDbTransactionStatusEnum {
  AWAITING_APPROVAL = 'AWAITING_APPROVAL',
  PROPOSED = 'PROPOSED',
  CHECKED_NO_DISCREPANCY = 'CHECKED_NO_DISCREPANCY',
  CHECKED_DISCREPANCY = 'CHECKED_DISCREPANCY',
  COMPLETED = 'COMPLETED',
  TERMINATED = 'TERMINATED',
  REJECTED = 'REJECTED',
  CANCELLED = 'CANCELLED',
  ACCEPTED = 'ACCEPTED',
  STL_CHECKED_NO_DISCREPANCY = 'STL_CHECKED_NO_DISCREPANCY',
  STL_CHECKED_DISCREPANCY = 'STL_CHECKED_DISCREPANCY',
  REVERSED = 'REVERSED',
  DELAYED = 'DELAYED',
}

export function getTransactionStatus(value: string) {
  switch (value) {
    case 'Awaiting approval':
      return RegistryDbTransactionStatusEnum.AWAITING_APPROVAL;
    case 'Proposed':
      return RegistryDbTransactionStatusEnum.PROPOSED;
    case 'Checked no discrepancy':
      return RegistryDbTransactionStatusEnum.CHECKED_NO_DISCREPANCY;
    case 'Checked discrepancy':
      return RegistryDbTransactionStatusEnum.CHECKED_DISCREPANCY;
    case 'Completed':
      return RegistryDbTransactionStatusEnum.COMPLETED;
    case 'Terminated':
      return RegistryDbTransactionStatusEnum.TERMINATED;
    case 'Rejected':
      return RegistryDbTransactionStatusEnum.REJECTED;
    case 'Cancelled':
      return RegistryDbTransactionStatusEnum.CANCELLED;
    case 'Accepted':
      return RegistryDbTransactionStatusEnum.ACCEPTED;
    case 'STL checked no discrepancy':
      return RegistryDbTransactionStatusEnum.STL_CHECKED_NO_DISCREPANCY;
    case 'STL checked discrepancy':
      return RegistryDbTransactionStatusEnum.STL_CHECKED_DISCREPANCY;
    case 'Revered':
      return RegistryDbTransactionStatusEnum.REVERSED;
    case 'Delayed':
      return RegistryDbTransactionStatusEnum.DELAYED;
    default:
      return null;
  }
}
