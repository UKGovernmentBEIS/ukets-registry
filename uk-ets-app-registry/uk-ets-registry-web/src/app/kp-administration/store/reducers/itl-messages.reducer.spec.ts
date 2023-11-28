import { reducer, initialState } from './itl-messages.reducer';
import {
  clearState,
  hideCriteria,
  messagesLoaded,
  resetResultsLoaded,
  showCriteria
} from '../actions/itl-messages.actions';

import { SortParameters } from '@shared/search/sort/SortParameters';
import { Pagination } from '@shared/search/paginator';
import {
  MessageSearchCriteria,
  MessageSearchResult
} from '@kp-administration/itl-messages/model';

describe('ITL Message List reducer', () => {
  it('resets the results loaded flag', () => {
    const beforeResetState = reducer(initialState, {} as any);
    beforeResetState.resultsLoaded = true;
    expect(beforeResetState.resultsLoaded).toBeTruthy();

    const resetAction = resetResultsLoaded();
    const afterResetState = reducer(beforeResetState, resetAction);
    expect(afterResetState.resultsLoaded).toBeFalsy();
  });

  it('sets the hide criteria flag', () => {
    const beforeHideCriteriaActionState = reducer(initialState, {} as any);
    expect(beforeHideCriteriaActionState.hideCriteria).toBeFalsy();
    const hideCriteriaAction = hideCriteria();
    const afterHideCriteriaActionState = reducer(
      beforeHideCriteriaActionState,
      hideCriteriaAction
    );
    expect(afterHideCriteriaActionState.hideCriteria).toBeTruthy();
  });

  it('sets the show criteria flag', () => {
    const beforeShowCriteriaActionState = reducer(initialState, {} as any);
    beforeShowCriteriaActionState.hideCriteria = true;
    expect(beforeShowCriteriaActionState.hideCriteria).toBeTruthy();
    const showCriteriaAction = showCriteria();
    const afterShowCriteriaActionState = reducer(
      beforeShowCriteriaActionState,
      showCriteriaAction
    );
    expect(afterShowCriteriaActionState.hideCriteria).toBeFalsy();
  });

  it('sets the results', () => {
    const criteria: MessageSearchCriteria = {
      messageId: undefined,
      messageDateFrom: undefined,
      messageDateTo: undefined
    };

    const sortParameters: SortParameters = {
      sortField: 'messageId',
      sortDirection: 'ASC'
    };

    const results: MessageSearchResult[] = [
      {
        messageId: 45,
        from: 'GB',
        to: 'ITL',
        messageDate: '2020-11-04T16:39:09.00',
        content: 'A1 content'
      },
      {
        messageId: 33,
        from: 'GB',
        to: 'ITL',
        messageDate: '2020-12-15T11:00:03.34',
        content: 'A2 content'
      },
      {
        messageId: 39,
        from: 'GB',
        to: 'ITL',
        messageDate: '2020-09-22T12:19:23.09',
        content: 'A3 content'
      }
    ];

    const pagination: Pagination = {
      currentPage: 0,
      totalResults: 3,
      pageSize: 10
    };

    const beforeMessagesLoadedActionState = reducer(initialState, {} as any);
    expect(beforeMessagesLoadedActionState.results).toBeUndefined();
    const messagesLoadedAction = messagesLoaded({
      results,
      pagination,
      sortParameters,
      criteria
    });
    const afterMessagesLoadedActionState = reducer(
      beforeMessagesLoadedActionState,
      messagesLoadedAction
    );
    expect(afterMessagesLoadedActionState.results).toStrictEqual(results);
    expect(afterMessagesLoadedActionState.pageParameters).toStrictEqual({
      page: -1,
      pageSize: 10
    });
    expect(afterMessagesLoadedActionState.pagination).toStrictEqual(pagination);
    expect(afterMessagesLoadedActionState.criteria).toStrictEqual(criteria);
  });

  it('sets the show criteria flag', () => {
    const criteria: MessageSearchCriteria = {
      messageId: undefined,
      messageDateFrom: undefined,
      messageDateTo: undefined
    };

    const sortParameters: SortParameters = {
      sortField: 'messageId',
      sortDirection: 'ASC'
    };

    const results: MessageSearchResult[] = [
      {
        messageId: 45,
        from: 'GB',
        to: 'ITL',
        messageDate: '2020-11-04T16:39:09.00',
        content: 'A1 content'
      },
      {
        messageId: 33,
        from: 'ITL',
        to: 'GB',
        messageDate: '2020-12-15T11:00:03.34',
        content: 'A2 content'
      },
      {
        messageId: 39,
        from: 'GB',
        to: 'ITL',
        messageDate: '2020-09-22T12:19:23.09',
        content: 'A3 content'
      }
    ];

    const pagination: Pagination = {
      currentPage: 0,
      totalResults: 3,
      pageSize: 10
    };

    const beforeClearActionState = reducer(initialState, {} as any);
    beforeClearActionState.criteria = criteria;
    beforeClearActionState.results = results;
    beforeClearActionState.pagination = pagination;
    expect(beforeClearActionState.criteria).toStrictEqual(criteria);
    expect(beforeClearActionState.results).toStrictEqual(results);
    expect(beforeClearActionState.pagination).toStrictEqual(pagination);

    const afterClearActionState = reducer(beforeClearActionState, clearState());
    expect(afterClearActionState).toStrictEqual(initialState);
  });
});
