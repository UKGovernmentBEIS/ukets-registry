import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Validators } from '@angular/forms';
import { PaymentDetails } from '@request-payment/model';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { Option } from '@shared/form-controls/uk-select-input/uk-select.model';
import { User } from '@shared/user';

@Component({
  selector: 'app-set-payment-details',
  templateUrl: './set-payment-details.component.html',
  styles: ``,
})
export class SetPaymentDetailsComponent
  extends UkFormComponent
  implements OnInit
{
  @Input()
  candidateRecipients: Pick<
    User,
    'firstName' | 'lastName' | 'alsoKnownAs' | 'urid' | 'state'
  >[];
  @Input()
  recipientUrid: string;
  @Input()
  recipientName: string;
  @Input()
  amount: number;
  @Input()
  description: string;
  @Output()
  readonly setPaymentDetails = new EventEmitter<PaymentDetails>();

  candidateRecipientOptions: Option[];

  ngOnInit() {
    this.candidateRecipientOptions = this.candidateRecipients
      //.filter((cr) => cr.status === 'ENROLLED')
      .map((cr) => {
        return {
          label:
            cr.alsoKnownAs && cr.alsoKnownAs.length > 0
              ? cr.alsoKnownAs
              : cr.firstName + ' ' + cr.lastName,
          value: cr.urid,
        };
      });
    super.ngOnInit();
  }

  protected doSubmit() {
    this.recipientUrid = this.formGroup.get('recipient').value;
    this.amount = this.formGroup.get('amount').value.replace(',', '');
    this.description = this.formGroup.get('description').value;
    this.recipientName = this.candidateRecipients
      .filter((cr) => cr.urid === this.recipientUrid)
      .map((cr) =>
        cr.alsoKnownAs && cr.alsoKnownAs.length > 0
          ? cr.alsoKnownAs
          : cr.firstName + ' ' + cr.lastName
      )[0];

    this.setPaymentDetails.emit({
      recipientUrid: this.recipientUrid,
      recipientName: this.recipientName,
      amount: this.amount,
      description: this.description,
    });
  }

  protected getFormModel(): any {
    return {
      recipient: [this.recipientUrid, Validators.required],
      amount: [
        this.amount,
        [
          Validators.required,
          Validators.pattern('\\d{0,2},?\\d{1,3}.?\\d{0,2}'),
          Validators.min(0.03),
          Validators.max(15000.0),
        ],
      ],
      description: [
        this.description,
        { validators: Validators.required, updateOn: 'change' },
      ],
    };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      recipient: {
        required: 'Please select a recipient.',
      },
      amount: {
        required: 'Please enter an amount.',
        pattern: 'Amount must be a number between 0.03 and 15000.00.',
        min: 'Amount must be between 0.03 and 15000.00',
        max: 'Amount must be between 0.03 and 15000.00',
      },
      description: {
        required: 'Please enter a description.',
      },
    };
  }
}
