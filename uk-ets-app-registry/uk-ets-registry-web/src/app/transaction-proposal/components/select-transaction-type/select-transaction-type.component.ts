import {
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  OnInit,
  Output,
} from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { combineLatest, fromEvent, map, Subject, takeUntil } from 'rxjs';

import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import {
  ProposedTransactionType,
  TRANSACTION_TYPES_VALUES,
  TransactionTypesResult,
  TransactionType,
} from '@shared/model/transaction';
import {
  FormRadioGroupInfo,
  FormRadioOption,
} from '@shared/form-controls/uk-radio-input/uk-radio.model';
import { MenuItemEnum } from '@registry-web/account-management/account/account-details/model';

import {
  Account,
  TrustedAccountStatus,
} from '@registry-web/shared/model/account';

import { ErrorDetail } from '@registry-web/shared/error-summary';
import { TransactionProposalErrorsMap } from '@registry-web/transaction-proposal/transaction-proposal-error-messages';

@Component({
  selector: 'app-select-transaction-type',
  templateUrl: './select-transaction-type.component.html',
})
export class SelectTransactionTypeComponent
  extends UkFormComponent
  implements OnInit, OnDestroy
{
  @Input() allowedTransactionTypes: TransactionTypesResult;
  @Input() transactionType: ProposedTransactionType;
  @Input() itlNotificationId: number;
  @Input() isAdmin: boolean;
  @Input() transferringAccount: Account;

  @Output() readonly selectedTransactionType = new EventEmitter<{
    proposedTransactionType: ProposedTransactionType;
    itlNotificationId: string;
    clearNextStepsInWizard: boolean;
  }>();

  formRadioGroupInfo: FormRadioGroupInfo;
  separatorIndex = -1;
  private readonly unsubscribe$: Subject<void> = new Subject();

  constructor(
    protected formBuilder: UntypedFormBuilder,
    private router: Router,
    private route: ActivatedRoute
  ) {
    super();
  }

  ngOnInit() {
    super.ngOnInit();
    this.formRadioGroupInfo = {
      radioGroupHeading: 'Choose a transaction type',
      radioGroupHeadingCaption: 'Propose transaction',
      radioGroupHint: '',
      key: 'transactionType',
      extraInputField: 'itlNotificationId',
      options: this.getAllowedTransactionTypes(),
    };

    const event$ = fromEvent(document, 'click');
    const errors$ = this.errorDetails.asObservable();

    combineLatest([event$, errors$])
      .pipe(
        map(([event, errors]) => {
          if (
            event.target['href'] &&
            errors[0].errorMessage ===
              TransactionProposalErrorsMap
                .NO_ACCOUNT_AND_TRANSFERS_OUTSIDE_TAL_NOT_ALLOWED.errorMessage
          ) {
            event.preventDefault();
            this.router.navigate(
              [`/account/${this.route.snapshot.paramMap.get('accountId')}`],
              { state: { selectedSideMenu: MenuItemEnum.TRANSACTIONS } }
            );
          }
        }),
        takeUntil(this.unsubscribe$)
      )
      .subscribe();
  }

  getAllowedTransactionTypes(): FormRadioOption[] {
    if (this.allowedTransactionTypes) {
      const types = this.allowedTransactionTypes.result.map((type, i) => {
        let showITLInputField = false;
        if (
          TRANSACTION_TYPES_VALUES[type.type].hasTransactionITLNotificationId
        ) {
          showITLInputField = true;
        }
        return {
          label: type.description,
          value: type,
          hint: type.hint,
          enabled: true,
          showExtraInputField: showITLInputField && this.isAdmin,
          order: TRANSACTION_TYPES_VALUES[type.type].order,
        };
      });

      types.sort((a, b) => a.order - b.order);
      this.calculateSeparatorIndex(types);
      return types;
    }
  }

  private calculateSeparatorIndex(sortedOptions: FormRadioOption[]) {
    const primaryTypes = [
      TransactionType.SurrenderAllowances,
      TransactionType.TransferAllowances,
    ];
    this.separatorIndex = sortedOptions.reduce(
      (separatorIndex, option, index) => {
        const isPrimaryType = primaryTypes.indexOf(option.value.type) > -1;
        if (isPrimaryType) separatorIndex = index;
        return separatorIndex;
      },
      -1
    );
  }

  onContinue() {
    const transactionType: ProposedTransactionType =
      this.formGroup.get('transactionType').value;
    if (!transactionType) {
      return this.onSubmit();
    }

    if (
      transactionType.type === TransactionType.ExternalTransfer ||
      transactionType.type === TransactionType.TransferAllowances ||
      transactionType.type === TransactionType.InternalTransfer
    ) {
      if (
        !this.transferringAccount.trustedAccountListRules.rule2 &&
        this.transferringAccountTrustedList().length === 0 &&
        this.transferringAccountUnderTheSameAccountHolder().length === 0
      ) {
        const errors: ErrorDetail[] = [];
        errors.push(
          TransactionProposalErrorsMap.NO_ACCOUNT_AND_TRANSFERS_OUTSIDE_TAL_NOT_ALLOWED
        );
        this.errorDetails.emit(errors);
      } else {
        this.onSubmit();
      }
    } else {
      this.onSubmit();
    }
  }

  transferringAccountTrustedList() {
    return this.transferringAccount.trustedAccountList?.results?.filter(
      (ta) => ta.status === TrustedAccountStatus.ACTIVE
    );
  }

  transferringAccountUnderTheSameAccountHolder() {
    return this.transferringAccount.trustedAccountList?.results?.filter(
      (ta) => ta.underSameAccountHolder
    );
  }

  protected doSubmit() {
    this.selectedTransactionType.emit({
      proposedTransactionType: this.formGroup.get('transactionType').value,
      itlNotificationId: this.formGroup.get('itlNotificationId').value,
      clearNextStepsInWizard: this.formGroup.dirty,
    });
  }

  protected getFormModel(): any {
    return {
      transactionType: [this.transactionType, Validators.required],
      itlNotificationId: [
        this.itlNotificationId,
        Validators.pattern('^[0-9]*$'),
      ],
    };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      transactionType: {
        required: 'Select a transaction type',
      },
      itlNotificationId: {
        pattern: 'ITL notification ID should contain only numbers',
      },
    };
  }

  ngOnDestroy(): void {
    this.unsubscribe$.next();
    this.unsubscribe$.complete();
  }
}
