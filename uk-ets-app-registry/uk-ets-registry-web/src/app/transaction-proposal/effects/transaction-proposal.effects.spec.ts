import { Observable } from 'rxjs';
import { TestBed } from '@angular/core/testing';
import { provideMockActions } from '@ngrx/effects/testing';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import { ApiErrorHandlingService } from '@shared/services';
import { cold, hot } from 'jasmine-marbles';
import { TransactionProposalService } from '../services/transaction-proposal.service';
import { TransactionProposalEffects } from './transaction-proposal.effects';
import { SelectUnitTypesActions } from '../actions';
import {
  CommitmentPeriod,
  ReturnExcessAllocationTransactionSummary,
  TransactionSummary,
  TransactionType,
  UnitType,
} from '@shared/model/transaction';
import {
  selectExcessAmount,
  selectReturnExcessAllocationTransactionSummary,
  selectReturnExcessAllocationType,
  selectTotalQuantityOfSelectedTransactionBlocks,
  selectTransactionSummary,
} from '../reducers/transaction-proposal.selector';
import { TransactionProposalState } from '../reducers';
import { ReturnExcessAllocationType } from '@shared/model/allocation';
import { setCalculatedReturnExcessAllocationQuantitiesAndType } from '../actions/select-unit-types.actions';

describe('TransactionProposalEffects', () => {
  const transactionSummaryExcessAllocation = {
    identifier: null,
    reversedIdentifier: null,
    acquiringAccountIdentifier: null,
    acquiringAccountFullIdentifier: null,
    toBeReplacedBlocksAccountFullIdentifier: null,
    transferringAccountIdentifier: 234434,
    attributes: null,
    itlNotification: null,
    type: TransactionType.ExcessAllocation,
    blocks: [
      {
        type: UnitType.ALLOWANCE,
        originalPeriod: CommitmentPeriod.CP0,
        applicablePeriod: CommitmentPeriod.CP0,
        environmentalActivity: null,
        index: 0,
        quantity: '23',
        availableQuantity: 893,
      },
    ],
    comment: 'A comment',
    allocationYear: 2023,
    allocationType: 'NAT_AND_NER',
    reference: null,
    returnExcessAllocationType: 'NAT_AND_NER' as ReturnExcessAllocationType,
  };
  const transactionSummaryTransferAllowances = {
    identifier: null,
    reversedIdentifier: null,
    acquiringAccountIdentifier: null,
    acquiringAccountFullIdentifier: null,
    toBeReplacedBlocksAccountFullIdentifier: null,
    transferringAccountIdentifier: 234434,
    attributes: null,
    itlNotification: null,
    type: TransactionType.TransferAllowances,
    blocks: [
      {
        type: UnitType.ALLOWANCE,
        originalPeriod: CommitmentPeriod.CP0,
        applicablePeriod: CommitmentPeriod.CP0,
        environmentalActivity: null,
        index: 0,
        quantity: '88',
        availableQuantity: 991,
      },
    ],
    comment: 'A comment',
    allocationYear: null,
    allocationType: null,
    reference: null,
    returnExcessAllocationType: null,
  };
  const returnExcessAllocationTransactionSummary: ReturnExcessAllocationTransactionSummary =
    {
      natQuantity: 55,
      nerQuantity: 30,
      natReturnTransactionIdentifier: null,
      nerReturnTransactionIdentifier: null,
      natAcquiringAccountInfo: null,
      nerAcquiringAccountInfo: null,
      transferringAccountIdentifier: 345534,
      attributes: null,
      type: TransactionType.ExcessAllocation,
      blocks: [
        {
          type: UnitType.ALLOWANCE,
          originalPeriod: CommitmentPeriod.CP0,
          applicablePeriod: CommitmentPeriod.CP0,
          index: 0,
          quantity: '85',
          availableQuantity: 456,
          environmentalActivity: null,
        },
      ],
      comment: 'Type your comment.',
      allocationYear: 2022,
      reference: null,
      returnExcessAllocationType: 'NAT_AND_NER',
    };
  let actions: Observable<any>;
  let effects: TransactionProposalEffects;
  let transactionProposalService: TransactionProposalService;
  let apiErrorHandlingService: ApiErrorHandlingService;

  let mockStore: MockStore<TransactionProposalState>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        TransactionProposalEffects,
        ApiErrorHandlingService,
        provideMockStore(),
        provideMockActions(() => actions),
        {
          provide: TransactionProposalService,
          useValue: {
            fetchAllocation: jest.fn(),
            fetchAllocationStatus: jest.fn(),
            updateAllocationStatus: jest.fn(),
          },
        },
      ],
    });

    afterEach(() => {
      mockStore?.resetSelectors();
    });

    effects = TestBed.inject(TransactionProposalEffects);
    transactionProposalService = TestBed.inject(TransactionProposalService);
    apiErrorHandlingService = TestBed.inject(ApiErrorHandlingService);
    mockStore = TestBed.inject(MockStore);
  });

  it('should be created', () => {
    expect(effects).toBeTruthy();
  });

  describe('selectUnitDone$', () => {
    it('should return the [setCalculatedReturnExcessAllocationQuantitiesAndType] action', () => {
      mockStore.overrideSelector(
        selectTransactionSummary,
        transactionSummaryExcessAllocation
      );
      actions = hot('-a', {
        a: SelectUnitTypesActions.setSelectedBlockSummaries({
          selectedTransactionBlockSummaries: [
            {
              index: 0,
              type: UnitType.ALLOWANCE,
              originalPeriod: CommitmentPeriod.CP0,
              applicablePeriod: CommitmentPeriod.CP0,
              quantity: '10',
              availableQuantity: 390,
              environmentalActivity: null,
            },
          ],
          clearNextStepsInWizard: false,
          toBeReplacedUnitsHoldingAccountParts: null,
        }),
      });
      const expectedEffectResponse = cold('-(b)', {
        b: SelectUnitTypesActions.setCalculatedReturnExcessAllocationQuantitiesAndType(),
      });
      expect(effects.selectUnitDone$).toBeObservable(expectedEffectResponse);
    });

    it('should return the [validateSelectedBlockSummaries] action with proper props', () => {
      mockStore.overrideSelector(
        selectTransactionSummary,
        transactionSummaryTransferAllowances
      );
      actions = hot('-a', {
        a: SelectUnitTypesActions.setSelectedBlockSummaries({
          selectedTransactionBlockSummaries: [
            {
              index: 0,
              type: UnitType.ALLOWANCE,
              originalPeriod: CommitmentPeriod.CP0,
              applicablePeriod: CommitmentPeriod.CP0,
              quantity: '10',
              availableQuantity: 390,
              environmentalActivity: null,
            },
          ],
          clearNextStepsInWizard: false,
          toBeReplacedUnitsHoldingAccountParts: null,
        }),
      });
      const expectedEffectResponse = cold('-(b)', {
        b: SelectUnitTypesActions.validateSelectedBlockSummaries({
          transactionSummary: transactionSummaryTransferAllowances,
        }),
      });
      expect(effects.selectUnitDone$).toBeObservable(expectedEffectResponse);
    });
  });

  describe('calculatedReturnExcessAllocationQuantitiesAndType$', () => {
    it('should return the [SelectUnitTypesActions.setReturnExcessAmountsAndType] action with proper props NAT_AND_NER', () => {
      mockStore.overrideSelector(selectExcessAmount, {
        returnToAllocationAccountAmount: 22,
        returnToNewEntrantsReserveAccount: 45,
      });
      mockStore.overrideSelector(
        selectTotalQuantityOfSelectedTransactionBlocks,
        67
      );
      mockStore.overrideSelector(
        selectReturnExcessAllocationType,
        'NAT_AND_NER'
      );
      actions = hot('-a', {
        a: setCalculatedReturnExcessAllocationQuantitiesAndType(),
      });
      const expectedEffectResponse = cold('-(b)', {
        b: SelectUnitTypesActions.setReturnExcessAmountsAndType({
          selectedExcessAmounts: {
            returnToAllocationAccountAmount: 22,
            returnToNewEntrantsReserveAccount: 45,
          },
          returnExcessAllocationType: 'NAT_AND_NER',
        }),
      });
      expect(
        effects.calculatedReturnExcessAllocationQuantitiesAndType$
      ).toBeObservable(expectedEffectResponse);
    });

    it('should return the [SelectUnitTypesActions.setReturnExcessAmountsAndType] action with proper props NAT_AND_NER to NER', () => {
      mockStore.overrideSelector(selectExcessAmount, {
        returnToAllocationAccountAmount: 22,
        returnToNewEntrantsReserveAccount: 45,
      });
      mockStore.overrideSelector(
        selectTotalQuantityOfSelectedTransactionBlocks,
        34
      );
      mockStore.overrideSelector(
        selectReturnExcessAllocationType,
        'NAT_AND_NER'
      );
      actions = hot('-a', {
        a: setCalculatedReturnExcessAllocationQuantitiesAndType(),
      });
      const expectedEffectResponse = cold('-(b)', {
        b: SelectUnitTypesActions.setReturnExcessAmountsAndType({
          selectedExcessAmounts: {
            returnToAllocationAccountAmount: 0,
            returnToNewEntrantsReserveAccount: 34,
          },
          returnExcessAllocationType: 'NER',
        }),
      });
      expect(
        effects.calculatedReturnExcessAllocationQuantitiesAndType$
      ).toBeObservable(expectedEffectResponse);
    });

    it('should return the [SelectUnitTypesActions.setReturnExcessAmountsAndType] action with proper props NER', () => {
      mockStore.overrideSelector(selectExcessAmount, {
        returnToAllocationAccountAmount: null,
        returnToNewEntrantsReserveAccount: 45,
      });
      mockStore.overrideSelector(
        selectTotalQuantityOfSelectedTransactionBlocks,
        67
      );
      mockStore.overrideSelector(
        selectReturnExcessAllocationType,
        'NAT_AND_NER'
      );
      actions = hot('-a', {
        a: setCalculatedReturnExcessAllocationQuantitiesAndType(),
      });
      const expectedEffectResponse = cold('-(b)', {
        b: SelectUnitTypesActions.setReturnExcessAmountsAndType({
          selectedExcessAmounts: {
            returnToAllocationAccountAmount: 0,
            returnToNewEntrantsReserveAccount: 67,
          },
          returnExcessAllocationType: 'NER',
        }),
      });
      expect(
        effects.calculatedReturnExcessAllocationQuantitiesAndType$
      ).toBeObservable(expectedEffectResponse);
    });

    it('should return the [SelectUnitTypesActions.setReturnExcessAmountsAndType] action with proper props NAT', () => {
      mockStore.overrideSelector(selectExcessAmount, {
        returnToAllocationAccountAmount: 33,
        returnToNewEntrantsReserveAccount: null,
      });
      mockStore.overrideSelector(
        selectTotalQuantityOfSelectedTransactionBlocks,
        67
      );
      mockStore.overrideSelector(selectReturnExcessAllocationType, 'NAT');
      actions = hot('-a', {
        a: setCalculatedReturnExcessAllocationQuantitiesAndType(),
      });
      const expectedEffectResponse = cold('-(b)', {
        b: SelectUnitTypesActions.setReturnExcessAmountsAndType({
          selectedExcessAmounts: {
            returnToAllocationAccountAmount: 67,
            returnToNewEntrantsReserveAccount: 0,
          },
          returnExcessAllocationType: 'NAT',
        }),
      });
      expect(
        effects.calculatedReturnExcessAllocationQuantitiesAndType$
      ).toBeObservable(expectedEffectResponse);
    });

    it('should return the [SelectUnitTypesActions.setReturnExcessAmountsAndType] action with proper props NAVAT', () => {
      mockStore.overrideSelector(selectExcessAmount, {
        returnToAllocationAccountAmount: 88,
        returnToNewEntrantsReserveAccount: null,
      });
      mockStore.overrideSelector(
        selectTotalQuantityOfSelectedTransactionBlocks,
        36
      );
      mockStore.overrideSelector(selectReturnExcessAllocationType, 'NAVAT');
      actions = hot('-a', {
        a: setCalculatedReturnExcessAllocationQuantitiesAndType(),
      });
      const expectedEffectResponse = cold('-(b)', {
        b: SelectUnitTypesActions.setReturnExcessAmountsAndType({
          selectedExcessAmounts: {
            returnToAllocationAccountAmount: 36,
            returnToNewEntrantsReserveAccount: 0,
          },
          returnExcessAllocationType: 'NAVAT',
        }),
      });
      expect(
        effects.calculatedReturnExcessAllocationQuantitiesAndType$
      ).toBeObservable(expectedEffectResponse);
    });
  });

  describe('setSelectedExcessAmounts$', () => {
    it('should return the [validateSelectedBlockSummaries] action with proper props', () => {
      mockStore.overrideSelector(selectReturnExcessAllocationType, null);
      mockStore.overrideSelector(
        selectTransactionSummary,
        transactionSummaryTransferAllowances
      );
      mockStore.overrideSelector(
        selectReturnExcessAllocationTransactionSummary,
        null
      );

      actions = hot('-a', {
        a: SelectUnitTypesActions.setReturnExcessAmountsAndType({
          selectedExcessAmounts: null,
          returnExcessAllocationType: null,
        }),
      });
      const expectedEffectResponse = cold('-(b)', {
        b: SelectUnitTypesActions.validateSelectedBlockSummaries({
          transactionSummary: transactionSummaryTransferAllowances,
        }),
      });
      expect(effects.setSelectedExcessAmounts$).toBeObservable(
        expectedEffectResponse
      );
    });
    it('should return the [validateSelectedBlockSummariesNatAndNer] action with proper props', () => {
      mockStore.overrideSelector(
        selectReturnExcessAllocationType,
        'NAT_AND_NER'
      );
      mockStore.overrideSelector(selectTransactionSummary, null);
      mockStore.overrideSelector(
        selectReturnExcessAllocationTransactionSummary,
        returnExcessAllocationTransactionSummary
      );

      actions = hot('-a', {
        a: SelectUnitTypesActions.setReturnExcessAmountsAndType({
          selectedExcessAmounts: null,
          returnExcessAllocationType: null,
        }),
      });
      const expectedEffectResponse = cold('-(b)', {
        b: SelectUnitTypesActions.validateSelectedBlockSummariesNatAndNer({
          returnExcessAllocationTransactionSummary,
        }),
      });
      expect(effects.setSelectedExcessAmounts$).toBeObservable(
        expectedEffectResponse
      );
    });
  });
});
