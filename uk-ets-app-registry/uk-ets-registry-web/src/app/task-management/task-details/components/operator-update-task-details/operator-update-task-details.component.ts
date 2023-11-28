import { Component, Input, OnInit } from '@angular/core';
import { OperatorUpdateTaskDetails, RequestType } from '@task-management/model';
import { SummaryListItem } from '@shared/summary-list/summary-list.info';

@Component({
  selector: 'app-operator-update-task-details',
  templateUrl: './operator-update-task-details.component.html',
})
export class OperatorUpdateTaskDetailsComponent implements OnInit {
  @Input()
  operatorUpdateTaskDetails: OperatorUpdateTaskDetails;

  isInstallation: boolean;
  isAircraft: boolean;

  ngOnInit(): void {
    this.isInstallation =
      this.operatorUpdateTaskDetails.taskType ===
      RequestType.INSTALLATION_OPERATOR_UPDATE_REQUEST;
    this.isAircraft =
      this.operatorUpdateTaskDetails.taskType ===
      RequestType.AIRCRAFT_OPERATOR_UPDATE_REQUEST;
  }

  getSummaryListItems(): SummaryListItem[] {
    return [
      {
        key: { label: 'Account Holder' },
        value: [
          {
            label: this.operatorUpdateTaskDetails.accountInfo
              ?.accountHolderName,
          },
        ],
      },
      {
        key: { label: 'Account number' },
        value: [
          {
            label: this.operatorUpdateTaskDetails.accountInfo?.fullIdentifier,
          },
        ],
      },
      {
        key: { label: 'Account name' },
        value: [
          {
            label: this.operatorUpdateTaskDetails.accountInfo?.accountName,
          },
        ],
      },
    ];
  }
}
