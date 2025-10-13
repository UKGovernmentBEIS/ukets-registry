export type RequestPaymentOrigin =
  | 'ACCOUNT_OPENING'
  | 'ADD_AUTHORISED_REPRESENTATIVE'
  | 'REPLACE_AUTHORISED_REPRESENTATIVE'
  | 'CHANGE_AUTHORISED_REPRESENTATIVE_PERMISSION';

export type PaymentMethod = 'BACS' | 'CARD_OR_DIGITAL_WALLET' | 'WEBLINK';

export const PaymentMethod = {
  DEFAULT: 'CARD_OR_DIGITAL_WALLET',

  label(method = PaymentMethod.DEFAULT): string {
    if (method === 'CARD_OR_DIGITAL_WALLET') {
      return 'Card or digital wallet';
    } else if (method === 'BACS') {
      return 'Bank transfer (BACS,CHAPS,SWIFT and Faster Payments)';
    } else if (method === 'WEBLINK') {
      return 'Payment link';
    } else {
      throw Error('Invalid payment method');
    }
  },
};

export type PaymentStatus =
  | 'CREATED'
  | 'STARTED'
  | 'SUBMITTED'
  | 'CAPTURABLE'
  | 'SUCCESS'
  | 'FAILED'
  | 'CANCELLED'
  | 'ERROR';

export const PaymentStatus = {
  DEFAULT: 'CREATED',

  label(status = PaymentStatus.DEFAULT): string {
    if (status === 'CREATED') {
      return 'Created';
    } else if (status === 'STARTED') {
      return 'Started';
    } else if (status === 'SUBMITTED') {
      return 'Submitted';
    } else if (status === 'CAPTURABLE') {
      return 'Capturable';
    } else if (status === 'SUCCESS') {
      return 'Success';
    } else if (status === 'FAILED') {
      return 'Failed';
    } else if (status === 'CANCELLED') {
      return 'Cancelled';
    } else if (status === 'ERROR') {
      return 'Error';
    } else {
      throw Error('Invalid payment status');
    }
  },

  color(status = PaymentStatus.DEFAULT): string {
    if (status === 'CREATED') {
      return 'purple';
    } else if (status === 'STARTED') {
      return 'yellow';
    } else if (status === 'SUBMITTED') {
      return 'yellow';
    } else if (status === 'CAPTURABLE') {
      return 'pink';
    } else if (status === 'SUCCESS') {
      return 'green';
    } else if (status === 'FAILED') {
      return 'red';
    } else if (status === 'CANCELLED') {
      return 'red';
    } else if (status === 'ERROR') {
      return 'red';
    } else {
      throw Error('Invalid payment status');
    }
  },
};

export const AvailablePaymentMethods = [
  {
    label: PaymentMethod.label('CARD_OR_DIGITAL_WALLET'),
    hint: 'The prefered method of payment.Most cards accepted, including VISA,Mastercard,American Express, Apple Pay and Google Pay.You will be directed to Gov.uk pay.Payment is cleared instantly,increasing the speed of processing your application.',
    value: 'CARD_OR_DIGITAL_WALLET',
    enabled: true,
  },
  {
    label: PaymentMethod.label('BACS'),
    hint: 'Transfer money to our bank account.You will be provided with the details on the next screen.This payment method could significantly delay your application.',
    value: 'BACS',
    enabled: true,
  },
];

export interface PaymentDetails {
  recipientUrid: string;
  recipientName: string;
  amount: number;
  description: string;
}

export interface PaymentRequestDetails {
  recipientUrid: string;
  amount: number;
  description: string;
  parentRequestId: string;
}
