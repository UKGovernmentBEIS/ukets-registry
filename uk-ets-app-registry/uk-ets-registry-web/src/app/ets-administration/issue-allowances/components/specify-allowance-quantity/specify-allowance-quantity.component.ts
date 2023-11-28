import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  UntypedFormBuilder,
  UntypedFormGroup,
  Validators,
} from '@angular/forms';
import { AllowanceTransactionBlockSummary } from '@shared/model/transaction';
import {
  UkRegistryValidators,
  UkValidationMessageHandler,
} from '@shared/validation';
import { ErrorDetail } from '@shared/error-summary';

@Component({
  selector: 'app-specify-allowance-quantity',
  templateUrl: './specify-allowance-quantity.component.html',
})
// TODO : describe issue  when extending UkFormComponent
export class SpecifyAllowanceQuantityComponent implements OnInit {
  genericValidator: UkValidationMessageHandler;
  validationErrorMessage: { [key: string]: string } = {};
  validationMessages: { [key: string]: { [key: string]: string } };

  @Input()
  activeYear: number;

  formGroup: UntypedFormGroup;

  @Input()
  transactionBlockSummaries: Partial<AllowanceTransactionBlockSummary>[];

  @Input()
  selectedQuantity: string;

  @Output() readonly quantity = new EventEmitter<{
    quantity: number;
    year: number;
  }>();

  @Output() readonly errorDetails = new EventEmitter<ErrorDetail[]>();

  constructor(protected formBuilder: UntypedFormBuilder) {
    this.formGroup = this.formBuilder.group({
      quantity: [
        '',
        {
          validators: [
            Validators.required,
            UkRegistryValidators.isPositiveNumberWithoutDecimals,
          ],
          updateOn: 'change',
        },
      ],
    });
    this.validationMessages = this.getValidationMessages();
    this.genericValidator = new UkValidationMessageHandler(
      this.validationMessages
    );
  }

  ngOnInit() {
    if (this.selectedQuantity) {
      this.quantityControl.setValue(this.selectedQuantity);
    }
  }

  get quantityControl() {
    return this.formGroup.get('quantity');
  }

  trackByFn(index: number, summary: AllowanceTransactionBlockSummary) {
    return summary.year;
  }

  protected doSubmit() {
    this.quantity.emit({
      quantity: this.quantityControl.value,
      year: this.activeYear,
    });
  }

  onSubmit() {
    if (this.formGroup.valid) {
      this.doSubmit();
    } else {
      this.validationErrorMessage = this.genericValidator.processMessages(
        this.formGroup
      );
      this.errorDetails.emit(
        this.genericValidator.mapMessagesToErrorDetails(
          this.validationErrorMessage
        )
      );
    }
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      quantity: {
        required: 'One non-zero quantity must be specified.',
        invalid:
          'The quantity must be a positive number without decimal places.',
      },
    };
  }
}
