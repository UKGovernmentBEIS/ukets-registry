import { Action } from '@ngrx/store';

export function verifyBeforeAndAfterActionDispatched<S>(
  reducer: (state: S, action: Action) => S,
  beforeActionDispatchedState: S,
  verifyBeforeActionDispatched: (state: S) => void,
  action: Action,
  verifyAfterActionDispatched: (state: S) => void
) {
  const initialState = reducer(beforeActionDispatchedState, {} as any);
  verifyBeforeActionDispatched(initialState);
  const afterLoadDetailsActionState = reducer(initialState, action);
  verifyAfterActionDispatched(afterLoadDetailsActionState);
}
