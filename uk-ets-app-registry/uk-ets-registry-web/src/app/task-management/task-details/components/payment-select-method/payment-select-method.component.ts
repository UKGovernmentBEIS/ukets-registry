import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { AvailablePaymentMethods, PaymentMethod } from '@request-payment/model';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { FormRadioGroupInfo } from '@shared/form-controls/uk-radio-input/uk-radio.model';

@Component({
  selector: 'app-payment-select-method',
  templateUrl: './payment-select-method.component.html',
  styles: ``,
})
export class PaymentSelectMethodComponent
  extends UkFormComponent
  implements OnInit
{
  @Input()
  paymentMethod: PaymentMethod;
  @Output()
  readonly selectedPaymentMethod = new EventEmitter<PaymentMethod>();

  formRadioGroupInfo: FormRadioGroupInfo;

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  ngOnInit() {
    super.ngOnInit();
    this.formRadioGroupInfo = {
      radioGroupHeading: 'Choose a way to pay',
      radioGroupHeadingCaption: 'Make Payment',
      //radioGroupHint: 'Select one option',
      key: 'paymentMethod',
      options: AvailablePaymentMethods,
    };
  }

  onContinue() {
    this.onSubmit();
  }

  protected doSubmit() {
    const selectedOption = AvailablePaymentMethods.find(
      (option) => option.value === this.formGroup.get('paymentMethod').value
    );
    this.selectedPaymentMethod.emit(<PaymentMethod>selectedOption.value);
  }

  protected getFormModel(): any {
    return {
      paymentMethod: [this.paymentMethod, Validators.required],
    };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      paymentMethod: {
        required: 'You must select a way to pay',
      },
    };
  }
}
