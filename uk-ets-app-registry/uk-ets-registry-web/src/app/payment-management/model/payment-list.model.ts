import { PaymentMethod, PaymentStatus } from '@request-payment/model';

export interface PaymentSearchCriteria {
  referenceNumber: string;
}

export interface PaymentSearchResult {
  referenceNumber: number;
  paymentId: string;
  type: string;
  method: PaymentMethod;
  status: PaymentStatus;
  amount: number;
  updated: string;
}
