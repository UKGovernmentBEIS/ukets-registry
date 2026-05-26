import { Component, Input } from '@angular/core';
import { AccountHolder } from '@shared/model/account';

@Component({
  selector: 'app-transferring-account-summary',
  templateUrl: './transferring-account-summary.component.html',
  styles: ``,
})
export class TransferringAccountSummaryComponent {
  @Input()
  transferringAccountHolder!: AccountHolder;
  @Input()
  isSectionBreakVisible!: boolean;
  @Input()
  emitterId!: string;
}
