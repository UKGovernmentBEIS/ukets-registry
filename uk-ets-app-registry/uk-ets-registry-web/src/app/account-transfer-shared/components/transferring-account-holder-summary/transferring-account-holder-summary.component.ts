import { Component, Input } from '@angular/core';
import { AccountHolder } from '@registry-web/shared/model/account';

@Component({
  selector: 'app-transferring-account-holder-summary',
  templateUrl: './transferring-account-holder-summary.component.html',
  styles: [],
})
export class TransferringAccountHolderSummaryComponent {
  @Input()
  transferringAccountHolder: AccountHolder;
  @Input() isSectionBreakVisible: boolean;
}
