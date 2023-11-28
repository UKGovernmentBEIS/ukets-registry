import { DetailsResolver } from '@shared/resolvers/details.resolver';
import { EmailChangeState } from '@email-change/reducer';
import { MemoizedSelector, Store } from '@ngrx/store';
import { ActivatedRouteSnapshot } from '@angular/router';
import { selectConfirmationLoaded } from '@email-change/reducer/email-change.selector';
import { confirmEmailChange } from '@email-change/action/email-change.actions';
import { Injectable } from '@angular/core';

@Injectable()
export class EmailChangeConfirmationResolver extends DetailsResolver<
  EmailChangeState
> {
  constructor(protected store: Store) {
    super(store);
  }

  protected getDetailsLoadedFlagSelector(): MemoizedSelector<
    EmailChangeState,
    boolean
  > {
    return selectConfirmationLoaded;
  }

  protected loadDetails(route: ActivatedRouteSnapshot) {
    this.store.dispatch(
      confirmEmailChange({
        token: route.params.token
      })
    );
  }
}
