import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { TransactionRuleUpdateTaskDetails } from '@task-management/model';
import { AccountType, getRuleLabel } from '@shared/model/account';
import { SummaryListItem } from '@shared/summary-list/summary-list.info';
import { empty } from '@shared/shared.util';

@Component({
  selector: 'app-transaction-rules-update-task-details',
  templateUrl: './transaction-rules-update-task-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TransactionRulesUpdateTaskDetailsComponent {
  @Input()
  transactionRuleUpdateTaskDetails: TransactionRuleUpdateTaskDetails;

  get isOHAOrAOHA() {
    return (
      this.transactionRuleUpdateTaskDetails.accountInfo.accountType ===
        AccountType.OPERATOR_HOLDING_ACCOUNT ||
      this.transactionRuleUpdateTaskDetails.accountInfo.accountType ===
        AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT
    );
  }

  getTransactionRuleValue(rule: boolean | null) {
    return getRuleLabel(rule);
  }

  getSummaryListItems(): SummaryListItem[] {
    const summaryListItems: SummaryListItem[] = [
      {
        key: { label: 'Transaction rules' },
        value: [
          {
            label: 'Current value',
            class: 'summary-list-change-header-font-weight',
          },
          {
            label: 'Changed value',
            class: 'summary-list-change-header-font-weight',
          },
        ],
      },

      {
        key: {
          label:
            'Is the approval of a second authorised representative necessary to execute transfers to accounts on the trusted account list?',
        },
        value: [
          {
            label: getRuleLabel(
              this.transactionRuleUpdateTaskDetails.trustedAccountListRules
                .currentRule1
            ),
          },
          {
            label: getRuleLabel(
              this.transactionRuleUpdateTaskDetails.trustedAccountListRules
                .rule1
            ),
            innerStyle:
              this.transactionRuleUpdateTaskDetails.trustedAccountListRules
                .currentRule1 !==
              this.transactionRuleUpdateTaskDetails.trustedAccountListRules
                .rule1
                ? 'govuk-summary-list__key govuk-body-m focused-text'
                : '',
          },
        ],
      },
      {
        key: {
          label:
            'Are transfers to accounts not on the trusted account list allowed?',
        },
        value: [
          {
            label: getRuleLabel(
              this.transactionRuleUpdateTaskDetails.trustedAccountListRules
                .currentRule2
            ),
          },
          {
            label: getRuleLabel(
              this.transactionRuleUpdateTaskDetails.trustedAccountListRules
                .rule2
            ),
            innerStyle:
              this.transactionRuleUpdateTaskDetails.trustedAccountListRules
                .currentRule2 !==
              this.transactionRuleUpdateTaskDetails.trustedAccountListRules
                .rule2
                ? 'govuk-summary-list__key govuk-body-m focused-text'
                : '',
          },
        ],
      },
    ];
    if (this.isOHAOrAOHA) {
      summaryListItems.push({
        key: {
          label:
            'Is the approval of a second AR necessary to execute a surrender transaction or a return of excess allocation?',
        },
        value: [
          {
            label: getRuleLabel(
              this.transactionRuleUpdateTaskDetails.trustedAccountListRules
                .currentRule3
            ),
          },
          {
            label: getRuleLabel(
              this.transactionRuleUpdateTaskDetails.trustedAccountListRules
                .rule3
            ),
            innerStyle:
              !empty(
                this.transactionRuleUpdateTaskDetails.trustedAccountListRules
                  .rule3
              ) &&
              this.transactionRuleUpdateTaskDetails.trustedAccountListRules
                .currentRule3 !==
                this.transactionRuleUpdateTaskDetails.trustedAccountListRules
                  .rule3
                ? 'govuk-summary-list__key govuk-body-m focused-text'
                : '',
          },
        ],
      });
    }
    return summaryListItems;
  }
}
