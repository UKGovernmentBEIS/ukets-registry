import { TransactionAtrributesPipe } from '@shared/pipes/transaction-atrributes.pipe';

describe('TransactionAtrributesPipe', () => {
  const pipe = new TransactionAtrributesPipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should return the correct attribute value', () => {
    expect(
      pipe.transform(
        '{"AllocationYear":2021,"AllocationType":"NAT"}',
        'AllocationType'
      )
    ).toEqual('NAT');
    expect(
      pipe.transform(
        '{"AllocationYear":2021,"AllocationType":"NAT"}',
        'AllocationYear'
      )
    ).toEqual(2021);
    expect(pipe.transform('', 'AllocationYear')).toEqual('');
    expect(
      pipe.transform('{"AllocationYear":2021,"AllocationType":"NAT"}', '')
    ).toEqual('');
  });
});
