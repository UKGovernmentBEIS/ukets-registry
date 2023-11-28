import { Component, Input } from '@angular/core';
import { KpIssuanceTaskDetailsDto } from '@task-management/model';
import { ApiEnumTypes } from '@shared/model';

@Component({
  selector: 'app-issue-kp-units-task-details',
  templateUrl: './issue-kp-units-task-details.component.html',
})
export class IssueKpUnitsTaskDetailsComponent {
  ApiEnumTypes = ApiEnumTypes;

  @Input()
  transactionTaskDetails: KpIssuanceTaskDetailsDto;
}
