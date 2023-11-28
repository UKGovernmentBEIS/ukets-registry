import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import {
  navigateToEmailChangeWizard,
  navigateToVerificationPage
} from '@email-change/action/email-change.actions';
import { concatMap, switchMap } from 'rxjs/operators';
import { canGoBack, navigateTo } from '@shared/shared.action';
import { EmailChangeRoutePath } from '@email-change/model';
import { NavigationExtras } from '@angular/router';

@Injectable()
export class EmailChangeNavigationEffect {
  constructor(private actions$: Actions) {}

  navigateToEmailChangeWizard$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(navigateToEmailChangeWizard),
      concatMap(
        (action: {
          urid: string;
          caller: {
            route: string;
            extras?: NavigationExtras;
          };
        }) => {
          return [
            navigateTo({
              route: `/${EmailChangeRoutePath.BASE_PATH}`
            })
          ];
        }
      )
    );
  });

  navigateToVerificationPage$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(navigateToVerificationPage),
      concatMap(() => {
        return [
          canGoBack({
            goBackRoute: null
          }),
          navigateTo({
            route: `/${EmailChangeRoutePath.BASE_PATH}/${EmailChangeRoutePath.EMAIL_CHANGE_REQUEST_VERIFICATION_PATH}`
          })
        ];
      })
    );
  });
}
