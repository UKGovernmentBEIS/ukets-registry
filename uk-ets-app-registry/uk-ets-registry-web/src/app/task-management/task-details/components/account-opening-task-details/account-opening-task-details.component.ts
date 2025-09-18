import { Store } from '@ngrx/store';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  AccountOpeningTaskDetails,
  TaskUpdateAction,
  TaskUpdateDetails,
} from '@task-management/model';
import {
  AccessRightLabelHintMap,
  AccountHolderType,
  AccountTypeMap,
  AuthorisedRepresentative,
  getRuleLabel,
  InstallationActivityType,
  Operator,
  OperatorType,
  operatorTypeMap,
  Regulator,
} from '@shared/model/account';
import { Router } from '@angular/router';
import { RequestDocumentsOrigin } from '@shared/model/request-documents/request-documents-origin';
import { enterRequestDocumentsWizard } from '@request-documents/wizard/actions';
import { DocumentsRequestType } from '@shared/model/request-documents/documents-request-type';
import { empty } from '@shared/shared.util';
import { SummaryListItem } from '@shared/summary-list/summary-list.info';
import { regulatorMap } from '@account-management/account-list/account-list.model';
import { enterRequestPaymentWizard } from '@request-payment/store/actions';

@Component({
  selector: 'app-account-opening-task-details',
  templateUrl: './account-opening-task-details.component.html',
  styleUrls: ['./account-opening-task-details.component.css'],
})
export class AccountOpeningTaskDetailsComponent {
  @Input()
  taskDetails: AccountOpeningTaskDetails;
  @Input()
  isSeniorOrJuniorAdmin: boolean;

  @Output()
  readonly triggerTaskUpdate: EventEmitter<TaskUpdateDetails> =
    new EventEmitter();
  isCompleted: boolean;
  operatorType = OperatorType;
  accountHolderTypes = AccountHolderType;
  activityTypes = InstallationActivityType;
  regulators = Regulator;
  accountTypeMap = AccountTypeMap;
  operatorTypeMap = operatorTypeMap;
  accessRightLabelHintMap = AccessRightLabelHintMap;

  ngOnInit(): void {
    this.isCompleted = this.taskDetails.taskStatus === 'COMPLETED';
  }

  constructor(
    private store: Store,
    private router: Router
  ) {}

  isInstallationTransfer() {
    return (
      this.taskDetails.account?.operator?.type ===
      this.operatorType.INSTALLATION_TRANSFER
    );
  }
  isaccountHolderNew() {
    return empty(this.taskDetails.account.accountHolder.id);
  }

  hasAccountHolderChanged() {
    return (
      this.taskDetails.account.changedAccountHolderId &&
      this.taskDetails.account.accountHolder.id !==
        this.taskDetails.account.changedAccountHolderId
    );
  }

  canUpdateTask() {
    return (
      this.taskDetails.currentUserClaimant &&
      this.taskDetails.taskStatus !== 'COMPLETED'
    );
  }

  get isOHAOrAOHA() {
    return (
      this.taskDetails.account.accountType === 'OPERATOR_HOLDING_ACCOUNT' ||
      this.taskDetails.account.accountType ===
        'AIRCRAFT_OPERATOR_HOLDING_ACCOUNT'
    );
  }

  get isOHAOrAOHAorMOHA() {
    return (
      this.taskDetails.account.accountType === 'OPERATOR_HOLDING_ACCOUNT' ||
      this.taskDetails.account.accountType ===
        'MARITIME_OPERATOR_HOLDING_ACCOUNT'
    );
  }

  getDefaultRules(): SummaryListItem[] {
    const transactionRules: SummaryListItem[] = [
      {
        key: {
          label:
            'Do you want a second authorised representative to approve transfers of units to a trusted account?',
        },
        value: {
          label: getRuleLabel(
            this.taskDetails.account.trustedAccountListRules.rule1
          ),
        },
      },
      {
        key: {
          label:
            'Do you want to allow transfers of units to accounts that are not on the trusted account list?',
        },
        value: {
          label: getRuleLabel(
            this.taskDetails.account.trustedAccountListRules.rule2
          ),
        },
      },
      {
        key: {
          label:
            'Do you want a second authorised representative to approve a surrender transaction or a return of excess allocation?',
        },
        value: {
          label: getRuleLabel(
            this.taskDetails.account.trustedAccountListRules.rule3
          ),
        },
      },
    ];
    if (this.isOHAOrAOHAorMOHA) {
      return transactionRules;
    }
    transactionRules.splice(2, 1);
    return transactionRules;
  }

