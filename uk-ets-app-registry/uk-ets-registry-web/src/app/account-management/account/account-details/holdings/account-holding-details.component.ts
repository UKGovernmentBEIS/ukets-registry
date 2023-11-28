import { Component, OnInit } from '@angular/core';
import { AccountHoldingDetails } from '@account-management/account/account-details/holdings/account-holding-details.model';
import { ActivatedRoute, Data } from '@angular/router';
import {
  CommitmentPeriod,
  EnvironmentalActivity,
  EnvironmentalActivityLabelMap,
  UNIT_TYPE_LABELS,
} from '@shared/model/transaction';
import { Store } from '@ngrx/store';
import { errors } from '@shared/shared.action';
import { ErrorSummary } from '@shared/error-summary';
import { AccountType, AccountTypeMap } from '@shared/model/account';

@Component({
  selector: 'app-account-holding-details',
  templateUrl: './account-holding-details.component.html',
})
export class AccountHoldingDetailsComponent implements OnInit {
  details: AccountHoldingDetails;
  accountType: AccountType;
  commitmentPeriod = CommitmentPeriod;
  unitTypeLabels = UNIT_TYPE_LABELS;

  readonly AccountTypeMap = AccountTypeMap;
  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit() {
    this.route.data.subscribe((data: Data) => {
      this.details = data['details'].details;
      this.accountType = data['details'].accountType;
      const errorSummary: ErrorSummary = this.details.errorSummary;
      if (this.details.errorSummary) {
        this.store.dispatch(errors({ errorSummary }));
      }
    });
  }

  getActivityLabel(activity: string): string {
    let label = null;
    if (activity) {
      const enviromentalActivity = EnvironmentalActivity[activity];
      if (enviromentalActivity) {
        label = EnvironmentalActivityLabelMap.get(enviromentalActivity);
      }
    }
    return label;
  }
}
