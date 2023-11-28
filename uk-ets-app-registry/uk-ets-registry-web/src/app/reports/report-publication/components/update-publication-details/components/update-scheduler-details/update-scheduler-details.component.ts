import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  Output,
} from '@angular/core';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { UntypedFormBuilder, ValidatorFn } from '@angular/forms';
import {
  MonthPropertiesMap,
  PublicationFrequency,
  PublicationFrequencyMap,
  Section,
} from '@report-publication/model';
import {
  empty,
  generateHoursOptions,
  getOptionsFromMap,
} from '@shared/shared.util';
import { takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { ReportType } from '@reports/model';

@Component({
  selector: 'app-update-scheduler-details',
  templateUrl: './update-scheduler-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UpdateSchedulerDetailsComponent
  extends UkFormComponent
  implements OnDestroy
{
  @Input() sectionDetails: Section;
  @Output() emitter = new EventEmitter<any>();

  sectionsWithGenerationDate: Array<ReportType> = [
    ReportType.R0017,
    ReportType.R0025,
    ReportType.R0028,
    ReportType.R0031,
  ];

  publicationFrequencyOptions = getOptionsFromMap(PublicationFrequencyMap);
  timeOptions = generateHoursOptions();
  isFrequencyDisabled: boolean;
  isReccurenceEnabled: boolean;
  private unsubscribe$: Subject<void> = new Subject();

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  ngOnInit() {
    this.isFrequencyDisabled =
      this.sectionDetails.publicationFrequency ===
      PublicationFrequency.DISABLED;
    this.isReccurenceEnabled =
      this.sectionDetails.publicationFrequency ===
      PublicationFrequency.EVERY_X_DAYS;
    super.ngOnInit();

    this.formGroup
      .get('publicationFrequency')
      .valueChanges.pipe(takeUntil(this.unsubscribe$))
      .subscribe((value) => {
        let controlsArray = [
          'recurrence',
          'publicationDate',
          'publicationTime',
          'generationDate',
          'generationTime',
        ];
        switch (value) {
          case PublicationFrequency.DISABLED:
            this.disableFormControls(controlsArray);
            break;
          case PublicationFrequency.EVERY_X_DAYS:
            this.enableFormControls(controlsArray);
            break;
          default:
            this.disableFormControls(['recurrence']);
        }
      });
  }

  protected doSubmit() {
    this.emitter.emit({
      publicationFrequency: this.formGroup.get('publicationFrequency')?.value,
      generationDate:
        !this.hasGenerationDateTime || this.isFrequencyDisabled
          ? null
          : this.constructGenerationDate(),
      generationTime:
        !this.hasGenerationDateTime || this.isFrequencyDisabled
          ? null
          : this.formGroup.get('generationTime')?.value,
      publicationStart: this.isFrequencyDisabled
        ? null
        : this.formGroup.get('publicationDate')?.value,
      publicationTime: this.isFrequencyDisabled
        ? null
        : this.formGroup.get('publicationTime')?.value,
      everyXDays:
        this.isFrequencyDisabled || !this.isReccurenceEnabled
          ? null
          : this.formGroup.get('recurrence')?.value,
    });
  }

  constructGenerationDate() {
    if (empty(this.formGroup.get('generationDate')?.value)) {
      return;
    }
    let generationDate = this.formGroup.get('generationDate')?.value;
    return (
      (generationDate.year ? generationDate.year : new Date().getFullYear()) +
      '-' +
      String(MonthPropertiesMap[generationDate.month].literal).padStart(
        2,
        '0'
      ) +
      '-' +
      String(generationDate.day).padStart(2, '0')
    );
  }

  protected getFormModel(): any {
    return {
      publicationFrequency: [
        this.sectionDetails.publicationFrequency,
        this.freqValidator(),
      ],
      recurrence: [this.sectionDetails.everyXDays, this.recurrenceValidator()],
      publicationDate: [
        this.sectionDetails.publicationStart,
        this.publicationDateValidator(),
      ],
      publicationTime: [
        this.sectionDetails.publicationTime,
        this.publicationTimeValidator(),
      ],
      generationDate: [''],
      generationTime: [
        this.sectionDetails.generationTime,
        this.generationTimeValidator(),
      ],
    };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      publicationFrequency: {
        required: 'Select a publication frequency',
      },
      publicationTime: {
        required: 'Select a publication time',
      },
      publicationDate: {
        pattern: 'The date you have entered is invalid.',
        ngbDate: ' ',
      },
      recurrence: {
        required: 'Select a recurrence',
      },
      generationTime: {
        required: 'Select a generation time',
      },
    };
  }

  freqValidator(): ValidatorFn {
    return () => {
      if (empty(this.formGroup)) {
        return null;
      }
      if (empty(this.formGroup.get('publicationFrequency').value)) {
        return {
          required: true,
        };
      }
      return null;
    };
  }

  recurrenceValidator(): ValidatorFn {
    return () => {
      if (empty(this.formGroup)) {
        return null;
      }
      if (this.isFrequencyDisabled) {
        return null;
      }
      if (
        this.formGroup.get('publicationFrequency').value ===
        PublicationFrequency.EVERY_X_DAYS
      ) {
        if (empty(this.formGroup.get('recurrence').value)) {
          return { required: true };
        }
      }
      return null;
    };
  }

  publicationDateValidator(): ValidatorFn {
    return () => {
      if (empty(this.formGroup)) {
        return null;
      }
      if (this.isFrequencyDisabled) {
        return null;
      }
      if (empty(this.formGroup.get('publicationDate').value)) {
        return {
          ngbDate: true,
        };
      }
      return null;
    };
  }

  publicationTimeValidator(): ValidatorFn {
    return () => {
      if (empty(this.formGroup)) {
        return null;
      }
      if (this.isFrequencyDisabled) {
        return null;
      }
      if (empty(this.formGroup.get('publicationTime').value)) {
        return {
          required: true,
        };
      }
      return null;
    };
  }

  generationTimeValidator(): ValidatorFn {
    return () => {
      if (empty(this.formGroup)) {
        return null;
      }
      if (this.isFrequencyDisabled) {
        return null;
      }
      if (
        empty(this.formGroup.get('generationTime').value) &&
        this.hasGenerationDateTime
      ) {
        return {
          required: true,
        };
      }
      return null;
    };
  }

  checkFrequencyValue(value: string) {
    this.isFrequencyDisabled =
      value.indexOf(PublicationFrequency.DISABLED) > -1;
    this.isReccurenceEnabled =
      value.indexOf(PublicationFrequency.EVERY_X_DAYS) > -1;
  }

  disableFormControls(controls: string[]) {
    controls.forEach((control) => {
      this.formGroup.get(control).disable();
    });
  }

  enableFormControls(controls: string[]) {
    controls.forEach((control) => {
      this.formGroup.get(control).enable();
    });
  }

  populateFormControl(event) {
    this.formGroup.get('generationDate').patchValue(event);
  }

  get hasGenerationDateTime() {
    return this.sectionsWithGenerationDate.includes(
      this.sectionDetails?.reportType
    );
  }

  noReportType() {
    return empty(this.sectionDetails?.reportType);
  }

  ngOnDestroy() {
    this.unsubscribe$.next();
    this.unsubscribe$.complete();
  }
}
