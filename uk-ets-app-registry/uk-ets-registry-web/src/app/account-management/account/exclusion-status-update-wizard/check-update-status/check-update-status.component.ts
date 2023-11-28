import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { OperatorEmissionsExclusionStatus } from '../model';
import {
  Account,
  AircraftOperator,
  Installation,
  OperatorType,
} from '@shared/model/account';
import { InstallationPipe, AircraftOperatorPipe } from '@shared/pipes';

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
  account: Account;
  @Input()
  routePathForYearSelection: string;
  @Input()
  routePathForExclusionStatus: string;
  values: OperatorEmissionsExclusionStatus = {} as OperatorEmissionsExclusionStatus;
  isInstallation: boolean;
  isAircraft: boolean;
  title: string;
  installation: Installation;
  aircraftOperator: AircraftOperator;

  constructor(
    private installationPipe: InstallationPipe,
    private aircraftOperatorPipe: AircraftOperatorPipe
  ) {}

  ngOnInit() {
    this.values.year = this.year;
    this.values.excluded = this.exclusionStatus;

    this.isInstallation =
      this.account.operator.type === OperatorType.INSTALLATION;
    this.isAircraft =
      this.account.operator.type === OperatorType.AIRCRAFT_OPERATOR;
    this.title = this.isInstallation
      ? 'Installation details'
      : 'Aircraft operator details';
    if (this.isInstallation) {
      this.installation = this.installationPipe.transform(
        this.account.operator
      );
    } else if (this.isAircraft) {
      this.aircraftOperator = this.aircraftOperatorPipe.transform(
        this.account.operator
      );
    }
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
