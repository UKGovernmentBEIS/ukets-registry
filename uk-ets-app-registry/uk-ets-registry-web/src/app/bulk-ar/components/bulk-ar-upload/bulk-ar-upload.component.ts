import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import {
  Component,
  EventEmitter,
  HostListener,
  Input,
  Output,
} from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { getConfigurationValue } from '@shared/shared.util';
import { Configuration } from '@shared/configuration/configuration.interface';

@Component({
  selector: 'app-bulk-ar-upload',
  templateUrl: './bulk-ar-upload.component.html',
})
export class BulkArUploadComponent extends UkFormComponent {
  @Input()
  isInProgress: boolean;

  @Input()
  fileProgress: number;

  @Input()
  configuration: Configuration[];
  allowedFileTypes: string[] = [];
  maxFileSize: number;

  @Output() readonly fileEmitter = new EventEmitter<File>();

  @Output() readonly cancelClicked = new EventEmitter<string>();

  hint = 'The table must be an XLSX';

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  @HostListener('change', ['$event.target.files'])
  onFileSelected(event: FileList) {
    this.showErrors = false;
    this.formGroup.patchValue({
      bulkAr: event && event.item(0),
    });
  }

  protected doSubmit() {
    this.fileEmitter.emit(this.formGroup.value.bulkAr);
  }

  protected getFormModel(): any {
    this.allowedFileTypes.push(
      getConfigurationValue('registry.file.bulk.ar.type', this.configuration)
    );
    this.maxFileSize = getConfigurationValue(
      'registry.file.max.size',
      this.configuration
    );
    return {
      bulkAr: [''],
    };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {};
  }
}
