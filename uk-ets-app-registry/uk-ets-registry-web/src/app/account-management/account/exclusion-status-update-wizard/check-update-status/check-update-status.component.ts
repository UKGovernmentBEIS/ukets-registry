import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { OperatorEmissionsExclusionStatus } from '@exclusion-status-update-wizard/model';
import {
  Account,
  AircraftOperator,
  Installation,
  MaritimeOperator,
  OperatorType,
} from '@shared/model/account';
import {
  InstallationPipe,
  AircraftOperatorPipe,
  MaritimeOperatorPipe,
} from '@shared/pipes';
import { VerifiedEmissions } from '@account-shared/model';

@Component({
  selector: 'app-check-update-status',
  templateUrl: './check-update-status.component.html',
})
export class CheckUpdateStatusComponent implements OnInit {
  @Output() readonly submitUpdate = new EventEmitter<{
    accountIdentifier: string;
    exclusionStatus: OperatorEmissionsExclusionStatus;
  }>();
  @Output() readonly navigateToEmitter = new EventEmitter<string>();
  @Input()
  year: number;
  @Input()
  exclusionStatus: boolean;
  @Input()
  exclusionReason: string;
  @Input()
  emissions: VerifiedEmissions[];
  @Input()
  account: Account;
  @Input()
  routePathForYearSelection: string;
  @Input()
  routePathForExclusionStatus: string;
  @Input()
  routePathForExclusionReason: string;

  values: OperatorEmissionsExclusionStatus =
    {} as OperatorEmissionsExclusionStatus;
  isInstallation: boolean;
  isAircraft: boolean;
  isMaritime: boolean;
  title: string;
  installation: Installation;
  aircraftOperator: AircraftOperator;
  maritimeOperator: MaritimeOperator;
  hasEmissions: boolean;

  constructor(
    private installationPipe: InstallationPipe,
    private aircraftOperatorPipe: AircraftOperatorPipe,
    private maritimeOperatorPipe: MaritimeOperatorPipe
  ) {}

  ngOnInit() {
    this.values.year = this.year;
    this.values.excluded = this.exclusionStatus;
    this.values.reason = this.exclusionReason;

    this.isInstallation =
      this.account.operator.type === OperatorType.INSTALLATION;
    this.isAircraft =
      this.account.operator.type === OperatorType.AIRCRAFT_OPERATOR;
    this.isMaritime =
      this.account.operator.type === OperatorType.MARITIME_OPERATOR;
    this.title = this.isInstallation
      ? 'Installation details'
      : this.isMaritime
        ? 'Maritime operator details'
        : 'Aircraft operator details';
    if (this.isInstallation) {
      this.installation = this.installationPipe.transform(
        this.account.operator
      );
    } else if (this.isAircraft) {
      this.aircraftOperator = this.aircraftOperatorPipe.transform(
        this.account.operator
      );
    } else if (this.isMaritime) {
      this.maritimeOperator = this.maritimeOperatorPipe.transform(
        this.account.operator
      );
    }

    this.hasEmissions = this.emissions?.some(
      (e) =>
        e.year === this.year &&
        e.reportableEmissions != null &&
        e.reportableEmissions !== 'Excluded'
    );
  }

  onSubmit() {
    this.submitUpdate.emit({
      accountIdentifier: String(this.account.identifier),
      exclusionStatus: this.values as OperatorEmissionsExclusionStatus,
    });
  }

  navigateTo(value) {
    this.navigateToEmitter.emit(value);
  }
}
