import {
  Component,
  ElementRef,
  forwardRef,
  HostListener,
  inject,
  Input,
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
  implements Validator
{
  @Input() isInProgress: boolean;
  @Input() fileProgress: number;
  @Input() showErrors: boolean;
  @Input() hint = '';
  @Input() disableAllForms = false;
  @Input() fileType: string[];
  @Input() fileSize: number;

  @Input() set isFileRequired(value: boolean) {
    this._isFileRequired = value;
    this.formControl?.updateValueAndValidity();
  }
  get isFileRequired(): boolean {
    return this._isFileRequired;
  }
  private _isFileRequired: boolean;

  @Input() set uploadErrorMessage(value: string) {
    this._uploadErrorMessage = value;
    this.formControl?.updateValueAndValidity();
  }
  get uploadErrorMessage(): string {
    return this._uploadErrorMessage;
  }
  private _uploadErrorMessage: string;

  @ViewChild('fileInput', { static: false }) inputElement: ElementRef;

  private host = inject(ElementRef<HTMLInputElement>);
  private fb = inject(UntypedFormBuilder);

  @HostListener('change') onFileSelected() {
    this.uploadErrorMessage = null;
  }

  onClick() {
    this.clearInputText();
  }

  private clearInputText(): void {
    this.inputElement.nativeElement.value = '';
  }

  reset(): void {
    this.formControl?.reset();
    this.clearInputText();
    this.formControl?.updateValueAndValidity();
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
      uploadError: this.uploadErrorMessage || 'File upload failed',
    };
  }

  validate(control: AbstractControl): ValidationErrors | null {
    const file: File = control.value;
    if (this.isFileRequired && empty(file)) {
      return { selectFileRequired: true };
    }
    if (!empty(file) && !this.fileType.includes(file.type.toLowerCase())) {
      return { invalidFileType: true };
    }
    if (!empty(file) && file.size / 1024 > this.fileSize) {
      return { maxFileSizeExceeded: true };
    }
    if (!empty(file) && file.size == 0) {
      return { emptySelectedFile: true };
    }
    if (this.uploadErrorMessage) {
      return { uploadError: true };
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
      default:
        return `Invalid file type`;
    }
  }
}
