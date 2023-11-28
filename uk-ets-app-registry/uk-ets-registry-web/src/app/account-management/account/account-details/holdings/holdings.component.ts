import { Component, Input } from '@angular/core';
import {
  Account,
  AccountHolding,
  AccountHoldingsResult,
  AccountTypeMap,
} from '@shared/model/account';
import { CommitmentPeriod, UnitType } from '@shared/model/transaction';
import { ActivatedRoute, Router } from '@angular/router';
import { AccountHoldingDetailsCriteria } from '@account-management/account/account-details/holdings/account-holding-details.model';

@Component({
  selector: 'app-holdings',
  templateUrl: './holdings.component.html',
  styleUrls: ['./holdings.component.scss'],
})
export class HoldingsComponent {
  @Input() accountId: string;

  @Input() account: Account;

  @Input()
  accountHoldingsResult: AccountHoldingsResult;

  public commitmentPeriod = CommitmentPeriod;
  actionForAnyAdmin = 'urn:uk-ets-registry-api:actionForAnyAdmin';

  readonly AccountTypeMap = AccountTypeMap;

  constructor(private router: Router, private route: ActivatedRoute) {}

  goTodetails(holding: AccountHolding) {
    const criteria: AccountHoldingDetailsCriteria = {
      accountId: this.accountId,
      originalPeriodCode: this.commitmentPeriod[holding.originalPeriod],
      applicablePeriodCode: this.commitmentPeriod[holding.applicablePeriod],
      subjectToSop: holding.subjectToSop,
      unit: holding.type,
    };
    this.router.navigate(['holdings', 'details'], {
      relativeTo: this.route,
      state: {
        criteria,
        accountType: this.account.accountType,
      },
    });
  }

  isKyotoProtocolUnit(unitType: UnitType): boolean {
    if (UnitType.ALLOWANCE === unitType) {
      return false;
    }
    return true;
  }
}
