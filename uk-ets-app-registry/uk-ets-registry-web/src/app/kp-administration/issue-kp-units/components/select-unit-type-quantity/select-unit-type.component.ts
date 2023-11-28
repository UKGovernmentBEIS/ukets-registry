import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { UnitTypeAndActivityPipe } from '@issue-kp-units/pipes';
import { RegistryLevelInfo } from '@shared/model/transaction';

@Component({
  selector: 'app-select-unit-type-component',
  templateUrl: './select-unit-type.component.html',
})
export class SelectUnitTypeComponent extends UkFormComponent implements OnInit {
  selectedRegistryLevelId: number;

  @Input()
  registryLevelInfos: RegistryLevelInfo[];

  @Input()
  selectedRegistryLevelInfo: RegistryLevelInfo;

  @Input()
  selectedQuantity: number;

  @Output() readonly selectedRegistryLevelAndQuantity = new EventEmitter<{
    registryLevelInfo: RegistryLevelInfo;
    quantity: number;
  }>();

  constructor(
    protected formBuilder: UntypedFormBuilder,
    private unitTypeAndActivityPipe: UnitTypeAndActivityPipe
  ) {
    super();
  }

  ngOnInit() {
    super.ngOnInit();
    this.unitTypeControl.setValue(
      this.unitTypeAndActivityPipe.transform(this.selectedRegistryLevelInfo)
    );
    if (this.selectedRegistryLevelInfo && this.selectedRegistryLevelInfo.id) {
      this.selectedRegistryLevelId = this.selectedRegistryLevelInfo.id;
    }
    this.quantityControl.setValue(this.selectedQuantity);
  }

  protected getFormModel(): any {
    return {
      unitTypeAndEnvironmentalActivity: [
        '',
        { validators: Validators.required, updateOn: 'change' },
      ],
      quantity: ['', { validators: Validators.required, updateOn: 'change' }],
    };
  }

  get unitTypeControl() {
    return this.formGroup.get('unitTypeAndEnvironmentalActivity');
  }

  get quantityControl() {
    return this.formGroup.get('quantity');
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      unitTypeAndEnvironmentalActivity: {
        required: 'Please select the unit type.',
      },
      quantity: {
        required: 'At least one non-zero quantity must be specified.',
      },
    };
  }

  unitTypeChanged(registryInfo: RegistryLevelInfo) {
    this.quantityControl.reset('');
    this.selectedRegistryLevelId = registryInfo.id;
  }

  protected doSubmit() {
    const registryLevelInfo: RegistryLevelInfo = this.registryLevelInfos.find(
      (r) => r.id === this.selectedRegistryLevelId
    );
    this.selectedRegistryLevelAndQuantity.emit({
      registryLevelInfo,
      quantity: this.quantityControl.value,
    });
  }
}
