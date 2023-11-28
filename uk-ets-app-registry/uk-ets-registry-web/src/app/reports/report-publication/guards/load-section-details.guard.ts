import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { Store } from '@ngrx/store';
import { ErrorDetail } from '@registry-web/shared/error-summary';
import {
  getReportPublicationSection,
  loadReportPublicationHistory,
} from '@report-publication/actions';
import { catchError, filter, Observable, of, switchMap, take } from 'rxjs';
import { Section } from '../model';
import { selectSection } from '../selectors';

@Injectable({
  providedIn: 'root',
})
export class LoadSectionSetailsGuard {
  constructor(private router: Router, private store: Store) {}
  potentialErrors: Map<any, ErrorDetail>;
  sectionDetails: Section;

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> {
    this.store.dispatch(getReportPublicationSection());
    this.store.dispatch(
      loadReportPublicationHistory({
        sortParameters: {
          sortField: 'applicableForYear',
          sortDirection: 'DESC',
        },
        potentialErrors: this.potentialErrors,
      })
    );

    return this.store.select(selectSection).pipe(
      filter((data) => data != null),
      take(1),
      switchMap(() => of(true)),
      catchError(() => of(false))
    );
  }
}
