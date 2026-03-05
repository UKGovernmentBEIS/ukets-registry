import { inject } from '@angular/core';
import {
  CanActivateFn,
  CanDeactivateFn,
  createUrlTreeFromSnapshot,
  Router,
} from '@angular/router';
import { Store } from '@ngrx/store';
import { TaskService } from '@registry-web/shared/services/task-service';
import { catchError, exhaustMap, map, of } from 'rxjs';
import { RegulatorNoticeDetailsActions } from '@regulator-notice-management/details/store';
import { REGULATOR_NOTICE_LIST_PATH } from '@regulator-notice-management/list';
import { RegulatorNoticeTaskDetails } from '@shared/task-and-regulator-notice-management/model';
import { AuthApiService } from '@registry-web/auth/auth-api.service';

export const regulatorNoticeDetailsCanActivateGuard: CanActivateFn = (
  route
) => {
  const taskIdParam = 'requestId';
  const router = inject(Router);
  const store = inject(Store);
  const taskService = inject(TaskService);

  const id = route.paramMap.get(taskIdParam);
  if (!route.paramMap.has(taskIdParam)) {
    console.warn(`No :${taskIdParam} param in route`);
    return true;
  }

  const hasAdminPermissions$ = inject(AuthApiService).hasScope(
    'urn:uk-ets-registry-api:actionForAnyAdmin'
  );

  return hasAdminPermissions$.pipe(
    exhaustMap((hasAdminPermissions) => {
      return hasAdminPermissions
        ? taskService.fetchOneTask(id).pipe(
            map((noticeDetails: RegulatorNoticeTaskDetails) => {
              store.dispatch(
                RegulatorNoticeDetailsActions.LOAD_DETAILS({
                  noticeDetails: noticeDetails,
                })
              );
              return true;
            }),
            catchError((error) =>
              of(router.createUrlTree([REGULATOR_NOTICE_LIST_PATH]))
            )
          )
        : of(createUrlTreeFromSnapshot(route, ['../../']));
    })
  );
};

export const regulatorNoticeDetailsCanDeactivateGuard: CanDeactivateFn<
  unknown
> = () => {
  inject(Store).dispatch(RegulatorNoticeDetailsActions.RESET());
  return true;
};
