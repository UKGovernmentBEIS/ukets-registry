import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import {
  AccountInfo,
  AcquiringAccountInfo,
  CandidateAcquiringAccounts,
  ProposedTransactionType,
} from '@shared/model/transaction';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { FormRadioGroupInfo } from '@shared/form-controls/uk-radio-input/uk-radio.model';

@Component({
  selector: 'app-select-acquiring-predefined-accounts',
  templateUrl: './select-acquiring-predefined-accounts.component.html',
})
export class SelectAcquiringPredefinedAccountsComponent
  extends UkFormComponent
  implements OnInit
{
  @Input()
  transactionType: ProposedTransactionType;

  @Input()
  selectedIdentifier: AccountInfo;

  @Input()
  candidateAcquiringAccountsResult: CandidateAcquiringAccounts;

  @Output()
  readonly predefinedAccountEmitter = new EventEmitter<AcquiringAccountInfo>();

  formRadioGroupInfo: FormRadioGroupInfo;

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  ngOnInit() {
    super.ngOnInit();
    this.formRadioGroupInfo = {
      radioGroupHeading: 'Specify acquiring account',
      radioGroupHeadingCaption: '',
      radioGroupHint: 'Select one account from the list',
      radioGroupSubHeading:
        this.candidateAcquiringAccountsResult
          .predefinedCandidateAccountsDescription,
      key: 'centralAccount',
      options: this.getPredefinedAccounts(),
    };

    if (this.selectedIdentifier) {
      this.centralAccountControl.setValue(
        this.selectedIdentifier.fullIdentifier.toString()
      );
    }
  }

  getPredefinedAccounts() {
    if (this.candidateAcquiringAccountsResult.predefinedCandidateAccounts) {
      return this.candidateAcquiringAccountsResult.predefinedCandidateAccounts.map(
        (account) => {
          return {
            label: account.accountName,
            value: account.fullIdentifier,
            enabled: true,
          };
        }
      );
    }
  }

  protected getFormModel(): any {
    return {
      centralAccount: ['', Validators.required],
    };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      centralAccount: {
        required: 'Select an acquiring account.',
      },
    };
  }

  get centralAccountControl() {
    return this.formGroup.get('centralAccount');
  }

  protected doSubmit() {
    const acquiringAccountInfos =
      this.candidateAcquiringAccountsResult.predefinedCandidateAccounts
        .filter(
          (account) =>
            account.fullIdentifier === this.centralAccountControl.value
        )
        .map((account) => account);
    this.predefinedAccountEmitter.emit(acquiringAccountInfos[0]);
  }
}
