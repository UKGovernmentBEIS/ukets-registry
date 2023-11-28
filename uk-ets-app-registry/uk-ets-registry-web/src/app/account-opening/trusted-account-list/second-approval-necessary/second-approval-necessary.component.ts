import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import {
  selectTrustedAccountList,
  selectTrustedAccountListCompleted,
} from '../trusted-account-list.selector';
import { Store } from '@ngrx/store';
import { TrustedAccountList } from '../trusted-account-list';
import { take } from 'rxjs/operators';
import { UntypedFormBuilder } from '@angular/forms';
import {
  completeWizard,
  navigateTo,
  nextPage,
} from '../trusted-account-list.actions';
import { ActivatedRoute, Router } from '@angular/router';
import { TrustedAccountListWizardRoutes } from '../trusted-account-list-wizard-properties';
import { canGoBack } from '@shared/shared.action';
import { MainWizardRoutes } from '../../main-wizard.routes';
import { TransactionRulesNavigationEffects } from '@account-opening/trusted-account-list/trusted-account-list-navigation.effects';
import { OperatorUpdateWizardPathsModel } from '@operator-update/model/operator-update-wizard-paths.model';

@Component({
  selector: 'app-second-approval-necessary',
  templateUrl: './second-approval-necessary.component.html',
})
export class SecondApprovalNecessaryComponent implements OnInit {
  selectedValue: string;
  trustedAccountList$: Observable<TrustedAccountList> = this.store.select(
    selectTrustedAccountList
  ) as Observable<TrustedAccountList>;
  trustedAccountListCompleted$: Observable<boolean> = this.store.select(
    selectTrustedAccountListCompleted
  ) as Observable<boolean>;

  readonly transfersOutsideListRoute =
    TrustedAccountListWizardRoutes.TRANSFERS_OUTSIDE_LIST_ALLOWED;
  readonly overviewRoute = TrustedAccountListWizardRoutes.OVERVIEW;
  readonly mainWizardRoute = MainWizardRoutes.TASK_LIST;

  constructor(
    private _router: Router,
    private route: ActivatedRoute,
    private formBuilder: UntypedFormBuilder,
    private store: Store
  ) {}

  ngOnInit() {
    this.trustedAccountList$.pipe(take(1)).subscribe((trustedAccountList) => {
      if (
        trustedAccountList == null ||
        trustedAccountList.rule1 == null ||
        trustedAccountList.rule1
      ) {
        this.selectedValue = 'yes';
      } else {
        this.selectedValue = 'no';
      }
    });
    this.trustedAccountListCompleted$.pipe(take(1)).subscribe((completed) => {
      if (completed) {
        this.store.dispatch(
          canGoBack({
            goBackRoute: this.overviewRoute,
            extras: { skipLocationChange: true },
          })
        );
      } else {
        this.store.dispatch(
          canGoBack({
            goBackRoute: this.mainWizardRoute,
            extras: { skipLocationChange: true },
          })
        );
      }
    });
    this.store.dispatch(completeWizard({ complete: false }));
  }

  onContinue() {
    const trustedAccountListPartial = new TrustedAccountList();
    trustedAccountListPartial.rule1 = this.selectedValue === 'yes';
    this.store.dispatch(
      nextPage({
        trustedAccountList: trustedAccountListPartial,
      })
    );

    this.store.dispatch(
      navigateTo({
        route: TrustedAccountListWizardRoutes.TRANSFERS_OUTSIDE_LIST_ALLOWED,
        extras: {
          skipLocationChange: true,
        },
      })
    );
  }
}
