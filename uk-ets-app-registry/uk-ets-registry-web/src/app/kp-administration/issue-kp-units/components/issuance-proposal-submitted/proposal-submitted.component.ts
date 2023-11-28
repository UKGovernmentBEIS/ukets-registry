import { Component, Input, OnInit } from '@angular/core';
import { BusinessCheckResult } from '@shared/model/transaction';

@Component({
  selector: 'app-proposal-submitted',
  templateUrl: './proposal-submitted.component.html'
})
export class ProposalSubmittedComponent {
  @Input()
  businessCheckResult: BusinessCheckResult;
}
