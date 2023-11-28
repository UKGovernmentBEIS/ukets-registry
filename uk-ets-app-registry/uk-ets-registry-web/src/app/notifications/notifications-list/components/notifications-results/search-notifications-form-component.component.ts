import {
  AfterViewInit,
  Component,
  EventEmitter,
  Input,
  Output,
} from '@angular/core';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { UntypedFormBuilder } from '@angular/forms';
import {
  FiltersDescriptor,
  NotificationSearchCriteria,
} from '@notifications/notifications-list/model';
import { Option } from '@shared/form-controls/uk-select-input/uk-select.model';

@Component({
  selector: 'app-search-notifications-form-component',
  templateUrl: './search-notifications-form-component.component.html',
})
export class SearchNotificationsFormComponentComponent
  extends UkFormComponent
  implements AfterViewInit
{
  private _filtersDescriptor: FiltersDescriptor;

  typeOptions: Option[];

  @Input()
  storedCriteria: NotificationSearchCriteria;

  @Input()
  get filtersDescriptor(): FiltersDescriptor {
    return this._filtersDescriptor;
  }

  set filtersDescriptor(filters: FiltersDescriptor) {
    this.typeOptions = filters.typeOptions;
  }

  @Output()
  readonly search = new EventEmitter<NotificationSearchCriteria>();
  @Output()
  readonly submitClick = new EventEmitter<null>();

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  protected getFormModel() {
    return {
      type: [''],
    };
  }

  protected getValidationMessages() {
    return {};
  }

  protected doSubmit() {
    this.submitClick.emit();
    this.search.emit(this.formGroup.value);
  }

  ngAfterViewInit() {
    if (this.storedCriteria) {
      this.formGroup.patchValue(this.storedCriteria);
    }
  }

  onClear() {
    this.formGroup.reset();
  }
}
