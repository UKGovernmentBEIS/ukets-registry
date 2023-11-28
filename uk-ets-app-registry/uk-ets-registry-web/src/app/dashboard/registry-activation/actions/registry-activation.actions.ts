import { createAction, props } from '@ngrx/store';

export enum RegistryActivationActionTypes {
  ENROL_USER = '[Registry Activation] Enrol user'
}

export const enrolUser = createAction(
  RegistryActivationActionTypes.ENROL_USER,
  props<{ enrolmentKey: string }>()
);

export const requestActivationCode = createAction(
  '[Registry Activation] Request Activation Code',
  props<{ backRoute: string }>()
);

export const submitNewRegistryActivationCodeRequest = createAction(
  '[Registry Activation API] Submit New Registry Activation Code Request'
);

export const submitNewRegistryActivationCodeRequestSuccess = createAction(
  '[Registry Activation API] Submit New Registry Activation Code Request Success',
  props<{ requestId: string }>()
);

export const submitNewRegistryActivationCodeRequestFailure = createAction(
  '[Registry Activation API] Submit New Registry Activation Code Request Failure'
);
