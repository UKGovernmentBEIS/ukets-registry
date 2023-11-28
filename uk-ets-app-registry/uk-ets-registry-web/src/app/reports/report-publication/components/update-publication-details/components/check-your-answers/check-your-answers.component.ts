import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input,
  Output,
} from '@angular/core';
import {
  DisplayTypeMap,
  PublicationFrequencyMap,
  Section,
} from '@report-publication/model';
import { SummaryListItem } from '@shared/summary-list/summary-list.info';
import { GdsDatePipe, GdsDateShortNoYearPipe } from '@shared/pipes';
import { empty, generateHoursOptions } from '@shared/shared.util';
import { ReportType, reportTypeMap } from '@reports/model';
import { ErrorDetail } from '@shared/error-summary';

@Component({
  selector: 'app-check-your-answers',
  templateUrl: './check-your-answers.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CheckYourAnswersComponent {
  @Input() sectionDetails: Section;
  @Input() updatedDetails: any;
  @Output() readonly navigateToEmitter = new EventEmitter<string>();
  @Output() readonly submitRequest = new EventEmitter<any>();
  @Output() readonly errorDetails = new EventEmitter<ErrorDetail>();

  timeOptions = generateHoursOptions();
  sectionsWithGenerationDate: Array<ReportType> = [
    ReportType.R0017,
    ReportType.R0025,
    ReportType.R0028,
    ReportType.R0031,
  ];

  constructor(
    private gdsDatePipe: GdsDatePipe,
    private gdsDateShortNoYear: GdsDateShortNoYearPipe
  ) {}

  get sectionDetailsItems(): SummaryListItem[] {
    return [
      {
        key: {
          label: 'Section details',
          class: 'govuk-heading-m govuk-summary-list-width-one-third__key',
        },
        value: [
          {
            label: '',
            class: 'govuk-summary-list-width-one-third__value',
          },
        ],
        action: {
          label: 'Change',
          visible: true,
          visuallyHidden: '',
          clickEvent: '/update-publication-details',
          class: 'govuk-summary-list-width-one-third__action',
        },
      },
      {
        key: { label: 'Field' },
        value: [
          {
            label: 'Current value',
            class: 'summary-list-change-header-font-weight',
          },
          {
            label: 'Changed value',
            class: 'summary-list-change-header-font-weight',
          },
        ],
      },
      {
        key: { label: 'Report type' },
        value: [
          {
            label: !empty(this.sectionDetails?.reportType)
              ? reportTypeMap[this.sectionDetails?.reportType].label
              : '-',
          },
          {
            label: this.updatedDetails?.reportType
              ? reportTypeMap[this.updatedDetails.reportType].label
              : '',
            innerStyle:
              !empty(this.updatedDetails.reportType) &&
              this.sectionDetails.reportType !== this.updatedDetails.reportType
                ? 'summary-list-change-notification'
                : '',
          },
        ],
      },
      {
        key: { label: 'Title' },
        value: [
          {
            label: this.sectionDetails?.title,
          },
          {
            label:
              this.sectionDetails.title?.trim() !==
              this.updatedDetails.title?.trim()
                ? this.updatedDetails?.title
                : '',
            innerStyle:
              !empty(this.updatedDetails.title) &&
              this.sectionDetails.title?.trim() !==
                this.updatedDetails.title?.trim()
                ? 'summary-list-change-notification'
                : '',
          },
        ],
      },
      {
        key: { label: 'Summary' },
        value: [
          {
            label: this.sectionDetails?.summary,
          },
          {
            label:
              this.sectionDetails.summary?.trim() !==
              this.updatedDetails.summary?.trim()
                ? this.updatedDetails?.summary
                : '',
            innerStyle:
              !empty(this.updatedDetails.summary) &&
              this.sectionDetails.summary?.trim() !==
                this.updatedDetails.summary?.trim()
                ? 'summary-list-change-notification'
                : '',
          },
        ],
      },
      {
        key: { label: 'Display type' },
        value: [
          {
            label: !empty(this.sectionDetails?.displayType)
              ? DisplayTypeMap[this.sectionDetails?.displayType]
              : '-',
          },
          {
            label: !empty(this.updatedDetails?.displayType)
              ? DisplayTypeMap[this.updatedDetails?.displayType]
              : '',
            innerStyle:
              !empty(this.updatedDetails.displayType) &&
              this.sectionDetails.displayType !==
                this.updatedDetails.displayType
                ? 'summary-list-change-notification'
                : '',
          },
        ],
      },
    ];
  }

  get publicationDetailsItems(): SummaryListItem[] {
    const summaryListItems: SummaryListItem[] = [
      {
        key: { label: 'Report publication details', class: 'govuk-heading-m' },
        value: [
          {
            label: '',
          },
        ],
        action: {
          label: 'Change',
          visible: true,
          visuallyHidden: '',
          clickEvent: '/update-publication-details/update-scheduler-details',
        },
      },
      {
        key: { label: 'Field' },
        value: [
          {
            label: 'Current value',
            class: 'summary-list-change-header-font-weight',
          },
          {
            label: 'Changed value',
            class: 'summary-list-change-header-font-weight',
          },
        ],
      },
      {
        key: { label: 'Report publication frequency' },
        value: [
          {
            label: !empty(this.sectionDetails?.publicationFrequency)
              ? PublicationFrequencyMap[
                  this.sectionDetails?.publicationFrequency
                ]
              : '-',
          },
          {
            label:
              !empty(this.updatedDetails?.publicationFrequency) &&
              this.sectionDetails.publicationFrequency !==
                this.updatedDetails.publicationFrequency
                ? PublicationFrequencyMap[
                    this.updatedDetails?.publicationFrequency
                  ]
                : '',
            innerStyle:
              !empty(this.updatedDetails.publicationFrequency) &&
              this.sectionDetails.publicationFrequency !==
                this.updatedDetails.publicationFrequency
                ? 'summary-list-change-notification'
                : '',
          },
        ],
      },
      {
        key: { label: 'Recurrence' },
        value: [
          {
            label: empty(this.sectionDetails?.everyXDays)
              ? null
              : String(this.sectionDetails?.everyXDays) + ' day(s)',
          },
          {
            label:
              empty(this.updatedDetails.everyXDays) ||
              this.sectionDetails.everyXDays === this.updatedDetails.everyXDays
                ? null
                : String(this.updatedDetails.everyXDays) + ' day(s)',
            innerStyle:
              !empty(this.updatedDetails.everyXDays) &&
              this.sectionDetails.everyXDays !== this.updatedDetails.everyXDays
                ? 'summary-list-change-notification'
                : '',
          },
        ],
      },
      {
        key: { label: 'Publication start date' },
        value: [
          {
            label: this.gdsDatePipe.transform(
              this.sectionDetails?.publicationStart
            ),
          },
          {
            label:
              this.sectionDetails.publicationStart !==
              this.updatedDetails.publicationStart
                ? this.gdsDatePipe.transform(
                    this.updatedDetails?.publicationStart
                  )
                : '',
            innerStyle:
              !empty(this.updatedDetails.publicationStart) &&
              this.sectionDetails.publicationStart !==
                this.updatedDetails.publicationStart
                ? 'summary-list-change-notification'
                : '',
          },
        ],
      },
      {
        key: { label: 'Publication time' },
        value: [
          {
            label: this.constructTime(this.sectionDetails?.publicationTime),
          },
          {
            label:
              this.sectionDetails.publicationTime !==
              this.updatedDetails.publicationTime
                ? this.constructTime(this.updatedDetails?.publicationTime)
                : '',
            innerStyle:
              !empty(this.updatedDetails.publicationTime) &&
              this.sectionDetails.publicationTime !==
                this.updatedDetails.publicationTime
                ? 'summary-list-change-notification'
                : '',
          },
        ],
      },
    ];
    if (
      this.sectionsWithGenerationDate.includes(this.sectionDetails?.reportType)
    ) {
      summaryListItems.push({
        key: { label: 'Cut-off day and time' },
        value: [
          {
            label:
              this.constructDate(this.sectionDetails?.generationDate) +
              this.constructTime(this.sectionDetails?.generationTime),
          },
          {
            label:
              this.constructDate(this.sectionDetails.generationDate) +
                this.constructTime(this.sectionDetails.generationTime) !==
              this.constructDate(this.updatedDetails.generationDate) +
                this.constructTime(this.updatedDetails.generationTime)
                ? this.constructDate(this.updatedDetails?.generationDate) +
                  this.constructTime(this.updatedDetails?.generationTime)
                : '',
            innerStyle:
              !empty(this.updatedDetails.generationDate) &&
              !empty(this.updatedDetails.generationTime) &&
              this.constructDate(this.sectionDetails.generationDate) +
                this.constructTime(this.sectionDetails.generationTime) !==
                this.constructDate(this.updatedDetails.generationDate) +
                  this.constructTime(this.updatedDetails.generationTime)
                ? 'summary-list-change-notification'
                : '',
          },
        ],
      });
    }
    return summaryListItems;
  }

  constructDate(date: string) {
    if (empty(date)) {
      return '';
    }
    return this.gdsDateShortNoYear.transform(date) + ', ';
  }

  constructTime(time: string) {
    if (empty(time)) {
      return '';
    }
    return this.timeOptions.filter((timeOption) => timeOption.value === time)[0]
      ?.label;
  }

  navigateTo(value: string) {
    this.navigateToEmitter.emit(value);
  }

  submitChanges() {
    if (
      !Object.keys(this.updatedDetails).filter(
        (prop) =>
          String(this.updatedDetails[prop])?.trim() !==
          String(this.sectionDetails[prop])?.trim()
      ).length
    ) {
      this.errorDetails.emit(
        new ErrorDetail(null, 'You can not make a request without any changes.')
      );
    } else {
      this.submitRequest.emit();
    }
  }
}
