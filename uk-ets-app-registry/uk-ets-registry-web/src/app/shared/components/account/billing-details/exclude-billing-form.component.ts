import {
  AfterViewInit,
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input,
  Output,
} from '@angular/core';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { saveExcludeBillingRemarks } from '@registry-web/account-management/account/account-details/account.actions';
import { debounceTime } from 'rxjs';

@Component({
  selector: 'app-exclude-billing-form',
  templateUrl: './exclude-billing-form.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ExcludeBillingFormComponent
  extends UkFormComponent
  implements AfterViewInit
{
  @Input()
  remarks: string;

  @Output()
  readonly remarksOutput = new EventEmitter<string>();

  constructor(protected formBuilder: UntypedFormBuilder, private store: Store) {
    super();
  }

  ngAfterViewInit() {
    this.formGroup.controls['remarks'].valueChanges
      .pipe(debounceTime(250))
      .subscribe((value) =>
        this.store.dispatch(saveExcludeBillingRemarks({ remarks: value }))
      );
  }

  protected getFormModel(): any {
    return {
      remarks: [
        this.remarks,
        {
          validators: Validators.required,
          updateOn: 'change',
        },
      ],
    };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      remarks: {
        required: 'Enter exclusion remarks',
      },
    };
  }

  protected doSubmit() {
    this.remarksOutput.emit(this.formGroup.value['remarks']);
  }
}
