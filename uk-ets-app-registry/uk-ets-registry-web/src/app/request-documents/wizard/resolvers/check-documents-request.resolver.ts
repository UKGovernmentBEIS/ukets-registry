import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { take } from 'rxjs/operators';
import { calculateGoBackPathFromCheckPage } from '@request-documents/wizard/reducers/request-document.selector';

@Injectable()
export class CheckDocumentsRequestResolver {
  constructor(private store: Store) {}

  resolve(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<string> {
    return this.store.select(calculateGoBackPathFromCheckPage).pipe(take(1));
  }
}
