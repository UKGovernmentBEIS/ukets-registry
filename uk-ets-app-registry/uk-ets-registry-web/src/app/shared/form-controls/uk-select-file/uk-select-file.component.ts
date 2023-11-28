import {
  Component,
  ElementRef,
  forwardRef,
  Injector,
  Input,
  OnInit,
  ViewChild,
} from '@angular/core';
import {
  AbstractControl,
  ControlContainer,
  UntypedFormBuilder,
  UntypedFormGroup,
  FormGroupDirective,
  NG_VALIDATORS,
  NG_VALUE_ACCESSOR,
  ValidationErrors,
  Validator,
} from '@angular/forms';
import { UkProtoFormCompositeComponent } from '@shared/form-controls/uk-proto-form-composite.component';
import { empty } from '@shared/shared.util';
import { FileMimeTypes } from '@shared/model/file';

@Component({
  selector: 'app-uk-select-file',
  templateUrl: './uk-select-file.component.html',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => UkSelectFileComponent),
      multi: true,
    },
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => UkSelectFileComponent),
      multi: true,
    },
  ],
  viewProviders: [
    { provide: ControlContainer, useExisting: FormGroupDirective },
  ],
})
export class UkSelectFileComponent
  extends UkProtoFormCompositeComponent
  implements Validator, OnInit
{
  @Input()
  isInProgress: boolean;
  @Input()
  fileProgress: number;
  @Input()
  showErrors: boolean;
  @Input() hint = '';
  @Input() disableAllForms = false;
  @Input() fileType: string[];
  @Input() isFileRequired: boolean;
  @Input() fileSize: number;

  @ViewChild('fileInput', { static: false })
  inputElement: ElementRef;

  constructor(
    private host: ElementRef<HTMLInputElement>,
    protected parentF: FormGroupDirective,
    private fb: UntypedFormBuilder,
    protected injector: Injector
  ) {
    super(parentF, injector);
  }

  ngOnInit(): void {
    super.ngOnInit();
  }

  onClick() {
    this.inputElement.nativeElement.value = '';
  }

  writeValue(host: ElementRef<HTMLInputElement>) {
    this.host.nativeElement.value = '';
  }

  protected buildForm(): UntypedFormGroup {
    return this.fb.group({
      uploadFile: [''],
    });
  }

  protected getDefaultErrorMessageMap(): { [p: string]: string } {
    return {
      selectFileRequired: 'Please select a file',
      emptySelectedFile: 'The selected file is empty',
      invalidFileType: this.getErrorMessage(this.fileType[0]),
      maxFileSizeExceeded: `The file must be smaller than ${
        this.fileSize / 1024
      }MB`,
    };
  }

  validate(control: AbstractControl): ValidationErrors | null {
    const file: File = control.value;
    if (this.isFileRequired && empty(file)) {
      return {
        selectFileRequired: this.getDefaultErrorMessageMap().selectFileRequired,
      };
    }
    if (!empty(file) && !this.fileType.includes(file.type.toLowerCase())) {
      return {
        invalidFileType: this.getDefaultErrorMessageMap().invalidFileType,
      };
    }
    if (!empty(file) && file.size / 1024 > this.fileSize) {
      return {
        maxFileSizeExceeded:
          this.getDefaultErrorMessageMap().maxFileSizeExceeded,
      };
    }
    if (!empty(file) && file.size == 0) {
      return {
        emptySelectedFile: this.getDefaultErrorMessageMap().emptySelectedFile,
      };
    }
  }

  getErrorMessage(fileType: string): string {
    switch (fileType) {
      case FileMimeTypes.XLSX:
        return `The selected file must be an XLSX`;
      case FileMimeTypes.PDF:
      case FileMimeTypes.JPG:
      case FileMimeTypes.PNG:
        return `The selected file must be PDF, PNG or JPG`;
    }
  }
}
