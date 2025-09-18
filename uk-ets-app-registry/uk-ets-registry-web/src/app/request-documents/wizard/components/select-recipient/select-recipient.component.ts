import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Validators } from '@angular/forms';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { Option } from '@shared/form-controls/uk-select-input/uk-select.model';
import { User } from '@shared/user';

@Component({
  selector: 'app-select-recipient',
  templateUrl: './select-recipient.component.html',
})
export class SelectRecipientComponent
  extends UkFormComponent
  implements OnInit
{
  @Input()
  accountName: string;
  @Input()
  accountNumber: string;
  @Input()
  accountHolderName: string;
  @Input()
  candidateRecipients: User[];
  @Input()
  recipientUrid: string;
  @Input()
  comment: string;
  @Output() readonly setRecipient = new EventEmitter<{
    recipientUrid: string;
    recipientName: string;
    comment: string;
  }>();

  candidateRecipientOptions: Option[];

  ngOnInit() {
    this.candidateRecipientOptions = this.candidateRecipients
      .filter((cr) => cr.status === 'ENROLLED')
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

  getTitleText(): string {
    return this.accountHolderName
      ? 'Request account holder documents'
      : 'Request user documents';
  }

  protected doSubmit() {
    this.recipientUrid = this.formGroup.get('recipient').value;
    this.comment = this.formGroup.get('comment').value;
    this.setRecipient.emit({
      recipientUrid: this.recipientUrid,
      recipientName: this.candidateRecipients
        .filter((cr) => cr.urid === this.recipientUrid)
        .map((cr) =>
          cr.alsoKnownAs && cr.alsoKnownAs.length > 0
            ? cr.alsoKnownAs
            : cr.firstName + ' ' + cr.lastName
        )[0],
      comment: this.comment,
    });
  }

  protected getFormModel(): any {
    return {
      recipient: [this.recipientUrid, Validators.required],
      comment: [
        this.comment,
        { validators: Validators.required, updateOn: 'change' },
      ],
    };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      recipient: {
        required: 'Please select a recipient.',
      },
      comment: {
        required: 'Please enter a comment.',
      },
    };
  }
}
