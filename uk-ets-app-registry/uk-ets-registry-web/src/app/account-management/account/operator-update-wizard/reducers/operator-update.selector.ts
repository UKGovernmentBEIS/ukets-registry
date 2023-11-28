import { createFeatureSelector, createSelector } from '@ngrx/store';
import {
  operatorUpdateFeatureKey,
  OperatorUpdateState,
} from '@operator-update/reducers/operator-update.reducer';

const selectOperatorUpdateState = createFeatureSelector<OperatorUpdateState>(
  operatorUpdateFeatureKey
);

export const selectCurrentOperatorInfo = createSelector(
  selectOperatorUpdateState,
  (state) => state.operator
);

export const selectNewOperatorInfo = createSelector(
  selectOperatorUpdateState,
  (state) => state.newOperator
);

export const selectOperator = createSelector(
  selectCurrentOperatorInfo,
  selectNewOperatorInfo,
  (currentOperator, newOperator) => {
    if (newOperator) {
      return { ...newOperator };
    } else {
      return { ...currentOperator };
    }
  }
);

export const selectSubmittedRequestIdentifier = createSelector(
  selectOperatorUpdateState,
  (state) => state.submittedRequestIdentifier
);
