import { Component, EventEmitter, Input, Output } from '@angular/core';
import {
  ReactiveFormsModule,
  UntypedFormControl,
  Validators,
} from '@angular/forms';
import { ErrorDetail } from '@registry-web/shared/error-summary';
import { UkFormComponent } from '@registry-web/shared/form-controls/uk-form.component';
import { SharedModule } from '@registry-web/shared/shared.module';

@Component({
  standalone: true,
  imports: [SharedModule, ReactiveFormsModule],
  selector: 'app-declaration',
  templateUrl: './declaration.component.html',
  styleUrls: ['./declaration.component.scss'],
})
export class DeclarationComponent extends UkFormComponent {
  @Input() confirmed: boolean;
  @Output() readonly confirm = new EventEmitter<boolean>();
  @Output() readonly errorDetails = new EventEmitter<ErrorDetail[]>();

  protected getFormModel(): any {
    return {
      confirm: [this.confirmed, Validators.requiredTrue],
    };
  }

  get confirmControl(): UntypedFormControl {
    return this.formGroup.get('confirm') as UntypedFormControl;
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      confirm: {
        required: 'Must confirm to continue.',
      },
    };
  }

  onChange(event) {
    this.confirmControl.setValue(event.target.checked);
    this.confirmControl.updateValueAndValidity();
  }

  protected doSubmit() {
    this.confirm.emit(this.confirmControl.value);
  }
}
