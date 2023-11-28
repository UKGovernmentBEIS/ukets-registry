import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
} from '@angular/core';
import { UserDetailsUpdateWizardPathsModel } from '@user-update/model';
import { SummaryListItem } from '@shared/summary-list/summary-list.info';
import {
  AccountDetails,
  AccountTypeMap,
  AuthorisedRepresentative,
} from '@shared/model/account';
import { AllocationStatus } from '@account-management/account-list/account-list.model';

@Component({
  selector: 'app-check-closure-details',
  templateUrl: './check-closure-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CheckClosureDetailsComponent implements OnInit {
  @Input()
  comment: string;
  @Input()
  accountDetails: AccountDetails;
  @Input()
  allocationClassification: AllocationStatus;
  @Input()
  pendingAllocationTaskExists: boolean;
  @Input()
  authorisedRepresentatives: AuthorisedRepresentative[];
  @Input() accountType: string;
  @Output()
  readonly navigateToEmitter = new EventEmitter<string>();
  @Output()
  readonly submitRequest = new EventEmitter<any>();

  routePathForJustificationComment: string;
  accountTypeMap = AccountTypeMap;

  ngOnInit(): void {
    this.routePathForJustificationComment =
      UserDetailsUpdateWizardPathsModel.BASE_PATH +
      '/' +
      UserDetailsUpdateWizardPathsModel.DEACTIVATION_COMMENT;
  }

  get accountHeadingSummaryList(): SummaryListItem[] {
    return [
      {
        key: { label: 'Account details', class: 'govuk-heading-m' },
        value: [
          {
            label: '',
          },
        ],
      },
    ];
  }

  get accountDetailsSummaryList(): SummaryListItem[] {
    return [
      {
        key: {
          label: 'You will close the following account:',
          class: 'govuk-heading-m',
        },
        value: [
          {
            label: '',
          },
        ],
      },
      {
        key: { label: 'Account type' },
        value: [
          {
            label: this.accountDetails.accountType,
          },
        ],
      },
      {
        key: { label: 'Account number' },
        value: [
          {
            label: this.accountDetails.accountNumber,
          },
        ],
      },
      {
        key: { label: 'Account name' },
        value: [
          {
            label: this.accountDetails.name,
          },
        ],
      },
      {
        key: { label: 'Account holder ID' },
        value: [
          {
            label: this.accountDetails.accountHolderId,
          },
        ],
      },
      {
        key: { label: 'Account holder name' },
        value: [
          {
            label: this.accountDetails.accountHolderName,
          },
        ],
      },
    ];
  }

  get justificationComment(): SummaryListItem[] {
    return [
      {
        key: { label: 'Reason for closing the account' },
        value: [
          {
            label: this.comment,
          },
        ],
        action: {
          label: 'Change',
          visible: true,
          visuallyHidden: '',
          clickEvent: this.routePathForJustificationComment,
        },
      },
    ];
  }
  get defaultWarningLabel(): string {
    return `The account will be closed. This action cannot be cancelled or reversed.
    All Authorised Representatives will be removed from the account after this request
    has been approved.`;
  }

  get overAllocatedLabel(): string {
    const accountType =
      this.accountType ===
      this.accountTypeMap.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT.registryAccountType
        ? 'Aircraft Operator'
        : 'installation';
    return `The ${accountType} has over-allocated allowances.`;
  }

  get underAllocatedLabel(): string {
    const accountType =
      this.accountType ===
      this.accountTypeMap.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT.registryAccountType
        ? 'Aircraft Operator'
        : 'installation';
    return `The ${accountType} has under-allocated allowances.`;
  }

  get unAllocatedLabel(): string {
    const accountType =
      this.accountType ===
      this.accountTypeMap.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT.registryAccountType
        ? 'Aircraft Operator'
        : 'installation';
    return `The ${accountType} has unallocated allowances.`;
  }

  get noARLinkedLabel(): string {
    return `No active AR is linked to the account.`;
  }

  get pendingAllocationTaskExistsLabel(): string {
    return `There are outstanding allocation tasks for this account.`;
  }

  get surrenderDeficitLabel(): string {
    return `There is a deficit of surrenders (Surrender status B) for this account.`;
  }

  get noActiveAR() {
    return (
      this.authorisedRepresentatives.filter((ar) => ar.state === 'ACTIVE')
        .length === 0
    );
  }

  onSubmit() {
    this.submitRequest.emit({
      allocationClassification: this.allocationClassification,
      noActiveAR: this.noActiveAR,
      closureComment: this.comment,
    });
  }

  navigateTo(value) {
    this.navigateToEmitter.emit(value);
  }
}
