import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import {
  AbstractControl,
  UntypedFormBuilder,
  UntypedFormGroup,
  Validators,
} from '@angular/forms';
import {
  EnvironmentalActivityLabelMap,
  PROJECT_TRACK_LABELS,
  ProjectTrack,
  ProposedTransactionType,
  TransactionBlockSummary,
  TransactionType,
} from '@shared/model/transaction';
import { Option } from '@shared/form-controls/uk-select-input/uk-select.model';
import { ItlNotification } from '@shared/model/transaction/itl-notification';
import { UserDefinedAccountParts } from '@registry-web/shared/model/account';

@Component({
  selector: 'app-select-unit-types-and-quantity',
  templateUrl: './specify-unit-types-and-quantity.component.html',
})
export class SpecifyUnitTypesAndQuantityComponent
  extends UkFormComponent
  implements OnInit
{
  checkedIdSet = new Set<number>();
  @Input()
  transactionType: ProposedTransactionType;

  @Input()
  itlNotification: ItlNotification;

  @Input()
  transactionBlockSummaries: TransactionBlockSummary[];

  @Input()
  selectedTransactionBlockSummaries: TransactionBlockSummary[];

  @Input()
  toBeReplacedUnitsAccountParts: UserDefinedAccountParts;

  @Output() readonly selectedSummaries = new EventEmitter<{
    selectedTransactionBlockSummaries: TransactionBlockSummary[];
    clearNextStepsInWizard: boolean;
    toBeReplacedUnitsHoldingAccountParts: UserDefinedAccountParts;
  }>();

  isTransactionConversionAorB: boolean;
  isReplacement: boolean;

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  ngOnInit() {
    super.ngOnInit();
    this.isTransactionConversionAorB =
      this.transactionType?.type === TransactionType.ConversionA ||
      this.transactionType?.type === TransactionType.ConversionB ||
      this.transactionType?.type === TransactionType.ConversionCP1;
    this.isReplacement =
      this.transactionType?.type === TransactionType.Replacement;
    if (this.selectedTransactionBlockSummaries.length > 0) {
      this.selectedTransactionBlockSummaries.forEach((s) => {
        const index = s.index.toString();
        this.addForm(index);
        this.getQuantityControl(index).setValue(s.quantity);
        this.getProjectControl(index).setValue(s.projectNumber);
        this.getEnvironmentalActivityControl(index).setValue(
          s.environmentalActivity
        );
        if (this.isTransactionConversionAorB) {
          this.getConversionProjectNumberControl(index).setValue(
            s.projectNumber
          );
          this.getConversionProjectTrackControl(index).setValue(s.projectTrack);
        }
        this.checkedIdSet.add(s.index);
      });
    }
    if (this.toBeReplacedUnitsAccountParts) {
      this.userDefinedCountryCodeControl.setValue(
        this.toBeReplacedUnitsAccountParts.userDefinedCountryCode
      );
      this.userDefinedAccountTypeControl.setValue(
        this.toBeReplacedUnitsAccountParts.userDefinedAccountType
      );
      this.userDefinedAccountIdControl.setValue(
        this.toBeReplacedUnitsAccountParts.userDefinedAccountId
      );
      this.userDefinedPeriodControl.setValue(
        this.toBeReplacedUnitsAccountParts.userDefinedPeriod
      );
      this.userDefinedCheckDigitsControl.setValue(
        this.toBeReplacedUnitsAccountParts.userDefinedCheckDigits
      );
    }
  }

  protected getFormModel(): any {
    return {
      userDefinedCountryCode: [''],
      userDefinedAccountType: [''],
      userDefinedAccountId: [''],
      userDefinedPeriod: [''],
      userDefinedCheckDigits: [''],
    };
  }

  toggle(checked: boolean, i: number) {
    if (checked) {
      this.checkedIdSet.add(i);
      this.addForm(i.toString());
      if (
        this.itlNotification?.projectNumber &&
        this.transactionType.type !== TransactionType.Replacement &&
        this.transactionType.type !== TransactionType.ExternalTransfer &&
        this.transactionType.type !== TransactionType.InternalTransfer
      ) {
        this.getProjectControl(i.toString()).setValue(
          this.itlNotification.projectNumber
        );
      }
    } else {
      this.checkedIdSet.delete(i);
      this.removeForm(i.toString());
    }
  }

  private addForm(index: string) {
    this.formGroup.addControl(index, this.buildQuantityGroup(index));
  }

  private removeForm(index: string) {
    this.formGroup.removeControl(index.toString());
  }

  buildQuantityGroup(index): UntypedFormGroup {
    const formConfig = {
      quantity: [''],
      project: [''],
      environmentalActivity: [''],
    };
    if (this.isTransactionConversionAorB) {
      formConfig['conversionProjectNumber_' + index] = [
        '',
        [Validators.required, Validators.pattern('^(GB)[0-9]{1,}')],
      ];
      formConfig['conversionProjectTrack_' + index] = ['', Validators.required];
    }

    return this.formBuilder.group(formConfig, { updateOn: 'change' });
  }

  getQuantityControl(index: string) {
    return this.formGroup.get(index).get('quantity');
  }

  getProjectControl(index: string) {
    return this.formGroup.get(index).get('project');
  }

  getConversionProjectNumberControl(index: string) {
    return this.formGroup.get(index).get('conversionProjectNumber_' + index);
  }

  getConversionProjectTrackControl(index: string) {
    return this.formGroup.get(index).get('conversionProjectTrack_' + index);
  }

  getEnvironmentalActivityControl(index: string) {
    return this.formGroup.get(index).get('environmentalActivity');
  }

  get userDefinedCountryCodeControl(): AbstractControl {
    return this.formGroup.get('userDefinedCountryCode');
  }

  get userDefinedAccountTypeControl(): AbstractControl {
    return this.formGroup.get('userDefinedAccountType');
  }

  get userDefinedAccountIdControl(): AbstractControl {
    return this.formGroup.get('userDefinedAccountId');
  }

  get userDefinedPeriodControl(): AbstractControl {
    return this.formGroup.get('userDefinedPeriod');
  }

  get userDefinedCheckDigitsControl(): AbstractControl {
    return this.formGroup.get('userDefinedCheckDigits');
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    const messages = {};
    if (this.isTransactionConversionAorB) {
      for (const [
        index,
        availableBlockSummary,
      ] of this.transactionBlockSummaries.entries()) {
        messages['conversionProjectNumber_' + index] = {
          required:
            'Project number is required for ' +
            availableBlockSummary.type.toString(),
          pattern:
            'Please enter a valid project number (i.e. GB1234) for ' +
            availableBlockSummary.type.toString(),
        };
        messages['conversionProjectTrack_' + index] = {
          required:
            'Project track is required for ' +
            availableBlockSummary.type.toString(),
        };
      }
    }
    return messages;
  }

  checked(index) {
    return (
      this.selectedTransactionBlockSummaries &&
      this.selectedTransactionBlockSummaries.filter((s) => s.index === index)
        .length > 0
    );
  }

  getProjectTrackOptions() {
    return [
      { label: '', value: null },
      {
        label: PROJECT_TRACK_LABELS.TRACK_1.label,
        value: ProjectTrack.TRACK_1,
      },
      {
        label: PROJECT_TRACK_LABELS.TRACK_2.label,
        value: ProjectTrack.TRACK_2,
      },
    ];
  }

  getProjectOptions(blockSummary: TransactionBlockSummary): Option[] {
    // TODO optional values in the <app-form-control-select>
    if (
      this.itlNotification &&
      this.itlNotification.projectNumber &&
      this.transactionType.type !== TransactionType.Replacement &&
      this.transactionType.type !== TransactionType.ExternalTransfer &&
      this.transactionType.type !== TransactionType.InternalTransfer
    ) {
      return blockSummary.projectNumbers.map((p) => ({
        label: p,
        value: p,
      }));
    }
    return [{ label: '', value: null }].concat(
      blockSummary.projectNumbers.map((p) => ({
        label: p,
        value: p,
      }))
    );
  }

  getEnvironmentalActivitiesOptions(
    blockSummary: TransactionBlockSummary
  ): Option[] {
    return [{ label: '', value: null }].concat(
      blockSummary.environmentalActivities.map((p) => ({
        label: EnvironmentalActivityLabelMap.get(p),
        value: p,
      }))
    );
  }

  /**
   * When fields are empty null should be sent to the server for optional value environmentalActivity
   * @param index the index of the selected transaction block summary
   */
  getEnvironmentalActivityValue(index: number) {
    const value = this.getEnvironmentalActivityControl(index.toString()).value;
    return value !== '' ? value : null;
  }

  /**
   * When fields are empty null should be sent to the server for optional value environmentalActivity
   * @param index the index of the selected transaction block summary
   */
  getProjectValue(index: number) {
    if (this.isTransactionConversionAorB) {
      const conversionProjectNumber = this.getConversionProjectNumberControl(
        index.toString()
      ).value;
      return conversionProjectNumber !== '' ? conversionProjectNumber : null;
    } else {
      const value = this.getProjectControl(index.toString()).value;
      return value !== '' ? value : null;
    }
  }

  getProjectTrack(index: number) {
    if (this.isTransactionConversionAorB) {
      const value = this.getConversionProjectTrackControl(
        index.toString()
      ).value;
      return value !== '' ? value : null;
    } else {
      return null;
    }
  }

  /**
   * get checked block summaries, assing the projectNumbers and quantities as set by the user and emit
   */
  protected doSubmit() {
    const selectedBlockSummaries: TransactionBlockSummary[] = [];
    for (const [
      index,
      availableBlockSummary,
    ] of this.transactionBlockSummaries.entries()) {
      if (this.checkedIdSet.has(index)) {
        selectedBlockSummaries.push({
          ...availableBlockSummary,
          index,
          quantity: this.getQuantityControl(index.toString()).value,
          projectNumber: this.getProjectValue(index),
          projectTrack: this.getProjectTrack(index),
          environmentalActivity: this.getEnvironmentalActivityValue(index),
        });
      }
    }
    this.selectedSummaries.emit({
      selectedTransactionBlockSummaries: selectedBlockSummaries,
      clearNextStepsInWizard: this.formGroup.dirty,
      toBeReplacedUnitsHoldingAccountParts: {
        userDefinedCountryCode:
          this.userDefinedCountryCodeControl.value.toUpperCase(),
        userDefinedAccountId: this.userDefinedAccountIdControl.value,
        userDefinedAccountType: this.userDefinedAccountTypeControl.value,
        userDefinedPeriod: this.userDefinedPeriodControl.value,
        userDefinedCheckDigits: this.userDefinedCheckDigitsControl.value,
      },
    });
  }
}
