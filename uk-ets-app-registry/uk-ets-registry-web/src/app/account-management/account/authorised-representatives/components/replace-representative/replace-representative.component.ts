import { Component, EventEmitter, Input, Output } from '@angular/core';
import { AuthorisedRepresentative } from '@shared/model/account';
import { Option } from '@shared/form-controls/uk-select-input/uk-select.model';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { SelectArFormModel } from '@shared/form-controls/uk-select-authorised-representative';
import { ErrorSummary } from '@shared/error-summary';

@Component({
  selector: 'app-replace-representative',
  templateUrl: './replace-representative.component.html',
})
export class ReplaceRepresentativeComponent extends UkFormComponent {
  @Input() currentAuthorisedRepresentatives: Option[];
  @Input() authorisedRepresentativesOtherAccounts: AuthorisedRepresentative[];
  @Input() errorSummary: ErrorSummary;
  @Output() readonly replaceAuthorisedRepresentative = new EventEmitter<any>();

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  protected doSubmit() {
    this.showErrors = true;
    this.replaceAuthorisedRepresentative.emit(
      this.retrieveArUrids(this.formGroup.value)
    );
  }

  protected getFormModel(): any {
    return {
      selectCurrentAr: ['', Validators.required],
      selectArOtherAccounts: [''],
    };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      selectCurrentAr: {
        required: 'Please select one from the list.',
      },
    };
  }

  private retrieveArUrids(replaceAr: {
    selectCurrentAr;
    selectArOtherAccounts;
    x;
  }) {
    const replaceeUrid = replaceAr.selectCurrentAr;
    const candidateUrid = new SelectArFormModel(
      replaceAr.selectArOtherAccounts
    ).getValue();
    return { candidateUrid, replaceeUrid };
  }
}
