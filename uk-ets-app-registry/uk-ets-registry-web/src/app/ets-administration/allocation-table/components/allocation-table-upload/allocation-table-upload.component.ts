import {
  Component,
  EventEmitter,
  HostListener,
  Input,
  Output,
} from '@angular/core';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { UntypedFormBuilder } from '@angular/forms';
import { getConfigurationValue } from '@shared/shared.util';
import { Configuration } from '@shared/configuration/configuration.interface';
import { ErrorSummary } from '@shared/error-summary';

@Component({
  selector: 'app-allocation-table-upload',
  templateUrl: './allocation-table-upload.component.html',
})
export class AllocationTableUploadComponent extends UkFormComponent {
  @Input()
  isInProgress: boolean;

  @Input()
  fileProgress: number;

  @Input()
  configuration: Configuration[];

  @Input()
  errorSummary: ErrorSummary;

  @Output() readonly fileEmitter = new EventEmitter<File>();

  @Output() readonly downloadErrorsCSV = new EventEmitter<number>();

  @Output() readonly cancelClicked = new EventEmitter<string>();

  hint = 'The table must be an XLSX file';

  allowedFileTypes: string[] = [];
  maxFileSize: number;

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  @HostListener('change', ['$event.target.files'])
  onFileSelected(event: FileList) {
    this.showErrors = false;
    this.formGroup.patchValue({
      allocationTable: event && event.item(0),
    });
  }

  protected doSubmit() {
    this.fileEmitter.emit(this.formGroup.value.allocationTable);
  }

  protected getFormModel(): any {
    this.allowedFileTypes.push(
      getConfigurationValue(
        'registry.file.allocation.table.type',
        this.configuration
      )
    );
    this.maxFileSize = getConfigurationValue(
      'registry.file.max.size',
      this.configuration
    );
    return {
      allocationTable: [''],
    };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {};
  }

  containsErrorFile(): boolean {
    return this.errorSummary?.errors?.some((e) => !!e.errorFileId);
  }

  onDownloadErrorsCSV(): void {
    this.downloadErrorsCSV.emit(this.errorFileId());
  }

  errorFileName(): string {
    return this.errorSummary?.errors?.find((e) => e.errorFileId).errorFilename;
  }

  errorFileId(): number {
    return this.errorSummary?.errors?.find((e) => e.errorFileId).errorFileId;
  }
}
