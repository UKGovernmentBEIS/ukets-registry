import { Component, EventEmitter, Input, Output } from '@angular/core';
import { AuthorisedRepresentative } from '@shared/model/account';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { UntypedFormBuilder } from '@angular/forms';
import { SelectArFormModel } from '@shared/form-controls/uk-select-authorised-representative';

@Component({
  selector: 'app-add-representative',
  templateUrl: './add-representative.component.html',
})
export class AddRepresentativeComponent extends UkFormComponent {
  @Input() authorisedRepresentatives: AuthorisedRepresentative[];
  @Output()
  readonly selectAuthorizedRepresentative = new EventEmitter<string>();

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  protected doSubmit() {
    this.selectAuthorizedRepresentative.emit(
      this.retrieveAuthorisedRepresentativeUrid(this.formGroup.value.selectAr)
    );
  }

  protected getFormModel(): any {
    return {
      selectAr: [''],
    };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {};
  }

  retrieveAuthorisedRepresentativeUrid(selectAr): string {
    return new SelectArFormModel(selectAr).getValue();
  }
}
