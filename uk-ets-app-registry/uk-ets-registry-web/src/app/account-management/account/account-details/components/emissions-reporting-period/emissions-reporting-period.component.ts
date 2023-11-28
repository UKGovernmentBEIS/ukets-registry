import { Component, Input } from '@angular/core';
import { VerifiedEmissionsResult } from '@account-shared/model';
import { ActivatedRoute, Router } from '@angular/router';
import { UpdateExclusionStatusPathsModel } from '@registry-web/account-management/account/exclusion-status-update-wizard/model';

@Component({
  selector: 'app-emissions-reporting-period',
  templateUrl: './emissions-reporting-period.component.html',
  styles: [],
})
export class EmissionsReportingPeriodComponent {
  @Input()
  title: string;

  @Input()
  helpMessageTitle: string;

  @Input()
  verifiedEmissionsResult: VerifiedEmissionsResult;

  @Input()
  canRequestUpdateExclusionStatus: boolean;

  constructor(private router: Router, private activatedRoute: ActivatedRoute) {}

  updateExclusionStatus() {
    this.router.navigate([
      this.activatedRoute.snapshot['_routerState'].url +
        `/${UpdateExclusionStatusPathsModel.BASE_PATH}`,
    ]);
  }

  showUploadedEmissionsNotification(): boolean {
    const currentYear = new Date().getFullYear();
    const verifiedEmissions = this.verifiedEmissionsResult?.verifiedEmissions;
    const lastYearOfVerifiedEmissions = this.verifiedEmissionsResult
      ?.lastYearOfVerifiedEmissions;

    return !!(
      currentYear == lastYearOfVerifiedEmissions &&
      verifiedEmissions.filter(
        (e) =>
          e.year == currentYear &&
          e.reportableEmissions != 'Excluded' &&
          e.reportableEmissions != null
      ).length > 0
    );
  }
}
