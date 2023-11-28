import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { canGoBack } from '@shared/shared.action';
import { MainWizardRoutes } from '../../main-wizard.routes';
import { take } from 'rxjs/operators';
import { TrustedAccountList } from '../trusted-account-list';
import {
  selectTrustedAccountList,
  selectTrustedAccountListCompleted,
} from '../trusted-account-list.selector';
import { TrustedAccountListWizardRoutes } from '../trusted-account-list-wizard-properties';
import {
  completeWizard,
  deleteTrustedAccountList,
} from '../trusted-account-list.actions';
import { selectIsOHAOrAOHA } from '@account-opening/account-opening.selector';
import { getRuleLabel } from '@shared/model/account';

@Component({
  selector: 'app-overview',
  templateUrl: './overview.component.html',
})
export class OverviewComponent implements OnInit {
  trustedAccountList$: Observable<TrustedAccountList> = this.store.select(
    selectTrustedAccountList
  );
  trustedAccountListCompleted$: Observable<boolean> = this.store.select(
    selectTrustedAccountListCompleted
  );
  isOHAorAOHA$: Observable<boolean> = this.store.select(selectIsOHAOrAOHA);
  trustedAccountListWizardRoutes = TrustedAccountListWizardRoutes;

  question1 =
    'Do you want a second authorised representative to approve transfers of units to a trusted account?';

  question2 =
    'Do you want to allow transfers of units to accounts that are not on the trusted list?';

  question3 =
    'Do you want a second authorised representative to approve a surrender transaction or a return of excess allocation?';

  readonly mainWizardRoute = MainWizardRoutes.TASK_LIST;
  readonly secondApprovalNecessaryRoute =
    TrustedAccountListWizardRoutes.SECOND_APPROVAL_NECESSARY;
  readonly transfersOutsideListRoute =
    TrustedAccountListWizardRoutes.TRANSFERS_OUTSIDE_LIST_ALLOWED;

  constructor(
    private route: ActivatedRoute,
    private _router: Router,
    private store: Store
  ) {}

  ngOnInit() {
    this.trustedAccountListCompleted$.pipe(take(1)).subscribe((completed) => {
      if (completed) {
        this.store.dispatch(
          canGoBack({
            goBackRoute: this.mainWizardRoute,
            extras: { skipLocationChange: true },
          })
        );
      } else {
        this.store.dispatch(
          canGoBack({
            goBackRoute: this.transfersOutsideListRoute,
            extras: { skipLocationChange: true },
          })
        );
      }
    });
  }

  onEdit() {
    this._router.navigate([this.secondApprovalNecessaryRoute], {
      skipLocationChange: true,
    });
  }

  onApply() {
    this.store.dispatch(completeWizard({ complete: true }));
    this._router.navigate([this.mainWizardRoute], { skipLocationChange: true });
  }

  onDelete() {
    this.store.dispatch(deleteTrustedAccountList());
    this._router.navigate([this.mainWizardRoute], { skipLocationChange: true });
  }

  getTransactionRuleValue(rule: boolean | null) {
    return getRuleLabel(rule);
  }
}
