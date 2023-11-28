import {
  Component,
  EventEmitter,
  HostListener,
  Input,
  Output,
} from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Configuration } from '@shared/configuration/configuration.interface';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { getConfigurationValue } from '@shared/shared.util';
import { ErrorSummary } from '@shared/error-summary';

@Component({
  selector: 'app-emissions-table-upload',
  templateUrl: './emissions-table-upload.component.html',
  styles: [],
})
export class EmissionsTableUploadComponent extends UkFormComponent {
  @Input()
  isInProgress: boolean;

  @Input()
  fileProgress: number;

  @Input()
  configuration: Configuration[];

  @Input()
  errorSummary: ErrorSummary;

  @Output() readonly fileEmitter = new EventEmitter<File>();

  @Output() readonly cancelClicked = new EventEmitter<string>();

  @Output() readonly downloadErrorsCSV = new EventEmitter<number>();

  allowedFileTypes: string[] = [];
  maxFileSize: number;

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  @HostListener('change', ['$event.target.files'])
  onFileSelected(event: FileList): void {
    this.showErrors = false;
    this.formGroup.patchValue({
      emissionsTable: event && event.item(0),
    });
  }

  protected doSubmit(): void {
    this.fileEmitter.emit(this.formGroup.value.emissionsTable);
  }

  protected getFormModel(): any {
    this.allowedFileTypes.push(
      getConfigurationValue(
        'registry.file.emissions.table.type',
        this.configuration
      )
    );
    this.maxFileSize = getConfigurationValue(
      'registry.file.max.size',
      this.configuration
    );
    return {
      emissionsTable: [''],
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
