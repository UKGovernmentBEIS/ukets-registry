/*
 * Copyright (c) 2019.
 *
 * UK Emission Trading Scheme.
 */

import { createAction, props } from '@ngrx/store';
import { User } from '../../shared/user/user';

export const submit = createAction(
  '[Check Answers and Submit] Submit',
  props<{ user: User; progressBarPosition: string }>()
);

export const changePersonalDetails = createAction(
  '[Check Answers and Submit] Submit',
  props<{ user: User; progressBarPosition: string }>()
);

export const changeWorkDetails = createAction(
  '[Check Answers and Submit] Change Work Details',
  props<{ progressBarPosition: string }>()
);

export const changePassword = createAction(
  '[Check Answers and Submit] Change Password',
  props<{ progressBarPosition: string }>()
);
