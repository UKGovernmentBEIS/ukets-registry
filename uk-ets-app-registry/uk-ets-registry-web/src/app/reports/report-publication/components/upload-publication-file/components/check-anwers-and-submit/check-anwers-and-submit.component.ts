import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
} from '@angular/core';
import { SummaryListItem } from '@shared/summary-list/summary-list.info';
import { FileBase } from '@shared/model/file';
import { DisplayType, Section } from '@report-publication/model';

@Component({
  selector: 'app-check-anwers-and-submit',
  templateUrl: './check-anwers-and-submit.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CheckAnwersAndSubmitComponent implements OnInit {
  @Input() fileHeader: FileBase;
  @Input() fileYear: number;
  @Input() sectionDetails: Section;
  @Output() readonly navigateToEmitter = new EventEmitter<string>();
  @Output() readonly displayTypeEmitter = new EventEmitter<boolean>();
  @Output() readonly submitRequest = new EventEmitter<any>();

  ngOnInit(): void {
    if (this.isNotOneFilePerYear) {
      this.displayTypeEmitter.emit(true);
    } else {
      this.displayTypeEmitter.emit(false);
    }
  }

  navigateTo(value: string) {
    this.navigateToEmitter.emit(value);
  }

  get publicationFileItems(): SummaryListItem[] {
    const summaryListItems = [
      {
        key: { label: 'File' },
        value: [
          {
            label: this.fileHeader?.fileName,
          },
        ],
        action: {
          label: 'Change',
          visible: true,
          visuallyHidden: '',
          clickEvent: '/upload-publication-file',
        },
      },
    ];
    if (!this.isNotOneFilePerYear) {
      summaryListItems.push({
        key: { label: 'File year' },
        value: [
          {
            label: this.fileYear?.toString(),
          },
        ],
        action: {
          label: 'Change',
          visible: true,
          visuallyHidden: '',
          clickEvent: '/upload-publication-file/add-publication-year',
        },
      });
    }
    return summaryListItems;
  }

  submitChanges() {
    this.submitRequest.emit();
  }

  onCancel() {
    console.log('cancel clicked');
  }

  get isNotOneFilePerYear() {
    return (
      this.sectionDetails?.displayType === DisplayType.ONE_FILE ||
      this.sectionDetails?.displayType === DisplayType.MANY_FILES
    );
  }
}
