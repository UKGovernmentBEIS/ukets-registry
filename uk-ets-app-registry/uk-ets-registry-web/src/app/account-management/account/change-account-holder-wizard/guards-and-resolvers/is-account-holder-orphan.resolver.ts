import { inject, Injectable } from '@angular/core';
import { Resolve } from '@angular/router';
import { Observable, of, switchMap, tap } from 'rxjs';
import { AccountHolderChangeService } from '@change-account-holder-wizard/service';
import { Store } from '@ngrx/store';
import { selectAccount } from '@account-management/account/account-details/account.selector';
import { ChangeAccountHolderWizardActions } from '@change-account-holder-wizard/store/actions';

@Injectable({ providedIn: 'root' })
export class IsAccountHolderOrphanResolver implements Resolve<boolean> {
  private readonly store = inject(Store);
  private readonly accountHolderChangeService = inject(
    AccountHolderChangeService
  );
  private readonly account$ = this.store.select(selectAccount);

  resolve(): Observable<boolean> {
    return this.account$.pipe(
      switchMap((account) =>
        account?.accountHolder?.id && account?.identifier
          ? this.accountHolderChangeService.getAccountHolderOrphan(
              account.accountHolder.id,
              account.identifier
            )
          : of(null)
      ),
      tap((isAccountHolderOrphan) =>
        this.store.dispatch(
          ChangeAccountHolderWizardActions.RESOLVE_IS_ACCOUNT_HOLDER_ORPHAN({
            isAccountHolderOrphan,
          })
        )
      )
    );
  }
}
