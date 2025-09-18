import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  UntypedFormGroup,
  ValidationErrors,
  UntypedFormBuilder,
  Validators,
  ReactiveFormsModule,
} from '@angular/forms';
import { RouterModule } from '@angular/router';
import {
  RegistryDocument,
  RegistryDocumentCategory,
  UpdateRegistryDocumentCategoryDTO,
} from '@registry-web/documents/models/document.model';
import { ErrorDetail } from '@registry-web/shared/error-summary';
import { UkFormComponent } from '@registry-web/shared/form-controls/uk-form.component';
import { FormRadioGroupInfo } from '@registry-web/shared/form-controls/uk-radio-input/uk-radio.model';
import { SharedModule } from '@registry-web/shared/shared.module';
import { UkValidationMessageHandler } from '@registry-web/shared/validation';

@Component({
  standalone: true,
  selector: 'app-select-display-order-form',
  imports: [CommonModule, ReactiveFormsModule, RouterModule, SharedModule],
  styleUrls: ['./select-display-order-form.component.scss'],
  templateUrl: './select-display-order-form.component.html',
})
export class SelectDisplayOrderFormComponent
  extends UkFormComponent
  implements OnInit
{
  @Input()
  storedOrder: number;

  @Input()
  orderOptions: number[];

  @Output()
  handleSubmit = new EventEmitter<number>();

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  ngOnInit(): void {
    super.ngOnInit();
  }

  protected doSubmit() {
    this.handleSubmit.emit(this.formGroup.value.order);
  }

  protected getFormModel(): any {
    return {
      order: [
        this.storedOrder || Math.max.apply(null, this.orderOptions),
        {
          validators: [Validators.required],
        },
      ],
    };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      order: {
        required: 'Select order',
      },
    };
  }

  onContinue() {
    this.onSubmit();
  }
}
