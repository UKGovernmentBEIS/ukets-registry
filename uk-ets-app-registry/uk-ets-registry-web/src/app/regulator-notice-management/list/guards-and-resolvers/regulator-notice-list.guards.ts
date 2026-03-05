import { inject } from '@angular/core';
import { CanActivateFn, createUrlTreeFromSnapshot } from '@angular/router';
import { catchError, exhaustMap, map, of } from 'rxjs';
import { AuthApiService } from '@registry-web/auth/auth-api.service';
import { TaskService } from '@shared/services/task-service';
import { Store } from '@ngrx/store';
import { RegulatorNoticeListActions } from '@regulator-notice-management/list/store';

export const canActivateRegulatorNoticeList: CanActivateFn = (route) => {
  const store = inject(Store);
  const taskService = inject(TaskService);

  const hasAdminPermissions$ = inject(AuthApiService).hasScope(
    'urn:uk-ets-registry-api:actionForAnyAdmin'
  );

  return hasAdminPermissions$.pipe(
    exhaustMap((hasAdminPermissions) => {
      return hasAdminPermissions
        ? taskService.getRegulatorNoticeProcessTypesList().pipe(
            map((processTypesList) => {
              store.dispatch(
                RegulatorNoticeListActions.SET_PROCESS_TYPES_LIST({
                  processTypesList,
                })
              );
              return true;
            }),
            catchError(() => of(createUrlTreeFromSnapshot(route, ['../'])))
          )
        : of(createUrlTreeFromSnapshot(route, ['../']));
    })
  );
};
