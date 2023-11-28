import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { MemoizedSelector, select, Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { filter, first } from 'rxjs/operators';

export abstract class DetailsResolver<S> {
  // TODO: Generic Typing might not be needed any more
  // eslint-disable-next-line
  protected constructor(protected store: Store<any>) {}

  resolve(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<any> {
    this.loadDetails(route);
    return this.store.pipe(
      select(this.getDetailsLoadedFlagSelector()),
      filter((loaded) => loaded),
      first()
    );
  }

  /**
   * The selector of the details loaded flag in feature stor
   */
  protected abstract getDetailsLoadedFlagSelector(): MemoizedSelector<
    S,
    boolean
  >;

  /**
   * Commonly it dispatches an action which starts the data loading
   */
  protected abstract loadDetails(route: ActivatedRouteSnapshot);
}
