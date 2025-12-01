import { Component, computed, input, output } from '@angular/core';
import {
  AccountHolder,
  AccountHolderType,
  Individual,
  Organisation,
} from '@shared/model/account';

@Component({
  selector: 'app-account-holder-long-overview',
  templateUrl: './account-holder-long-overview.component.html',
})
export class AccountHolderLongOverviewComponent {
  readonly accountHolder = input.required<AccountHolder>();
  readonly headingCaption = input.required<string>();
  readonly showChangeLink = input<boolean>(false);
  readonly goToChangeForm = output<void>();

  readonly organisation = computed<Organisation>(() =>
    this.accountHolder()?.type === AccountHolderType.ORGANISATION
      ? (this.accountHolder() as Organisation)
      : null
  );
  readonly individual = computed<Individual>(() =>
    this.accountHolder()?.type === AccountHolderType.INDIVIDUAL
      ? (this.accountHolder() as Individual)
      : null
  );

  navigateToChangeForm() {
    this.goToChangeForm.emit();
  }
}
