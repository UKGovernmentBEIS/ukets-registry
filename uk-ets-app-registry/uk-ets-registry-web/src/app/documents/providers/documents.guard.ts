import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable, of } from 'rxjs';
import { catchError, switchMap, take, tap } from 'rxjs/operators';
import { selectCategories } from '../store/documents.selectors';
import { RegistryDocumentCategory } from '../models/document.model';
import { fetchCategories } from '../store/documents.actions';

@Injectable()
export class DocumentsGuard {
  constructor(private store: Store) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> {
    return this.getCategories().pipe(
      switchMap(() => of(true)),
      catchError(() => of(false))
    );
  }

  canDeactivate(
    component: any,
    currentRoute: ActivatedRouteSnapshot,
    currentState: RouterStateSnapshot,
    nextState?: RouterStateSnapshot
  ): Observable<boolean> {
    return of(true);
  }

  private getCategories() {
    return this.store.select(selectCategories).pipe(
      tap((data) => this.prefetch(data)),
      take(1)
    );
  }

  private prefetch(data: RegistryDocumentCategory[]) {
    this.store.dispatch(fetchCategories());
  }
}
