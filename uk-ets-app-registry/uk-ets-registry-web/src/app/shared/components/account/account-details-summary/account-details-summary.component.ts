import { Component, EventEmitter, Input, Output } from '@angular/core';
import { SummaryListItem } from '@shared/summary-list/summary-list.info';
import { AccountDetails } from '@shared/model/account';

@Component({
  selector: 'app-account-details-summary',
  templateUrl: './account-details-summary.component.html',
})
export class AccountDetailsSummaryComponent {
  @Input()
  isWizardOrientedFlag: boolean;
  @Input()
  routePathForJustificationComment: string;
  @Input()
  accountDetails: AccountDetails;
  @Input()
  accountHeadingSummary: SummaryListItem[];
  @Input()
  accountDetailsSummary: SummaryListItem[];
  @Input()
  justificationCommentSummary: SummaryListItem[];
  @Output() readonly navigateToEmitter = new EventEmitter<string>();

  navigateTo(value: string) {
    this.navigateToEmitter.emit(value);
  }
}
