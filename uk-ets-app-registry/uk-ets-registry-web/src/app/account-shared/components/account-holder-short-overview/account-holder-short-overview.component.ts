import { Component, computed, input } from '@angular/core';
import {
  AccountHolder,
  AccountHolderType,
  Individual,
  IndividualDetails,
  Organisation,
  OrganisationDetails,
} from '@shared/model/account';

@Component({
  selector: 'app-account-holder-short-overview',
  templateUrl: './account-holder-short-overview.component.html',
})
export class AccountHolderShortOverviewComponent {
  readonly accountHolder = input.required<AccountHolder>();
  readonly headingCaption = input.required<string>();
  readonly warningMessage = input.required<string>();

  readonly organisationDetails = computed<OrganisationDetails>(() =>
    this.accountHolder()?.type === AccountHolderType.ORGANISATION
      ? (this.accountHolder() as Organisation).details
      : null
  );
  readonly individualDetails = computed<IndividualDetails>(() =>
    this.accountHolder()?.type === AccountHolderType.INDIVIDUAL
      ? (this.accountHolder() as Individual).details
      : null
  );
}
