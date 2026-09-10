import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  accountAccessStateMap,
  AuthorisedRepresentative,
} from '@shared/model/account';
import { AuthorisedRepresentativesUpdateType } from '@authorised-representatives/model';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { userStatusMap } from '@shared/user';
import { AGENT_TYPE_LABELS } from '@user-management/user-details/model';
import { CRC_LABELS } from '@user-management/user-details/model/crc.model';

@Component({
  selector: 'app-select-representative-table',
  templateUrl: './select-representative-table.component.html',
})
export class SelectRepresentativeTableComponent
  extends UkFormComponent
  implements OnInit
{
  @Input()
  authorisedRepresentatives: AuthorisedRepresentative[];
  @Input()
  updateType: AuthorisedRepresentativesUpdateType;
  @Input()
  selectedArFromTableUrid: string;
  @Output()
  readonly selectAuthorisedRepresentative = new EventEmitter<string>();

  accountAccessStateMap = accountAccessStateMap;
  userStatusMap = userStatusMap;
  AGENT_TYPE_LABELS = AGENT_TYPE_LABELS;
  CRC_LABELS = CRC_LABELS;

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  ngOnInit() {
    super.ngOnInit();
  }

  onContinue() {
    this.onSubmit();
  }

  protected doSubmit() {
    this.selectAuthorisedRepresentative.emit(
      this.formGroup.get('authorisedRepresentative').value
    );
  }

  protected getFormModel(): any {
    return {
      authorisedRepresentative: [
        this.selectedArFromTableUrid,
        { validators: Validators.required, updateOn: 'change' },
      ],
    };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      authorisedRepresentative: {
        required: 'Select a user',
      },
    };
  }
}
