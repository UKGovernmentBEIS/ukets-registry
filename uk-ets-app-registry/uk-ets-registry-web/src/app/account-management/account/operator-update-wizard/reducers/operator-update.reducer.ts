import { Operator } from '@shared/model/account';
import { Action, createReducer } from '@ngrx/store';
import { mutableOn } from '@shared/mutable-on';
import { OperatorUpdateActions } from '@operator-update/actions';

export const operatorUpdateFeatureKey = 'operator-update';

export interface OperatorUpdateState {
  operator: Operator;
  newOperator: Operator;
  submittedRequestIdentifier: string;
}

export const initialState: OperatorUpdateState = {
  operator: null,
  newOperator: null,
  submittedRequestIdentifier: null,
};

const talOperatorUpdateReducer = createReducer(
  initialState,
  mutableOn(
    OperatorUpdateActions.setCurrentOperatorInfoSuccess,
    (state, { operator }) => {
      state.operator = operator;
    }
  ),
  mutableOn(
    OperatorUpdateActions.setNewOperatorInfoSuccess,
    (state, { operator }) => {
      state.newOperator = operator;
    }
  ),
  mutableOn(
    OperatorUpdateActions.setUpdateOperatorInfoSuccess,
    (state, { operator }) => {
      state.newOperator = operator;
    }
  ),
  mutableOn(
    OperatorUpdateActions.submitUpdateRequestSuccess,
    (state, { requestId }) => {
      state.submittedRequestIdentifier = requestId;
    }
  ),
  mutableOn(OperatorUpdateActions.clearOperatorUpdateRequest, (state) => {
    resetState(state);
  })
);

export function reducer(
  state: OperatorUpdateState | undefined,
  action: Action
) {
  return talOperatorUpdateReducer(state, action);
}

function resetState(state) {
  state.operator = initialState.operator;
  state.newOperator = initialState.newOperator;
  state.submittedRequestIdentifier = initialState.submittedRequestIdentifier;
}
