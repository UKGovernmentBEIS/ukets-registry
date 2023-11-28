import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { Observable, of } from 'rxjs';
import { AccountHoldingDetailsService } from '@account-management/account/account-details/holdings/account-holding-details.service';
import {
  AccountHoldingDetails,
  AccountHoldingDetailsCriteria,
} from '@account-management/account/account-details/holdings/account-holding-details.model';
import { Store } from '@ngrx/store';
import { canGoBack } from '@shared/shared.action';
import { catchError, map } from 'rxjs/operators';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { AccountType } from '@shared/model/account';

@Injectable()
export class HoldingDetailsResolver {
  constructor(
    private service: AccountHoldingDetailsService,
    private router: Router,
    private store: Store
  ) {}

  resolve(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<{ details: AccountHoldingDetails; accountType: AccountType }> {
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${route.paramMap.get('accountId')}`,
        extras: { queryParams: { selectedSideMenu: 'Holdings' } },
      })
    );
    const criteria: AccountHoldingDetailsCriteria =
      this.router.getCurrentNavigation().extras.state.criteria;
    const accountType =
      AccountType[this.router.getCurrentNavigation().extras.state.accountType];

    return this.service.fetch(criteria).pipe(
      map((details: AccountHoldingDetails) => ({
        details,
        accountType,
      })),
      catchError((httpError: any) => {
        return of({
          accountType,
          details: {
            unit: null,
            originalPeriod: null,
            applicablePeriod: null,
            results: [],
            errorSummary: new ErrorSummary([
              new ErrorDetail(null, 'An unexpected error occurred.'),
            ]),
          },
        });
      })
    );
  }
}
