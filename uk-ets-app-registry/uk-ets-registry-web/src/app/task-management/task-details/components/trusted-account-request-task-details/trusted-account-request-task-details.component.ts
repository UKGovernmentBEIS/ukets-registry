import { Component, Input } from '@angular/core';
import { RequestType, TrustedAccountTaskDetails } from '@task-management/model';
import { TrustedAccountListType } from '@shared/components/account/trusted-account-table';

@Component({
  selector: 'app-trusted-account-request-task-details',
  templateUrl: './trusted-account-request-task-details.component.html'
})
export class TrustedAccountRequestTaskDetailsComponent {
  @Input()
  trustedAccountTaskDetails: TrustedAccountTaskDetails;
  TrustedAccountListType = TrustedAccountListType;
  RequestType = RequestType;
}
