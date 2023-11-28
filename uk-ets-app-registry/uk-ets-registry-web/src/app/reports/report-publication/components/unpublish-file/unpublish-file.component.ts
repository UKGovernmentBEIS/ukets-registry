import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input,
  Output,
} from '@angular/core';
import { PublicationHistory } from '@report-publication/model';
import { empty } from '@shared/shared.util';

@Component({
  selector: 'app-unpublish-file',
  templateUrl: './unpublish-file.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UnpublishFileComponent {
  @Input() selectedPublicationFile: PublicationHistory;
  @Output() readonly emitter = new EventEmitter<any>();

  get trimLongFileName() {
    if (!empty(this.selectedPublicationFile?.fileName)) {
      const fileName = this.selectedPublicationFile.fileName;
      return fileName.replace(/(.{30})/g, '$1\n');
    }
  }

  onSubmit() {
    this.emitter.emit();
  }
}
