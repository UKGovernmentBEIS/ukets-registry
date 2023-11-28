import { Component, Input } from '@angular/core';
import { AccountHoldingsResult } from '@shared/model/account';

@Component({
  selector: 'app-overview',
  templateUrl: './overview.component.html',
})
export class OverviewComponent {
  @Input()
  accountHoldingsResult: AccountHoldingsResult;
}
