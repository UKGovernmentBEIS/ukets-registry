import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  ProposedTransactionType,
  TransactionBlockSummary,
  TransactionType,
} from '@shared/model/transaction';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { ItlNotification } from '@shared/model/transaction/itl-notification';

@Component({
  selector: 'app-specify-quantity',
  templateUrl: './specify-quantity.component.html',
})
export class SpecifyQuantityComponent
  extends UkFormComponent
  implements OnInit
{
  @Input()
  transactionType: ProposedTransactionType;
  @Input()
  itlNotification: ItlNotification;
  @Input()
  transactionBlockSummaries: TransactionBlockSummary[];
  @Input()
  selectedTransactionBlockSummaries: TransactionBlockSummary[];
  @Input()
  excessAmount: number;
  @Output() readonly selectedSummaries = new EventEmitter<{
    selectedTransactionBlockSummaries: TransactionBlockSummary[];
    clearNextStepsInWizard: boolean;
  }>();

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  ngOnInit() {
    super.ngOnInit();
    this.transactionBlockSummaries.forEach((s, index) => {
      this.addForm(index.toString());
      if (this.selectedTransactionBlockSummaries.length > 0) {
        this.selectedTransactionBlockSummaries
          .filter((selectedBlock) => selectedBlock.index === index)
          .forEach((selectedBlock) => {
            this.getSelectedQuantityControl(index.toString()).setValue(
              selectedBlock.quantity
            );
          });
      }
    });
  }

  showExcessAllocationText() {
    return this.transactionType?.type === TransactionType.ExcessAllocation;
  }

  chooseUnitsText() {
    if (this.showExcessAllocationText()) {
      return 'Choose the number of units to return';
    }
    return 'Choose the number of units';
  }

  protected getFormModel(): any {
    return {};
  }

  buildQuantityGroup(): UntypedFormGroup {
    return this.formBuilder.group(
      {
        selectQuantity: [this.excessAmount],
      },
      { updateOn: 'change' }
    );
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {};
  }

  protected doSubmit() {
    const selectedBlockSummaries: TransactionBlockSummary[] = [];
    for (const [
      index,
      availableBlockSummary,
    ] of this.transactionBlockSummaries.entries()) {
      selectedBlockSummaries.push({
        ...availableBlockSummary,
        index,
        quantity: this.getSelectedQuantityControl(index.toString()).value,
      });
    }
    this.selectedSummaries.emit({
      selectedTransactionBlockSummaries: selectedBlockSummaries,
      clearNextStepsInWizard: this.formGroup.dirty,
    });
  }

  getSelectedQuantityControl(index: string) {
    return this.formGroup.get(index).get('selectQuantity');
  }

  private addForm(index: string) {
    this.formGroup.addControl(index, this.buildQuantityGroup());
  }
}
