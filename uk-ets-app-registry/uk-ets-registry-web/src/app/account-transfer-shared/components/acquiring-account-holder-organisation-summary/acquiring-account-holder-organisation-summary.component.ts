import { Component, Input } from '@angular/core';
import {
  AccountHolder,
  AccountHolderType,
} from '@registry-web/shared/model/account';

@Component({
  selector: 'app-acquiring-account-holder-organisation-summary',
  templateUrl: './acquiring-account-holder-organisation-summary.component.html',
  styles: [],
})
export class AcquiringAccountHolderOrganisationSummaryComponent {
  @Input()
  acquiringAccountHolder: AccountHolder;
  accountHolderTypes = AccountHolderType;
}
