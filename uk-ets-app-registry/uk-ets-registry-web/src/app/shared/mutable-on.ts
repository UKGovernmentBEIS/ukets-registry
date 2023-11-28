import produce, { Draft, enableAllPlugins } from 'immer';
import {
  ActionCreator,
  ActionReducer,
  ActionType,
  on,
  ReducerTypes,
} from '@ngrx/store';

/**
 * TODO
 * This code is copied from the ngrx-immer library: https://github.com/timdeschryver/ngrx-immer.
 * As long as the IE support still exists in the project, the enableAllPlugins call must be present.
 * Therefore, for the sake of other core library updates, such as the ngrx/store, this wrapper code may stay here
 * until we remove the IE support completely, which will be inevitable in Angular 13 either way.
 */
enableAllPlugins();

/**
 * Helper method that wraps a reducer with the Immer `produce` method
 */
function immerReducer<State, Next>(
  callback: (state: State, next: Next) => State | void
) {
  return (state: State | undefined, next: Next) => {
    return produce(state, (draft: State) => callback(draft, next)) as State;
  };
}

/**
 * An immer reducer that allows a void return
 */
interface ImmerOnReducer<State, AC extends ActionCreator[]> {
  (state: Draft<State>, action: ActionType<AC[number]>): void;
}

/**
 * Immer wrapper around `on` to mutate state
 */
export function mutableOn<State, Creators extends ActionCreator[]>(
  ...args: [...creators: Creators, reducer: ImmerOnReducer<State, Creators>]
): ReducerTypes<State, Creators> {
  const reducer = args.pop() as Function as ActionReducer<State>;
  return (on as any)(...(args as ActionCreator[]), immerReducer(reducer));
}
