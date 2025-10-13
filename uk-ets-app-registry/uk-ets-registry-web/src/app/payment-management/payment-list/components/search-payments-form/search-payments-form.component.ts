import {
  Component,
  ElementRef,
  EventEmitter,
  Input,
  OnInit,
  Output,
  ViewChild,
} from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { PaymentSearchCriteria } from '@payment-management/model';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';

@Component({
  selector: 'app-search-payments-form',
  templateUrl: './search-payments-form.component.html',
  styles: ``,
})
export class SearchPaymentsFormComponent
  extends UkFormComponent
  implements OnInit
{
  @Input()
  storedCriteria: PaymentSearchCriteria;
  @Input()
  isAdmin: boolean;
  @Input()
  showAdvancedSearch: boolean;

  @Output()
  readonly search = new EventEmitter<PaymentSearchCriteria>();
  @Output()
  readonly submitClick = new EventEmitter<null>();
  @Output()
  readonly advancedSearch = new EventEmitter<boolean>();

  @ViewChild('summary') summary: ElementRef;

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  protected getFormModel() {
    return {
      referenceNumber: [''],
    };
  }

  protected getValidationMessages(): {
    [key: string]: { [key: string]: string };
  } {
    return {
      referenceNumber: {
        maxLength: 'Enter at most 10 characters in the "Reference"',
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
