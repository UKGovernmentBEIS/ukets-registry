import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Option } from '@shared/form-controls/uk-select-input/uk-select.model';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { AccountInfo, CommitmentPeriod } from '@shared/model/transaction';

@Component({
  selector: 'app-uk-kp-administration-issue-units',
  templateUrl: './select-commitment-period-account.component.html',
})
export class SelectCommitmentPeriodAccountComponent
  extends UkFormComponent
  implements OnInit
{
  @Input()
  selectedCommitmentPeriod: CommitmentPeriod;

  @Input()
  selectedAcquiringAccountInfo: AccountInfo;

  @Input()
  commitmentPeriodList: CommitmentPeriod[];
  @Input()
  acquiringAccountListForCommitmentPeriod: AccountInfo[];

  @Output()
  readonly selectedCommitmentPeriodChanged = new EventEmitter<CommitmentPeriod>();
  @Output()
  readonly selectedAcquiringAccountIdentifier = new EventEmitter<string>();

  // TODO: always remember to ovveride commitment period
  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  ngOnInit() {
    super.ngOnInit();
    if (this.selectedCommitmentPeriod) {
      this.commitmentPeriodControl.setValue(this.selectedCommitmentPeriod);
    }
    if (
      this.selectedAcquiringAccountInfo &&
      this.selectedAcquiringAccountInfo.identifier
    ) {
      this.acquiringAccountControl.setValue(
        this.selectedAcquiringAccountInfo.identifier
      );
    }
  }

  protected getFormModel() {
    return {
      commitmentPeriod: [
        '',
        { validators: [Validators.required], updateOn: 'change' },
      ],
      acquiringAccount: ['', Validators.required],
    };
  }

  get commitmentPeriodControl() {
    return this.formGroup.get('commitmentPeriod');
  }

  get acquiringAccountControl() {
    return this.formGroup.get('acquiringAccount');
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      commitmentPeriod: {
        required: 'Please select a commitment period.',
      },
      acquiringAccount: {
        required: 'Please select an acquiring account.',
      },
    };
  }

  commitmentPeriodOptions(): Option[] {
    return this.commitmentPeriodList
      ? this.commitmentPeriodList.map((c) => ({ label: c, value: c }))
      : [];
  }

  accountOptions(): Option[] {
    return this.acquiringAccountListForCommitmentPeriod
      ? this.acquiringAccountListForCommitmentPeriod.map((c) =>
          this.convertAcquiringAcountToOption(c)
        )
      : [];
  }

  convertAcquiringAcountToOption(info: AccountInfo): Option {
    return {
      label: `${info.fullIdentifier} - ${info.accountName}`,
      value: info.identifier,
    };
  }

  changeCommitmentPeriod() {
    const selected: CommitmentPeriod = this.commitmentPeriodControl.value;
    this.acquiringAccountControl.reset('');
    this.selectedCommitmentPeriodChanged.emit(selected);
  }

  protected doSubmit() {
    this.selectedAcquiringAccountIdentifier.emit(
      this.acquiringAccountControl.value
    );
  }
}
