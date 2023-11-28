import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { TrustedAccountList } from '../trusted-account-list';
import {
  selectTrustedAccountList,
  selectTrustedAccountListCompleted,
} from '../trusted-account-list.selector';
import { TrustedAccountListWizardRoutes } from '../trusted-account-list-wizard-properties';
import { ActivatedRoute, Router } from '@angular/router';
import { UntypedFormBuilder } from '@angular/forms';
import { Store } from '@ngrx/store';
import { take } from 'rxjs/operators';
import { canGoBack } from '@shared/shared.action';
import {
  completeWizard,
  navigateToNextPage,
  nextPage,
} from '../trusted-account-list.actions';

@Component({
  selector: 'app-transfers-outside-list',
  templateUrl: './transfers-outside-list.component.html',
})
export class TransfersOutsideListComponent implements OnInit {
  selectedValue: string;
  trustedAccountList$: Observable<TrustedAccountList> = this.store.select(
    selectTrustedAccountList
  ) as Observable<TrustedAccountList>;
  trustedAccountListCompleted$: Observable<boolean> = this.store.select(
    selectTrustedAccountListCompleted
  ) as Observable<boolean>;

  readonly singlepersonsurrenderexcessallocationRoute =
    TrustedAccountListWizardRoutes.SINGLE_PERSON_SURRENDER_EXCESS_ALLOCATION;
  readonly secondApprovalNecessaryRoute =
    TrustedAccountListWizardRoutes.SECOND_APPROVAL_NECESSARY;
  readonly overviewRoute = TrustedAccountListWizardRoutes.OVERVIEW;

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
        trustedAccountList.rule2 == null ||
        !trustedAccountList.rule2
      ) {
        this.selectedValue = 'no';
      } else {
        this.selectedValue = 'yes';
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
            goBackRoute: this.secondApprovalNecessaryRoute,
            extras: { skipLocationChange: true },
          })
        );
      }
    });
    this.store.dispatch(completeWizard({ complete: false }));
  }

  onContinue() {
    const trustedAccountListPartial = new TrustedAccountList();
    trustedAccountListPartial.rule2 = this.selectedValue === 'yes';
    this.store.dispatch(
      nextPage({
        trustedAccountList: trustedAccountListPartial,
      })
    );
    this.store.dispatch(navigateToNextPage());
  }
}
