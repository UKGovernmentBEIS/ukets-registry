import { ActionReducerMap, MetaReducer } from '@ngrx/store';
import { environment } from '../../environments/environment';
import * as fromRegistration from '../registration/registration.reducer';
import * as fromAccountOpening from '../account-opening/account-opening.reducer';
import * as fromShared from '../shared/shared.reducer';
import * as fromAuth from '../auth/auth.reducer';
import * as fromRouter from '@ngrx/router-store';
import { AccountOpeningState } from '@account-opening/account-opening.model';

export interface State {
  registration: fromRegistration.RegistrationState;
  accountOpening: AccountOpeningState;
  shared: fromShared.SharedState;
  auth: fromAuth.AuthState;
  router: fromRouter.RouterReducerState<any>;
}

export const reducers: ActionReducerMap<State> = {
  registration: fromRegistration.reducer,
  accountOpening: fromAccountOpening.reducer,
  shared: fromShared.reducer,
  auth: fromAuth.reducer,
  router: fromRouter.routerReducer,
};

export const metaReducers: MetaReducer<State>[] = !environment.production
  ? []
  : [];
