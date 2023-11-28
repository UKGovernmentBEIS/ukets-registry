import { Component, Input, OnInit } from '@angular/core';
import { AccountHolderOwnership } from '@task-management/model';
import { ACCOUNT_TYPE_OPTIONS } from '@task-management/task-list/task-search/search-tasks-form/search-tasks-form.model';
import { getLabel } from '@shared/shared.util';

@Component({
  selector: 'app-account-holder-multiple-ownership',
  templateUrl: './account-holder-multiple-ownership.component.html',
  styleUrls: ['./account-holder-multiple-ownership.component.scss']
})
export class AccountHolderMultipleOwnershipComponent {
  @Input()
  accountHolderOwnership: AccountHolderOwnership[];

  getLabel(value: string): string {
    return getLabel(value, ACCOUNT_TYPE_OPTIONS);
  }
}
