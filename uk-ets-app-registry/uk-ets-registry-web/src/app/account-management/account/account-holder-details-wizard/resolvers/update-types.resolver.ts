import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { FormRadioOption } from '@shared/form-controls/uk-radio-input/uk-radio.model';
import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { selectUpdateTypes } from '@account-management/account/account-holder-details-wizard/reducers';
import { take } from 'rxjs/operators';
import { Observable } from 'rxjs';

@Injectable()
export class UpdateTypesResolver {
  constructor(private store: Store) {}

  resolve(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<FormRadioOption[]> {
    return this.store.select(selectUpdateTypes).pipe(take(1));
  }
}
