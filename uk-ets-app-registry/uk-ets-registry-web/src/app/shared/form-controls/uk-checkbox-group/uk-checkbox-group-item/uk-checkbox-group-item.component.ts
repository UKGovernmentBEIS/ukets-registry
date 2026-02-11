import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  ContentChild,
  inject,
  input,
  TemplateRef,
  ViewChild,
} from '@angular/core';
import { ControlValueAccessor } from '@angular/forms';
import { ConditionalContentDirective } from '../../conditional-content/conditional-content.directive';

@Component({
  selector: 'app-uk-checkbox-group-item',
  standalone: true,
  templateUrl: './uk-checkbox-group-item.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UkCheckboxGroupItemComponent<T> implements ControlValueAccessor {
  readonly changeDetectorRef = inject(ChangeDetectorRef);

  readonly value = input.required<T>();
  readonly label = input.required<string>();
  readonly hint? = input<string>();

  @ContentChild(ConditionalContentDirective)
  readonly conditional: ConditionalContentDirective;

  @ViewChild('conditionalTemplate', { static: true })
  readonly conditionalTemplate: TemplateRef<any>;

  @ViewChild('checkboxTemplate', { static: true })
  readonly optionTemplate: TemplateRef<any>;

  isChecked: boolean;
  index: number;
  isDisabled: boolean;
  isTouched: boolean;
  onBlur: () => any;
  onChange: (event: Event) => any;
  groupIdentifier: string;

  get identifier(): string {
    return `${this.groupIdentifier}-${this.index}`;
  }

  registerOnChange(onChange: () => any): void {
    this.onChange = (event) => {
      this.writeValue((event.target as HTMLInputElement).checked);
      onChange();
    };
  }

  registerOnTouched(onBlur: () => any): void {
    this.onBlur = () => {
      this.isTouched = true;
      onBlur();
    };
  }

  writeValue(value: boolean): void {
    this.isChecked = value;
    this.setConditionalDisabledState();
  }

  setDisabledState(isDisabled: boolean) {
    this.isDisabled = isDisabled;
    this.setConditionalDisabledState();
    this.changeDetectorRef.markForCheck();
  }

  private setConditionalDisabledState() {
    if (this.isChecked && !this.isDisabled) {
      this.conditional?.enableControls();
    } else {
      this.conditional?.disableControls();
    }
  }
}
