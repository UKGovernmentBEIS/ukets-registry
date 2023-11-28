import { Component, Input } from '@angular/core';
import { AccountHoldingsResult } from '@shared/model/account';
import { UNIT_TYPE_LABELS, UnitType } from '@shared/model/transaction';
import { complianceStatusMap } from '@account-shared/model';

@Component({
  selector: 'app-holdings-summary',
  templateUrl: './holdings-summary.component.html',
})
export class HoldingsSummaryComponent {
  @Input()
  accountHoldingsResult: AccountHoldingsResult;

  complianceStatusMap = complianceStatusMap;

  /**
   * If true we don't want to show the compliance specific info like the status and the warning messages.
   */
  @Input() hideComplianceInfo: boolean;

  getWarning(): string {
    if ('B' === this.accountHoldingsResult.currentComplianceStatus) {
      return 'You need to surrender allowances. See more details in Emissions and Surrenders section.';
    } else if ('C' === this.accountHoldingsResult.currentComplianceStatus) {
      return 'You need to report emissions for all years of the reporting period. See more details in Emissions and Surrenders section.';
    }

    return null;
  }

  getLabel(type: UnitType | null): string {
    return type != null ? UNIT_TYPE_LABELS[type].labelPlural : '';
  }
}
