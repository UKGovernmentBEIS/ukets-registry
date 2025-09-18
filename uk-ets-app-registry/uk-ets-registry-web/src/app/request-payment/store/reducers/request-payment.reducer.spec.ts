import {
  RequestPaymentState,
  initialState,
  requestPaymentReducer,
} from '@request-payment/store/reducers';
import {
  clearRequestPayment,
  enterRequestPaymentWizard,
  setPaymentDetails,
  submitPaymentRequestSuccess,
} from '@request-payment/store/actions';
import { RequestPaymentOrigin } from '@request-payment/model';

describe('Request Payment reducer', () => {
  describe('unknown action', () => {
    test('should return the default state', () => {
      const action = {
        type: 'Unknown',
      };
      const state = requestPaymentReducer(initialState, action);

      expect(state).toEqual(initialState);
    });
  });

  describe('enterRequestPaymentWizard action', () => {
    test('should update the state in an immutable way', () => {
      const details = {
        origin: <RequestPaymentOrigin>'ACCOUNT_OPENING',
        originatingPath: '/task-details/100002',
        parentRequestId: '1000002',
        candidateRecipients: [],
      };

      const newState = {
        ...initialState,
        origin: details.origin,
        originatingPath: details.originatingPath,
        parentRequestId: details.parentRequestId,
        candidateRecipients: details.candidateRecipients,
      };

      const state = requestPaymentReducer(
        initialState,
        enterRequestPaymentWizard(details)
      );

      expect(state).toEqual(newState);
      expect(state).not.toBe(initialState);
    });
  });

  describe('setPaymentDetails action', () => {
    test('should update the state in an immutable way', () => {
      const details = {
        recipientUrid: 'UK367902749814',
        recipientName: 'Test Pay recipient',
        amount: 890,
        description: 'A test payment',
      };

      const newState = {
        ...initialState,
        recipientUrid: details.recipientUrid,
        recipientName: details.recipientName,
        amount: details.amount,
        description: details.description,
      };

      const state = requestPaymentReducer(
        initialState,
        setPaymentDetails(details)
      );

      expect(state).toEqual(newState);
      expect(state).not.toBe(initialState);
    });
  });

  describe('clearRequestPayment action', () => {
    test('should clear the state', () => {
      const existingState: RequestPaymentState = {
        origin: 'ACCOUNT_OPENING',
        originatingPath: '/task-details/100002',
        parentRequestId: '1000002',
        candidateRecipients: [],
        recipientUrid: 'UK367902749814',
        recipientName: 'Test Pay recipient',
        amount: 890,
        description: 'A test payment',
        submittedRequestIdentifier: '1000035',
      };

      const state = requestPaymentReducer(existingState, clearRequestPayment());

      expect(state).toEqual(initialState);
    });
  });

  describe('submitPaymentRequestSuccess action', () => {
    test('should update the state in an immutable way', () => {
      const existingState: RequestPaymentState = {
        origin: 'ACCOUNT_OPENING',
        originatingPath: '/task-details/100002',
        parentRequestId: '1000002',
        candidateRecipients: [],
        recipientUrid: 'UK367902749814',
        recipientName: 'Test Pay recipient',
        amount: 890,
        description: 'A test payment',
        submittedRequestIdentifier: null,
      };

      const state = requestPaymentReducer(
        existingState,
        submitPaymentRequestSuccess({
          submittedRequestIdentifier: '1000035',
        })
      );

      expect(state).toEqual({
        ...existingState,
        submittedRequestIdentifier: '1000035',
      });
    });
  });
});
