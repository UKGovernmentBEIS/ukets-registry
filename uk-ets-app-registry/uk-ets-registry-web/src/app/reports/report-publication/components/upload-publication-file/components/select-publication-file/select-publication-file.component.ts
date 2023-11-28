import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  HostListener,
  Input,
  OnInit,
  Output,
} from '@angular/core';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { Configuration } from '@shared/configuration/configuration.interface';
import { UntypedFormBuilder } from '@angular/forms';
import { getConfigurationValue } from '@shared/shared.util';

@Component({
  selector: 'app-select-publication-file',
  templateUrl: './select-publication-file.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SelectPublicationFileComponent extends UkFormComponent {
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

  hint = 'The file must be an .xlsx file';

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  @HostListener('change', ['$event.target.files'])
  onFileSelected(event: FileList) {
    this.showErrors = false;
    this.formGroup.patchValue({
      publicationReport: event && event.item(0),
    });
  }

  protected doSubmit() {
    this.fileEmitter.emit(this.formGroup.value.publicationReport);
  }

  protected getFormModel(): any {
    this.allowedFileTypes.push(
      getConfigurationValue(
        'registry.file.publication.report.type',
        this.configuration
      )
    );
    this.maxFileSize = getConfigurationValue(
      'registry.file.max.size',
      this.configuration
    );
    return {
      publicationReport: [''],
    };
  }
  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {};
  }
}
