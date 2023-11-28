import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { canGoBack, errors } from '@shared/shared.action';
import { Store } from '@ngrx/store';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable, of, switchMap } from 'rxjs';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import {
  AccountDetails,
  AuthorisedRepresentative,
} from '@shared/model/account';
import {
  selectAccount,
  selectAuthorisedRepresentatives,
} from '@account-management/account/account-details/account.selector';
import {
  selectAllocationClassification,
  selectClosureComment,
  selectClosureDetails,
  selectPendingAllocationTaskExists,
} from '@account-management/account/account-closure-wizard/reducers';
import { AccountClosureWizardPathsModel } from '@account-management/account/account-closure-wizard/models';
import {
  cancelClicked,
  navigateTo,
  submitClosureRequest,
} from '@account-management/account/account-closure-wizard/actions';

@Component({
  selector: 'app-check-closure-details-container',
  template: `<app-check-closure-details
      [comment]="closureComment$ | async"
      [accountDetails]="accountDetails$ | async"
      [authorisedRepresentatives]="authorisedRepresentatives$ | async"
      [allocationClassification]="allocationClassification$ | async"
      [pendingAllocationTaskExists]="pendingAllocationTaskExists$ | async"
      [accountType]="accountType$ | async"
      (submitRequest)="onSubmit($event)"
      (navigateToEmitter)="navigateTo($event)"
    ></app-check-closure-details>
    <app-cancel-request-link
      (goToCancelScreen)="onCancel()"
    ></app-cancel-request-link>`,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CheckClosureDetailsContainerComponent implements OnInit {
  closureComment$: Observable<string>;
  accountDetails$: Observable<AccountDetails>;
  allocationClassification$: Observable<any>;
  authorisedRepresentatives$: Observable<AuthorisedRepresentative[]>;
  accountType$: Observable<string>;
  pendingAllocationTaskExists$: Observable<boolean>;

  constructor(
    private store: Store,
    private route: ActivatedRoute,
    private _router: Router
  ) {}

  ngOnInit(): void {
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.route.snapshot.paramMap.get(
          'accountId'
        )}/${AccountClosureWizardPathsModel.BASE_PATH}/${
          AccountClosureWizardPathsModel.CLOSURE_COMMENT
        }`,
        extras: { skipLocationChange: true },
      })
    );
    this.closureComment$ = this.store.select(selectClosureComment);
    this.accountDetails$ = this.store.select(selectClosureDetails);
    this.authorisedRepresentatives$ = this.store.select(
      selectAuthorisedRepresentatives
    );
    this.allocationClassification$ = this.store.select(
      selectAllocationClassification
    );
    this.pendingAllocationTaskExists$ = this.store.select(
      selectPendingAllocationTaskExists
    );
    this.accountType$ = this.store
      .select(selectAccount)
      .pipe(switchMap((account) => of(account.accountType)));
  }

  navigateTo(routePath: string) {
    this.store.dispatch(
      navigateTo({
        route: `/account/${this.route.snapshot.paramMap.get('accountId')}/${
          AccountClosureWizardPathsModel.BASE_PATH
        }/${AccountClosureWizardPathsModel.CLOSURE_COMMENT}`,
        extras: { skipLocationChange: true },
      })
    );
  }
  onSubmit(data) {
    this.store.dispatch(submitClosureRequest(data));
  }

  onCancel() {
    this.store.dispatch(
      cancelClicked({ route: this.route.snapshot['_routerState'].url })
    );
  }

  onError(value: ErrorDetail) {
    const summary: ErrorSummary = {
      errors: [value],
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
