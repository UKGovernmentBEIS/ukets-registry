import { RequestPaymentOrigin } from '@request-payment/model';
import {
  RequestPaymentState,
  selectAmount,
  selectCandidateRecipients,
  selectDescription,
  selectOriginatingPath,
  selectParentRequestId,
  selectPaymentRequest,
  selectRecipientName,
  selectRecipientUrid,
  selectSubmittedRequestIdentifier,
} from '@request-payment/store/reducers';

describe('RequestPaymentSelectors', () => {
  const initialState: RequestPaymentState = {
    origin: <RequestPaymentOrigin>'ACCOUNT_OPENING',
    originatingPath: '/task-details/100002',
    parentRequestId: '1000002',
    candidateRecipients: [],
    recipientUrid: 'UK367902749814',
    recipientName: 'Test Pay recipient',
    amount: 890.65,
    description: 'A test payment',
    submittedRequestIdentifier: '72367326',
  };

  test('should select the submitted request identifier', () => {
    const result = selectSubmittedRequestIdentifier.projector(initialState);
    expect(result).toEqual(initialState.submittedRequestIdentifier);
    expect(result).toEqual('72367326');
  });

  test('should select the amount', () => {
    const result = selectAmount.projector(initialState);
    expect(result).toEqual(initialState.amount);
    expect(result).toEqual(890.65);
  });

  test('should select the candidate recipients', () => {
    const result = selectCandidateRecipients.projector(initialState);
    expect(result).toEqual(initialState.candidateRecipients);
    expect(result.length).toEqual(0);
  });

  test('should select the recipient urid', () => {
    const result = selectRecipientUrid.projector(initialState);
    expect(result).toEqual(initialState.recipientUrid);
    expect(result).toEqual('UK367902749814');
  });

  test('should select the recipient name', () => {
    const result = selectRecipientName.projector(initialState);
    expect(result).toEqual(initialState.recipientName);
    expect(result).toEqual('Test Pay recipient');
  });

  test('should select the description', () => {
    const result = selectDescription.projector(initialState);
    expect(result).toEqual(initialState.description);
    expect(result).toEqual('A test payment');
  });

  test('should select the origin', () => {
    const result = selectOriginatingPath.projector(initialState);
    expect(result).toEqual(initialState.originatingPath);
    expect(result).toEqual('/task-details/100002');
  });

  test('should select the parent request identifier', () => {
    const result = selectParentRequestId.projector(initialState);
    expect(result).toEqual(initialState.parentRequestId);
    expect(result).toEqual('1000002');
  });

  test('should select the payment request', () => {
    const result = selectPaymentRequest.projector(
      initialState.parentRequestId,
      initialState.amount,
      initialState.description,
      initialState.recipientUrid
    );
    expect(result).toEqual({
      amount: 890.65,
      description: 'A test payment',
      parentRequestId: '1000002',
      recipientUrid: 'UK367902749814',
    });
  });
});
