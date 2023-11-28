import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input,
  Output,
} from '@angular/core';
import { ReportType, reportTypeMap } from '@registry-web/reports/model';
import { SortParameters } from '@registry-web/shared/search/sort/SortParameters';
import { SummaryListItem } from '@registry-web/shared/summary-list/summary-list.info';
import {
  DisplayTypeMap,
  PublicationFrequency,
  PublicationFrequencyMap,
  PublicationHistory,
  Section,
} from '@report-publication/model';
import { empty, generateHoursOptions } from '@shared/shared.util';
import { GdsDatePipe, GdsDateShortNoYearPipe } from '@shared/pipes';

@Component({
  selector: 'app-section-details',
  templateUrl: './section-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SectionDetailsComponent {
  @Input() section: Section;
  @Input() publicationHistory: PublicationHistory[];
  @Input() sortParameters: SortParameters;
  @Output() readonly sort = new EventEmitter<SortParameters>();
  @Output() readonly emitter = new EventEmitter<PublicationHistory>();
  @Output() readonly download = new EventEmitter<number>();
  sectionsWithGenerationDate: Array<ReportType> = [
    ReportType.R0017,
    ReportType.R0025,
    ReportType.R0028,
    ReportType.R0031,
  ];

  timeOptions = generateHoursOptions();

  constructor(
    private gdsDatePipe: GdsDatePipe,
    private gdsDateShortNoYear: GdsDateShortNoYearPipe
  ) {}

  onSorting($event: SortParameters) {
    this.sort.emit($event);
  }

  onUnpublish($event: PublicationHistory) {
    this.emitter.emit($event);
  }

  onDownload($event: number) {
    this.download.emit($event);
  }

  getSectionDetailsSummaryListItems(): SummaryListItem[] {
    return [
      {
        key: {
          label: 'Section details',
          class: 'summary-list-change-header-font-weight govuk-body-l',
        },
        value: [
          {
            label: '',
            class: '',
          },
        ],
      },
      {
        key: { label: 'Display order' },
        value: [
          {
            label: String(this.section?.displayOrder),
            class: 'govuk-summary-list__value',
          },
        ],
      },
      {
        key: { label: 'Report type' },
        value: [
          {
            label:
              !empty(this.section) && !empty(this.section.reportType)
                ? reportTypeMap[this.section.reportType].label
                : '-',
            class: 'govuk-summary-list__value',
          },
        ],
      },
      {
        key: { label: 'Title' },
        value: [
          {
            label: this.section?.title,
            class: 'govuk-summary-list__value',
          },
        ],
      },
      {
        key: { label: 'Summary' },
        value: [
          {
            label: this.section?.summary,
            class: 'govuk-summary-list__value',
          },
        ],
      },
      {
        key: { label: 'Display type' },
        value: [
          {
            label: !empty(this.section?.displayType)
              ? DisplayTypeMap[this.section?.displayType]
              : '-',
            class: 'govuk-summary-list__value',
          },
        ],
      },
    ];
  }

  getPublicationDetailsSummaryListItems(): SummaryListItem[] {
    const summaryListItems: SummaryListItem[] = [
      {
        key: { label: 'Report publication frequency' },
        value: [
          {
            label: !empty(this.section?.publicationFrequency)
              ? PublicationFrequencyMap[this.section?.publicationFrequency]
              : '-',
            class: 'govuk-summary-list__value',
          },
        ],
      },
    ];
    if (
      this.section?.publicationFrequency === PublicationFrequency.EVERY_X_DAYS
    ) {
      summaryListItems.push({
        key: { label: 'Recur every' },
        value: [
          {
            label: String(this.section?.everyXDays) + ' day(s)',
            class: 'govuk-summary-list__value',
          },
        ],
      });
    }
    if (this.section?.publicationFrequency !== PublicationFrequency.DISABLED) {
      summaryListItems.push(
        {
          key: { label: 'Publication start date' },
          value: [
            {
              label: this.gdsDatePipe.transform(this.section?.publicationStart),
              class: 'govuk-summary-list__value',
            },
          ],
        },
        {
          key: { label: 'Publication time' },
          value: [
            {
              label: this.constructTime(this.section?.publicationTime),
              class: 'govuk-summary-list__value',
            },
          ],
        }
      );
      if (this.sectionsWithGenerationDate.includes(this.section?.reportType)) {
        summaryListItems.push({
          key: { label: 'Cut-off day and time' },
          value: [
            {
              label:
                this.constructDate(this.section?.generationDate) +
                this.constructTime(this.section?.generationTime),
              class: 'govuk-summary-list__value',
            },
          ],
        });
      }
    }
    return summaryListItems;
  }

  constructTime(time: string) {
    if (empty(time)) {
      return '';
    }
    return this.timeOptions.filter((timeOption) => timeOption.value === time)[0]
      ?.label;
  }

  constructDate(date: string) {
    if (empty(date)) {
      return '';
    }
    return this.gdsDateShortNoYear.transform(date) + ', ';
  }

  get isFrequencyDisabled() {
    return this.section?.publicationFrequency === PublicationFrequency.DISABLED;
  }
}
