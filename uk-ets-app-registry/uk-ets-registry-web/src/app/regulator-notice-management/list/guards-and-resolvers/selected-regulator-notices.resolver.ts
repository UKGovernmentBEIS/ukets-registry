import { inject, Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { first } from 'rxjs/operators';
import { Store } from '@ngrx/store';
import { RegulatorNoticeTask } from '@shared/task-and-regulator-notice-management/model/regulator-notice-list.model';
import { selectSelectedRegulatorNotices } from '@regulator-notice-management/list/store';

@Injectable()
export class SelectedRegulatorNoticesResolver {
  private readonly store = inject(Store);

  resolve(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<RegulatorNoticeTask[]> {
    return this.store.select(selectSelectedRegulatorNotices).pipe(first());
  }
}
