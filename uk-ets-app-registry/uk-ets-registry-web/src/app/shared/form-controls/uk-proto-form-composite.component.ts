/* eslint-disable @typescript-eslint/no-empty-function */
import {
  ControlValueAccessor,
  UntypedFormControl,
  UntypedFormGroup,
  FormGroupDirective,
  NgControl,
} from '@angular/forms';
import {
  AfterContentInit,
  Component,
  EventEmitter,
  Injector,
  Input,
  OnDestroy,
  OnInit,
  Output,
  Type,
} from '@angular/core';
import { Subject } from 'rxjs';
import { takeUntil, tap } from 'rxjs/operators';
import { getControlName } from '../shared.util';

// eslint-disable-next-line @angular-eslint/use-component-selector
@Component({ template: `` })
export abstract class UkProtoFormCompositeComponent
  implements ControlValueAccessor, OnInit, AfterContentInit, OnDestroy
{
  @Input() showErrors: boolean;
  @Input() validationErrorMessages: { [key: string]: string };
  @Output() readonly nestedFormChangeEmitter = new EventEmitter<any>();

  nestedForm: UntypedFormGroup;
  controlName: string;
  private onChange = (value: any) => {};
  private onTouched = () => {};
  private onDestroy$: Subject<void> = new Subject();

  formControl: UntypedFormControl;
  compositeFormGroupId: string;
  protected constructor(
    protected parentF: FormGroupDirective,
    protected injector: Injector
  ) {}

  ngOnInit(): void {
    this.nestedForm = this.buildForm();
    this.nestedForm.valueChanges
      .pipe(
        tap((value) => {
          this.onChange(value);
          this.onTouched();
        }),
        takeUntil(this.onDestroy$)
      )
      .subscribe();
    // override defaultValues set in control with those set in the form
    this.validationErrorMessages = {
      ...this.getDefaultErrorMessageMap(),
      ...this.validationErrorMessages,
    };
    this.nestedFormChangeEmitter.emit(this.nestedForm?.value);
  }

  /**
   * Returns the default validation error messages
   */
  protected abstract getDefaultErrorMessageMap(): { [key: string]: string };

  /**
   * Builds and returns the nested form
   */
  protected abstract buildForm(): UntypedFormGroup;

  registerOnChange(fn: any): void {
    // when we want to let the parent
    // know that the value of the
    // form control should be updated
    this.onChange = fn;
  }
  registerOnTouched(fn: any): void {
    // when we want to let the parent
    // know that the form control
    // has been touched
    this.onTouched = fn;
  }

  writeValue(obj: any): void {
    if (obj) {
      this.nestedForm.setValue(obj, { emitEvent: false });
    }
  }

  public ngOnDestroy(): void {
    this.onDestroy$.next();
    this.onDestroy$.complete();
  }

  public getObjectKeys(o: any): string[] {
    return Object.keys(o);
  }

  ngAfterContentInit(): void {
    //  TS2352: Conversion of type 'typeof NgControl' to type 'Type<NgControl>'
    //  may be a mistake because neither type sufficiently overlaps with the
    //  other. If this was intentional, convert the expression to 'unknown'
    //  first.
    const ngControl = this.injector.get<NgControl>(
      NgControl as unknown as Type<NgControl>
    );
    this.formControl = ngControl.control as UntypedFormControl;
    this.controlName = getControlName(this.formControl);
    this.compositeFormGroupId = this.controlName
      ? this.controlName + '-label'
      : 'label';
  }
}