  getAccessRightsText(ar: AuthorisedRepresentative): string {
    return this.accessRightLabelHintMap.get(ar.right)?.text || 'Read only';
  }

  onApplyAccountHolder(changedAccountHolderInput) {
    if (
      changedAccountHolderInput.value !=
      this.taskDetails.account.accountHolder.id
    ) {
      this.triggerTaskUpdate.emit({
        taskUpdateAction: TaskUpdateAction.UPDATE_ACCOUNT_HOLDER,
        updateInfo: changedAccountHolderInput.value,
        taskDetails: this.taskDetails,
      });
    }
  }

  onAccountHolderReset() {
    this.triggerTaskUpdate.emit({
      taskUpdateAction: TaskUpdateAction.RESET_ACCOUNT_HOLDER,
      updateInfo: null,
      taskDetails: this.taskDetails,
    });
  }

  onRegulatorTaskUpdate(taskUpdateDetails: TaskUpdateDetails) {
    this.triggerTaskUpdate.emit(taskUpdateDetails);
  }

  onAccountHolderRequestDocuments() {
    // TODO  more work is needed for this one to work see UKETS-4828
    const accountHolderName = this.taskDetails.account.accountHolder.details;
    this.store.dispatch(
      enterRequestDocumentsWizard({
        origin: RequestDocumentsOrigin.ACCOUNT_OPENING_TASK,
        originatingPath: this.router.url,
        parentRequestId: this.taskDetails.requestId,
        documentsRequestType: DocumentsRequestType.ACCOUNT_HOLDER,
        accountHolderIdentifier: this.taskDetails.account.accountHolder.id,
        accountHolderName: this.taskDetails.account.accountHolder.details.name,
        accountName: this.taskDetails.account.accountDetails.name,
        accountFullIdentifier: this.taskDetails.accountFullIdentifier,
        recipientName: this.taskDetails.initiatorName,
        recipientUrid: this.taskDetails.initiatorUrid,
      })
    );
  }

  onUserRequestDocuments(recipientName: string, recipientUrid: string) {
    this.store.dispatch(
      enterRequestDocumentsWizard({
        origin: RequestDocumentsOrigin.ACCOUNT_OPENING_TASK,
        originatingPath: this.router.url,
        parentRequestId: this.taskDetails.requestId,
        documentsRequestType: DocumentsRequestType.USER,
        recipientName,
        recipientUrid,
      })
    );
  }

  onRequestPayment() {
    const candidateRecipients = [];
    this.taskDetails.account.authorisedRepresentatives?.forEach((rep) => {
      candidateRecipients.push({ ...rep.user, urid: rep.urid });
    });

    this.store.dispatch(
      enterRequestPaymentWizard({
        origin: 'ACCOUNT_OPENING',
        originatingPath: this.router.url,
        parentRequestId: this.taskDetails.requestId,
        candidateRecipients: candidateRecipients,
      })
    );
  }

  //TODO all the following methods should be moved to a child components. Did proceed with this due to a
  // problem in the css

  hasRegulatorChanged(operator: Operator): boolean {
    if (operator.type === OperatorType.INSTALLATION_TRANSFER) {
      return (
        operator.changedRegulator &&
        this.taskDetails.account.installationToBeTransferred.regulator !==
          operator.changedRegulator
      );
    } else {
      return (
        operator.changedRegulator &&
        operator.regulator !== operator.changedRegulator
      );
    }
  }

  getRegulatorKeys() {
    return Object.keys(this.regulators);
  }

  getRegulatorText(regulatorKey: string) {
    return regulatorMap[regulatorKey];
  }

  onRegulatorReset() {
    this.triggerTaskUpdate.emit({
      taskUpdateAction: TaskUpdateAction.RESET_REGULATOR,
      updateInfo: null,
      taskDetails: this.taskDetails,
    });
  }

  onApplyRegulator(changeRegulator) {
    if (changeRegulator.value) {
      this.triggerTaskUpdate.emit({
        taskUpdateAction: TaskUpdateAction.CHANGE_REGULATOR,
        updateInfo: Regulator[changeRegulator.value],
        taskDetails: this.taskDetails,
      });
    }
  }
}
