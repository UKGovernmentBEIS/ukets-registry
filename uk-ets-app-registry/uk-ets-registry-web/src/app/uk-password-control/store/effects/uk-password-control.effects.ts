import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { map, mergeMap } from 'rxjs/operators';
import { PasswordStrengthValidatorService } from '@uk-password-control/validation';
import * as PasswordStrengthActions from '@uk-password-control/store/actions';

@Injectable()
export class UkPasswordControlEffects {
  constructor(
    private passwordStrengthValidatorService: PasswordStrengthValidatorService,
    private actions$: Actions
  ) {}

  /**
   * load Password Score
   */
  loadPasswordScore$ = createEffect(() =>
    this.actions$.pipe(
      ofType(PasswordStrengthActions.loadPasswordScore),
      mergeMap((action) =>
        this.passwordStrengthValidatorService
          .getPasswordStrength({ password: action.password })
          .pipe(
            map((response) =>
              PasswordStrengthActions.loadPasswordScoreSuccess({
                score: response.score,
              })
            )
          )
      )
    )
  );
}
