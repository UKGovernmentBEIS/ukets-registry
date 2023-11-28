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
import { completeWizard, nextPage } from '../trusted-account-list.actions';
import { ActivatedRoute, Router } from '@angular/router';
import { TrustedAccountListWizardRoutes } from '../trusted-account-list-wizard-properties';
import { canGoBack } from '@shared/shared.action';
import { MainWizardRoutes } from '../../main-wizard.routes';

@Component({
  selector: 'app-single-person-surrender-excess-allocation',
  templateUrl: './single-person-surrender-excess-allocation.component.html',
})
export class SinglePersonSurrenderExcessAllocationComponent implements OnInit {
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
        trustedAccountList.rule3 == null ||
        trustedAccountList.rule3
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
            goBackRoute: this.transfersOutsideListRoute,
            extras: { skipLocationChange: true },
          })
        );
      }
    });
    this.store.dispatch(completeWizard({ complete: false }));
  }

  onContinue() {
    const trustedAccountListPartial = new TrustedAccountList();
    trustedAccountListPartial.rule3 = this.selectedValue === 'yes';
    this.store.dispatch(
      nextPage({
        trustedAccountList: trustedAccountListPartial,
      })
    );

    this._router.navigate([this.overviewRoute], {
      skipLocationChange: true,
    });
  }
}
