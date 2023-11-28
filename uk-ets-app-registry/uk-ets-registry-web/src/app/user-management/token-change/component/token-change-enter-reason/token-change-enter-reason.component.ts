import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { Store } from '@ngrx/store';
import {
  UntypedFormBuilder,
  UntypedFormControl,
  Validators,
} from '@angular/forms';
import { canGoBack } from '@shared/shared.action';
import { TokenChangeState } from '@user-management/token-change/reducer';

@Component({
  selector: 'app-token-change-enter-reason',
  templateUrl: './token-change-enter-reason.component.html',
})
export class TokenChangeEnterReasonComponent
  extends UkFormComponent
  implements OnInit
{
  @Output() readonly submitReason = new EventEmitter<string>();
  @Input() state: TokenChangeState;

  constructor(private store: Store, protected formBuilder: UntypedFormBuilder) {
    super();
  }

  ngOnInit(): void {
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/user-details/my-profile`,
      })
    );
    super.ngOnInit();
  }

  get reasonControl(): UntypedFormControl {
    return this.formGroup.get('reason') as UntypedFormControl;
  }

  protected doSubmit() {
    this.submitReason.emit(this.reasonControl.value);
  }

  protected getFormModel(): any {
    return {
      reason: [
        this.state.reason,
        {
          validators: Validators.required,
          updateOn: 'change',
        },
      ],
    };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      reason: {
        required: 'Explain why you are requesting this change',
      },
    };
  }
}
