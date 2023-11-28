import { Component, Input } from '@angular/core';
import { TrustedAccountList } from '@account-opening/trusted-account-list/trusted-account-list';

@Component({
  selector: 'app-transaction-rules-link',
  template: `
    <div
      class="govuk-caption-m"
      *ngIf="
        !accountDetailsCompleted && !trustedAccountListCompleted;
        else transactionRulesTemplate
      "
    >
      {{
        trustedAccountListCompleted
          ? trustedAccountListText
          : 'Choose the transaction rules'
      }}
    </div>
    <ng-template #transactionRulesTemplate>
      <a
        class="govuk-link transaction-rules-link"
        [routerLink]="trustedAccountListWizardLink"
        skipLocationChange="{{ skipLocationChange }}"
      >
        {{
          trustedAccountListCompleted
            ? trustedAccountListText
            : 'Choose the transaction rules'
        }}
      </a>
    </ng-template>
  `,
})
export class TransactionRulesLinkComponent {
  @Input() isOHAOrAOHA: boolean;
  @Input() trustedAccountListWizardLink: string;
  @Input() accountDetailsCompleted: boolean;
  @Input() trustedAccountListCompleted: boolean;
  @Input() trustedAccountList: TrustedAccountList;
  @Input() skipLocationChange?: boolean;

  get trustedAccountListText() {
    if (this.isOHAOrAOHA) {
      return `${
        this.trustedAccountList?.rule1
          ? TransactionRuleTexts.RULE_1_POSITIVE
          : TransactionRuleTexts.RULE_1_NEGATIVE
      }; ${
        this.trustedAccountList?.rule2
          ? TransactionRuleTexts.RULE_2_POSITIVE
          : TransactionRuleTexts.RULE_2_NEGATIVE
      }; ${
        this.trustedAccountList?.rule3
          ? TransactionRuleTexts.RULE_3_POSITIVE
          : TransactionRuleTexts.RULE_3_NEGATIVE
      }`;
    }
    return `${
      this.trustedAccountList?.rule1
        ? TransactionRuleTexts.RULE_1_POSITIVE
        : TransactionRuleTexts.RULE_1_NEGATIVE
    }; ${
      this.trustedAccountList?.rule2
        ? TransactionRuleTexts.RULE_2_POSITIVE
        : TransactionRuleTexts.RULE_2_NEGATIVE
    }`;
  }
}

enum TransactionRuleTexts {
  RULE_1_POSITIVE = 'A second authorised representative must approve transfers of units to a trusted account',
  RULE_1_NEGATIVE = 'Transfers of units to a trusted account do not need approval by a second authorised representative',
  RULE_2_POSITIVE = 'Transfers of units can be made to accounts that are not on the trusted account list',
  RULE_2_NEGATIVE = 'Transfers of units cannot be made to accounts that are not on the trusted account list',
  RULE_3_POSITIVE = 'Surrender transactions or return of excess allocations need approval by a second authorised representative',
  RULE_3_NEGATIVE = 'Surrender transactions or return of excess allocations do not need approval by a second authorised representative',
}
