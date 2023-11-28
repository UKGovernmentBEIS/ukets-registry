import { Component, Input } from '@angular/core';

import { AccountClosureTaskDetails } from '@task-management/model';
import { SummaryListItem } from '@shared/summary-list/summary-list.info';
import { accountStatusMap } from '@account-management/account-list/account-list.model';
import { AccountTypeMap } from '@registry-web/shared/model/account';

@Component({
  selector: 'app-account-closure-task-details',
  templateUrl: './account-closure-task-details.component.html',
})
export class AccountClosureTaskDetailsComponent {
  @Input() taskDetails: AccountClosureTaskDetails;

  accountStatusMap = accountStatusMap;
  accountTypeMap = AccountTypeMap;

  getSummaryListItems(): SummaryListItem[] {
    const items = [
      {
        key: { label: 'Account type' },
        value: {
          label: this.taskDetails.accountDetails.accountType,
          class: 'govuk-summary-list__value',
        },
      },
      {
        key: { label: 'Account number' },
        value: {
          label: this.taskDetails.accountDetails.accountNumber,
          class: 'govuk-summary-list__value',
        },
      },
      {
        key: { label: 'Account name' },
        value: {
          label: this.taskDetails.accountDetails.name,
          class: 'govuk-summary-list__value',
        },
      },
      {
        key: { label: 'Previous account status' },
        value: {
          label:
            accountStatusMap[this.taskDetails.accountDetails.accountStatus]
              .label,
          class:
            'govuk-summary-list__value tag tag-' +
            accountStatusMap[this.taskDetails.accountDetails.accountStatus]
              .color,
        },
      },
      {
        key: { label: 'Account holder ID' },
        value: {
          label: this.taskDetails.accountDetails.accountHolderId,
          class: 'govuk-summary-list__value',
        },
      },
      {
        key: { label: 'Account holder name' },
        value: {
          label: this.taskDetails.accountDetails.accountHolderName,
          class: 'govuk-summary-list__value',
        },
      },
      {
        key: { label: 'Permit ID' },
        value: {
          label: this.taskDetails.permitId,
          class: 'govuk-summary-list__value',
        },
      },
      {
        key: { label: 'Monitoring Plan ID' },
        value: {
          label: this.taskDetails.monitoringPlanId,
          class: 'govuk-summary-list__value',
        },
      },
      {
        key: { label: 'Reason for closing the account' },
        value: {
          label: this.taskDetails.closureComment,
          class: 'govuk-summary-list__value govuk-!-padding-top-7',
        },
      },
    ];

    if (
      this.accountTypeMap[this.taskDetails.accountDetails.accountTypeEnum]
        ?.registryAccountType ===
      this.accountTypeMap.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT.registryAccountType
    ) {
      return items.filter((t) => t.key.label != 'Permit ID');
    } else if (
      this.accountTypeMap[this.taskDetails.accountDetails.accountTypeEnum]
        ?.registryAccountType ===
      this.accountTypeMap.OPERATOR_HOLDING_ACCOUNT.registryAccountType
    ) {
      return items.filter((t) => t.key.label != 'Monitoring Plan ID');
    } else {
      return items.filter(
        (t) => t.key.label != 'Permit ID' && t.key.label != 'Monitoring Plan ID'
      );
    }
  }

  getAccountType(): string {
    const accountType =
      this.accountTypeMap[this.taskDetails.accountDetails.accountTypeEnum]
        ?.registryAccountType ===
      this.accountTypeMap.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT.registryAccountType
        ? 'Aircraft Operator'
        : 'installation';
    return accountType;
  }

  get surrenderDeficitLabel(): string {
    return `There is a deficit of surrenders (Surrender status B) for this account.`;
  }

  get overAllocatedLabel(): string {
    return `The ${this.getAccountType()} has over-allocated allowances.`;
  }

  get underAllocatedLabel(): string {
    return `The ${this.getAccountType()} has under-allocated allowances.`;
  }

  get unAllocatedLabel(): string {
    return `The ${this.getAccountType()} has unallocated allowances.`;
  }
}
