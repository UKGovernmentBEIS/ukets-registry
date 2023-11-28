import { reducer, initialState } from '.';
import {
  loadIssuanceAllocationStatusSuccess,
  loadAllocationTableEventHistorySuccess
} from '../actions';
import { AllowanceReport } from '../../model';
import { DomainEvent } from '@shared/model/event';

describe('Issuance and Allocation Status reducer', () => {
  const issuanceAllocationStatuses: AllowanceReport[] = [
    {
      description: '2021',
      cap: 500000,
      entitlement: 1000000,
      issued: 900000,
      allocated: 700000,
      forAuction: 50000,
      auctioned: 100000
    },
    {
      description: '2022',
      cap: 500000,
      entitlement: 500000,
      issued: 100000,
      allocated: 0,
      forAuction: 0,
      auctioned: 0
    },
    {
      description: 'Total in phase',
      cap: 2600000,
      entitlement: 1500000,
      issued: 900000,
      allocated: 700000,
      forAuction: 50000,
      auctioned: 100000
    }
  ];

  const allocationTableEventHistory: DomainEvent[] = [
    {
      domainType: 'TYPE',
      domainId: '1',
      domainAction: 'actions',
      description: 'A description',
      creator: 'uk-ets-authority-user',
      creatorType: 'A type',
      creationDate: new Date()
    }
  ];

  it('sets the Issuance and Allocation statuses per year', () => {
    const beforeLoadState = reducer(initialState, {} as any);
    expect(beforeLoadState.issuanceAllocationStatuses).toEqual([]);

    const loadAction = loadIssuanceAllocationStatusSuccess({
      results: issuanceAllocationStatuses
    });
    const afterLoadState = reducer(initialState, loadAction);
    expect(afterLoadState.issuanceAllocationStatuses).toEqual(
      issuanceAllocationStatuses
    );
  });

  it('sets the Issuance and Allocation statuses per year', () => {
    const beforeLoadState = reducer(initialState, {} as any);
    expect(beforeLoadState.allocationTableEventHistory).toEqual([]);

    const loadAction = loadAllocationTableEventHistorySuccess({
      results: allocationTableEventHistory
    });
    const afterLoadState = reducer(initialState, loadAction);
    expect(afterLoadState.allocationTableEventHistory).toEqual(
      allocationTableEventHistory
    );
  });
});
