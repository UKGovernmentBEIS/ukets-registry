import { Component, Input, OnInit } from '@angular/core';
import { BusinessCheckResult } from '@shared/model/transaction';

@Component({
  selector: 'app-allowances-proposal-submitted',
  templateUrl: './allowances-proposal-submitted.component.html'
})
export class AllowancesProposalSubmittedComponent {
  @Input()
  businessCheckResult: BusinessCheckResult;
}
