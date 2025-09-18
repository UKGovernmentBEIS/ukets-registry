import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  HostListener,
  Input,
  OnChanges,
  OnInit,
  Output,
  SimpleChanges,
} from '@angular/core';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { Configuration } from '@shared/configuration/configuration.interface';
import {
  ReactiveFormsModule,
  UntypedFormBuilder,
  Validators,
} from '@angular/forms';
import { getConfigurationValue } from '@shared/shared.util';
import { SharedModule } from '@registry-web/shared/shared.module';
import { DocumentUpdateType } from '@registry-web/documents/models/document-update-type.model';
import { SaveRegistryDocumentDTO } from '@registry-web/documents/models/document.model';

@Component({
  standalone: true,
  imports: [ReactiveFormsModule, SharedModule],
  selector: 'app-upload-file',
  templateUrl: './upload-file.component.html',
  styleUrls: ['./upload-file.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UploadFileComponent extends UkFormComponent implements OnChanges {
  @Input() storedDocumentFile: File;

  @Input() storedDocument: SaveRegistryDocumentDTO;

  @Input()
  isInProgress: boolean;

  @Input()
  fileProgress: number;

  @Input()
  configuration: Configuration[];

  @Input()
  updateType: DocumentUpdateType;

  @Input()
  loadingFile: boolean;

  @Input()
  fileUpdated: boolean;

  DocumentUpdateType = DocumentUpdateType;
  allowedFileTypes: string[] = [];
  maxFileSize: number;
  isFileRequired: boolean = false;
  showChooseFileBtn: boolean = true;

  @Output() readonly fileEmitter = new EventEmitter<{
    file: File;
    title: string;
    fileUpdated: boolean;
  }>();
  @Output() readonly cancelClicked = new EventEmitter<string>();
  @Output() readonly downloadStoredFileEmitter = new EventEmitter();
  @Output() readonly removeStoredFileEmitter = new EventEmitter();

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (
      changes['storedDocumentFile'] ||
      changes['updateType'] ||
      changes['loadingFile']
    ) {
      this._updateShowChooseFileButton();
    }

    if (changes['updateType']) {
      this.isFileRequired =
        changes['updateType'].currentValue === DocumentUpdateType.ADD_DOCUMENT;
    }
  }

  private _updateShowChooseFileButton() {
    if (
      this.updateType === DocumentUpdateType.UPDATE_DOCUMENT &&
      this.loadingFile
    ) {
      this.showChooseFileBtn = false;
      this.fileUpdated = false;
    } else {
      this.showChooseFileBtn = this.storedDocumentFile == null;
    }
  }

  @HostListener('change', ['$event.target.files'])
  onFileSelected(event: FileList) {
    this.showErrors = false;
    if (event) {
      this.formGroup.get('document').setValue(event && event.item(0));
      this.fileUpdated = true;
    }
  }

  protected doSubmit() {
    this.fileEmitter.emit({
      file: this.formGroup.value.document,
      title: this.formGroup.value.title,
      fileUpdated: this.fileUpdated,
    });
  }

  protected getFormModel(): any {
    this.allowedFileTypes = getConfigurationValue(
      'registry.file.document.area.type',
      this.configuration
    ).split(',');
    this.maxFileSize = getConfigurationValue(
      'registry.file.max.size',
      this.configuration
    );
    return {
      document: [this.storedDocumentFile],
      title: [this.storedDocument?.title, [Validators.required]],
    };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      title: {
        required: 'Add title',
      },
      document: {
        invalidFileType:
          'The selected file must be a DOC, DOCX, XLS, XLSX, PPT, PPTX, VSD, VSDX, JPG, JPEG, PDF, PNG, TIF, TXT, DIB, BMP or CSV',
      },
    };
  }

  downloadStoredDocument() {
    this.downloadStoredFileEmitter.emit();
  }

  removeStoredFile() {
    this.formGroup.get('document').setValue(null);
    this.removeStoredFileEmitter.emit();

    if (this.updateType === DocumentUpdateType.UPDATE_DOCUMENT) {
      this.isFileRequired = true;
    }
  }
}
