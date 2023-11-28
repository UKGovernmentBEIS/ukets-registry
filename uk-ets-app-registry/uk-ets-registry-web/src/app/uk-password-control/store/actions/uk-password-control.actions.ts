import { createAction, props } from '@ngrx/store';
import { Score } from '@uk-password-control/model';

export const loadPasswordScore = createAction(
  '[Password Strength] Load Password Score',
  props<{
    readonly password: string;
  }>()
);

export const loadPasswordScoreSuccess = createAction(
  '[Password Strength] Load Password Score Success',
  props<{
    readonly score: Score;
  }>()
);

export const clearPasswordStrength = createAction(
  '[Password Strength] Clear Password Strength'
);
