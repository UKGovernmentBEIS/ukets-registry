import { Component, Input } from '@angular/core';
import { IssueAllowancesTaskDetailsDTO } from '@task-management/model';
import { ApiEnumTypes } from '@shared/model';

@Component({
  selector: 'app-allowance-task-details',
  templateUrl: './issue-allowances-task-details.component.html',
})
export class IssueAllowancesTaskDetailsComponent {
  ApiEnumTypes = ApiEnumTypes;

  @Input()
  transactionTaskDetails: IssueAllowancesTaskDetailsDTO;

  getActiveYear() {
    return this.transactionTaskDetails.blocks.find((b) => b.quantity != null)
      .year;
  }
}
