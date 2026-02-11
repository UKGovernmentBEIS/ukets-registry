import { NgTemplateOutlet } from '@angular/common';
import {
  AfterContentInit,
  Component,
  ContentChildren,
  HostBinding,
  inject,
  OnInit,
  QueryList,
} from '@angular/core';
import {
  ControlContainer,
  ControlValueAccessor,
  FormGroupDirective,
  NgControl,
} from '@angular/forms';
import { UkProtoFormComponent } from '@shared/form-controls/uk-proto-form.component';
import { UkCheckboxGroupItemComponent } from './uk-checkbox-group-item/uk-checkbox-group-item.component';
import { getControlName } from '@shared/shared.util';

@Component({
  selector: 'app-uk-checkbox-group',
  templateUrl: './uk-checkbox-group.component.html',
  standalone: true,
  imports: [NgTemplateOutlet],
  viewProviders: [
    { provide: ControlContainer, useExisting: FormGroupDirective },
  ],
})
export class UkCheckboxGroupComponent<T>
  extends UkProtoFormComponent
  implements OnInit, AfterContentInit, ControlValueAccessor
{
  private readonly ngControl = inject(NgControl, {
    self: true,
    optional: true,
  })!;

  @HostBinding('class.govuk-!-display-block') readonly govukDisplayBlock = true;

  @ContentChildren(UkCheckboxGroupItemComponent) readonly options: QueryList<
    UkCheckboxGroupItemComponent<T>
  >;
  private onBlur: () => any;
  private onChange: (value: T[]) => void;
  private currentValue: T[] = [];

  constructor(parentF: FormGroupDirective) {
    super(parentF);
    this.ngControl.valueAccessor = this;
  }

  ngOnInit(): void {
    this.controlName = getControlName(this.ngControl.control);
    super.ngOnInit();
  }

  ngAfterContentInit(): void {
    this.options.forEach((option, index) => {
      option.groupIdentifier = this._id;
      option.index = index;
      option.registerOnChange(() => {
        this.currentValue = this.options
          .filter((option) => option.isChecked)
          .map((option) => option.value());
        this.onChange(this.currentValue);
      });
      option.registerOnTouched(() => this.onInputBlur());
      option.changeDetectorRef.markForCheck();
    });

    this.writeValue(this.formControl.value);
    this.setDisabledState(this.formControl.disabled);
  }

  writeValue(value: T[]): void {
    this.currentValue = value;
    this.options?.forEach((option) =>
      option.writeValue(value?.includes(option.value()) ?? false)
    );
  }

  registerOnChange(fn: (value: T[]) => void) {
    this.onChange = fn;
  }

  registerOnTouched(onBlur: () => any): void {
    this.onBlur = onBlur;
  }

  setDisabledState(isDisabled: boolean): void {
    this.options?.forEach((option) => option.setDisabledState(isDisabled));
  }

  onInputBlur(): void {
    if (
      !this.options ||
      Array.from(this.options).every((option) => option.isTouched)
    ) {
      this.onBlur();
    }
  }
}
