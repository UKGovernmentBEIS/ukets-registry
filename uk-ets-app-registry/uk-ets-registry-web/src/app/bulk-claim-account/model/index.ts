export const BULK_CLAIM_ACCOUNT_BASE_PATH = 'bulk-claim-account';
export const BULK_CLAIM_ACCOUNT_REQUEST_SUBMITTED = 'request-submitted';

export interface BulkClaimAccountResult {
  total: number;
  successful: number;
  failed: number;
}
