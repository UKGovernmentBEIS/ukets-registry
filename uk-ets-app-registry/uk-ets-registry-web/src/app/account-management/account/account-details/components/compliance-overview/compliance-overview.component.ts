import { Component, Input } from '@angular/core';
import {
  ComplianceOverviewResult,
  complianceStatusMap,
} from '@account-shared/model';
import { AccountType } from '@shared/model/account';

@Component({
  selector: 'app-compliance-overview',
  templateUrl: './compliance-overview.component.html',
  styles: [],
})
export class ComplianceOverviewComponent {
  @Input()
  accountType: AccountType;
  @Input()
  compliantEntityIdentifier: number;
  @Input()
  complianceOverviewResult: ComplianceOverviewResult;
  @Input()
  isAdmin: boolean;

  complianceStatusMap = complianceStatusMap;

  getWarning(): string {
    if ('B' === this.complianceOverviewResult?.currentComplianceStatus) {
      return 'You need to surrender allowances.';
    } else if ('C' === this.complianceOverviewResult?.currentComplianceStatus) {
      return this.isAdmin
        ? 'You need to report emissions for all years of the reporting period. Upload emissions through ETS administration.'
        : 'You need to report emissions for all years of the reporting period.';
    }

    return null;
  }

  getSurrenderBalance(): number {
    if (
      this.complianceOverviewResult?.totalNetSurrenders == undefined &&
      this.complianceOverviewResult?.totalVerifiedEmissions == undefined
    ) {
      return null;
    }

    return (
      this.undefinedToZero(this.complianceOverviewResult?.totalNetSurrenders) -
      this.undefinedToZero(
        this.complianceOverviewResult?.totalVerifiedEmissions
      )
    );
  }

  undefinedToZero(t: number): number {
    if (t === undefined) {
      return 0;
    }
    return t;
  }
}
