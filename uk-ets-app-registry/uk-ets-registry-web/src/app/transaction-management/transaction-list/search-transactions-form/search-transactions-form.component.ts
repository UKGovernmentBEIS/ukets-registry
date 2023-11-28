import {
  Component,
  ElementRef,
  EventEmitter,
  Input,
  OnInit,
  Output,
  ViewChild,
} from '@angular/core';
import { FiltersDescriptor } from '../transaction-list.model';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { Option } from '@shared/form-controls/uk-select-input/uk-select.model';
import { getOptionsFromStatusMap } from '@shared/shared.util';

import {
  TRANSACTION_TYPES_VALUES,
  TransactionSearchCriteria,
  transactionStatusMap,
  UNIT_TYPE_OPTIONS,
} from '@shared/model/transaction';

@Component({
  selector: 'app-search-transactions-form',
  templateUrl: './search-transactions-form.component.html',
})
export class SearchTransactionsFormComponent
  extends UkFormComponent
  implements OnInit
{
  @Input() storedCriteria: TransactionSearchCriteria;
  @Input() filtersDescriptor: FiltersDescriptor;
  @Input() isAdmin: boolean;
  @Input() showAdvancedSearch: boolean;

  @Output() readonly search = new EventEmitter<TransactionSearchCriteria>();
  @Output() readonly submitClick = new EventEmitter<null>();
  @Output() readonly advancedSearch = new EventEmitter<boolean>();

  @ViewChild('summary') summary: ElementRef;

  transactionStatusOptions = getOptionsFromStatusMap(transactionStatusMap);
  transactionTypeOptions = [
    {
      label: '',
      value: null,
    },
  ];
  unitTypeOptions = UNIT_TYPE_OPTIONS;

  accountTypeOptions: Option[];

  reversedOptions = [
    {
      label: '',
      value: null,
    },
    {
      label: 'Yes',
      value: true,
    },
    {
      label: 'No',
      value: false,
    },
  ];

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  protected getFormModel() {
    return {
      transactionId: ['', Validators.minLength(3)],
      transactionType: [''],
      transactionStatus: [''],
      transactionLastUpdateDateFrom: [''],
      transactionLastUpdateDateTo: [''],
      transferringAccountNumber: ['', Validators.minLength(3)],
      acquiringAccountNumber: ['', Validators.minLength(3)],
      acquiringAccountType: [''],
      transferringAccountType: [''],
      unitType: [''],
      initiatorUserId: ['', Validators.minLength(3)],
      approverUserId: ['', Validators.minLength(3)],
      transactionalProposalDateFrom: [''],
      transactionalProposalDateTo: [''],
      reversed: [''],
    };
  }

  protected getValidationMessages(): {
    [key: string]: { [key: string]: string };
  } {
    return {
      transactionId: {
        minlength: 'Enter at least 3 characters in the "Transaction ID"',
      },
      transferringAccountNumber: {
        minlength:
          'Enter at least 3 characters in the "Transferring account number"',
      },
      acquiringAccountNumber: {
        minlength:
          'Enter at least 3 characters in the "Acquiring account number"',
      },
      initiatorUserId: {
        minlength: 'Enter at least 3 characters in the "Initiator user ID"',
      },
      approverUserId: {
        minlength: 'Enter at least 3 characters in the "Approver user ID"',
      },
      transactionLastUpdateDateFrom: {
        pattern: 'The date you have entered is invalid.',
        ngbDate: ' ',
      },
      transactionLastUpdateDateTo: {
        pattern: 'The date you have entered is invalid.',
        ngbDate: ' ',
      },
      transactionalProposalDateFrom: {
        pattern: 'The date you have entered is invalid.',
        ngbDate: ' ',
      },
      transactionalProposalDateTo: {
        pattern: 'The date you have entered is invalid.',
        ngbDate: ' ',
      },
    };
  }

  protected doSubmit() {
    this.submitClick.emit();
    this.search.emit(this.formGroup.value);
  }

  onClear() {
    this.formGroup.reset();
  }

  ngOnInit() {
    super.ngOnInit();
    // UKETS 7094 added this to handle the NG0100 ExpressionChangedAfterItHasBeenCheckedError
    this.formGroup.reset();
    if (this.filtersDescriptor.transactionTypeOptions) {
      this.transactionTypeOptions = [{ label: '', value: null }].concat(
        this.filtersDescriptor.transactionTypeOptions.map(
          (descriptorOption) => {
            return {
              label:
                TRANSACTION_TYPES_VALUES[descriptorOption.value].label
                  .defaultLabel,
              value: descriptorOption.value,
            };
          }
        )
      );
    }
    this.transactionTypeOptions.sort((a, b) => {
      const textA = a.label.toUpperCase();
      const textB = b.label.toUpperCase();
      return textA < textB ? -1 : textA > textB ? 1 : 0;
    });

    if (this.filtersDescriptor.accountTypeOptions) {
      this.accountTypeOptions = [{ label: '', value: null }].concat(
        this.filtersDescriptor.accountTypeOptions.map((descriptorOption) => {
          return {
            label: descriptorOption.label,
            value: descriptorOption.value,
          };
        })
      );
    }

    if (this.storedCriteria) {
      this.formGroup.patchValue(this.storedCriteria);
      this.search.emit(this.formGroup.value);
    }
  }

  toggleAdvancedSearch(): void {
    const elm = this.summary.nativeElement as HTMLElement;
    setTimeout(() => {
      const showAdvancedSearch = elm.parentElement.attributes['open']
        ? true
        : false;
      this.advancedSearch.emit(showAdvancedSearch);
    });
  }
}
