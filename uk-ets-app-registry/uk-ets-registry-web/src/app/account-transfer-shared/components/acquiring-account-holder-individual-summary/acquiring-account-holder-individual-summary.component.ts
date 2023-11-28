import { Component, Input } from '@angular/core';
import {
  AccountHolder,
  AccountHolderType,
} from '@registry-web/shared/model/account';

@Component({
  selector: 'app-acquiring-account-holder-individual-summary',
  templateUrl: './acquiring-account-holder-individual-summary.component.html',
  styles: [],
})
export class AcquiringAccountHolderIndividualSummaryComponent {
  @Input()
  acquiringAccountHolder: AccountHolder;
  accountHolderTypes = AccountHolderType;
}
