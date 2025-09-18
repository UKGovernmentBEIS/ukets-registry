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
import {
  AccountSearchCriteria,
  accountStatusMap,
  allocationWithholdStatusMap,
  allocationStatusMap,
  FiltersDescriptor,
  regulatorMap,
} from '../account-list.model';
import { Option } from '@shared/form-controls/uk-select-input/uk-select.model';
import {
  getOptionsFromMap,
  getOptionsFromStatusMap,
} from '@shared/shared.util';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { complianceStatusMap } from '@account-shared/model';
import { UkRegistryValidators } from '@shared/validation';

@Component({
  selector: 'app-search-accounts-form',
  templateUrl: './search-accounts-form.component.html',
  styleUrls: ['./search-accounts-form.scss'],
})
export class SearchAccountsFormComponent
  extends UkFormComponent
  implements OnInit
{
  @Input()
  get filtersDescriptor(): FiltersDescriptor {
    return this._filtersDescriptor;
  }

  set filtersDescriptor(filters: FiltersDescriptor) {
    this.accountStatusOptions = this.accountStatusOptions.filter((status) =>
      filters.accountStatusOptions.includes(status.value)
    );
    this.accountStatusOptions.unshift({
      label: '',
      value: null,
    });
    this.accountTypeOptions = filters.accountTypeOptions;
    this.accountTypeOptions.unshift({
      label: '',
      value: null,
    });
    this._filtersDescriptor = filters;
  }

  private _filtersDescriptor: FiltersDescriptor;

  @Input() storedCriteria: AccountSearchCriteria;
  @Input() isAdmin: boolean;
  @Input() isAR: boolean;
  @Input() showAdvancedSearch: boolean;

  @Output() readonly search = new EventEmitter<AccountSearchCriteria>();
  @Output() readonly submitClick = new EventEmitter<null>();
  @Output() readonly advancedSearch = new EventEmitter<boolean>();

  @ViewChild('summary') summary: ElementRef;

  accountStatusOptions: Option[] = getOptionsFromStatusMap(accountStatusMap);
  complianceStatusOptions: Option[] =
    getOptionsFromStatusMap(complianceStatusMap);

  regulatorOptions: Option[] = getOptionsFromMap(regulatorMap);
  allocationStatusOptions: Option[] = getOptionsFromMap(allocationStatusMap);
  allocationWithholdStatusOptions: Option[] = getOptionsFromMap(
    allocationWithholdStatusMap
  );
  accountTypeOptions: Option[];

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  ngOnInit() {
    super.ngOnInit();
    if (!this.isAdmin) {
      //See UKETS-6405 for this one
      this.complianceStatusOptions = this.complianceStatusOptions.filter(
        (s) => s.value != 'ERROR'
      );
    }
    if (this.storedCriteria) {
      this.formGroup.patchValue(this.storedCriteria);
      this.search.emit(this.formGroup.value);
    }
  }

  onClear() {
    this.formGroup.reset();
  }

  protected getFormModel() {
    return {
      accountIdOrName: ['', Validators.minLength(3)],
      accountStatus: [''],
      accountType: [''],
      accountHolderName: ['', Validators.minLength(3)],
      complianceStatus: [''],
      permitOrMonitoringPlanIdentifier: ['', Validators.minLength(3)],
      authorizedRepresentativeUrid: ['', Validators.minLength(3)],
      regulatorType: [''],
      excludedForYear: ['', UkRegistryValidators.isValidYear()],
      allocationStatus: [''],
      allocationWithholdStatus: [''],
      operatorId: ['', Validators.minLength(3)],
      imo: [''],
    };
  }

  protected getValidationMessages(): {
    [key: string]: { [key: string]: string };
  } {
    return {
      accountIdOrName: {
        minlength: 'Enter at least 3 characters in the "Account name or ID"',
      },
      accountHolderName: {
        minlength: 'Enter at least 3 characters in the "Account Holder name"',
      },
      permitOrMonitoringPlanIdentifier: {
        minlength:
          'Enter at least 3 characters in the "Permit or monitoring plan ID"',
      },
      authorizedRepresentativeUrid: {
        minlength:
          'Enter at least 3 characters in the "Authorised Representative ID"',
      },
      operatorId: {
        minlength:
          'Enter at least 3 characters in the "Installation or Aircraft Operator ID"',
      },
      excludedForYear: {
        invalidYear: 'Not a valid year.',
      },
    };
  }

  protected doSubmit() {
    this.submitClick.emit();
    this.search.emit(this.formGroup.value);
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
