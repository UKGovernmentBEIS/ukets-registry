import {
  AfterViewInit,
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input,
  Output,
} from '@angular/core';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { DisplayTypeMap, Section } from '@report-publication/model';
import { ReportType, reportTypeMap, ReportTypeValue } from '@reports/model';
import { Option } from '@shared/form-controls/uk-select-input/uk-select.model';
import { getOptionsFromMap } from '@shared/shared.util';

@Component({
  selector: 'app-update-section-details',
  templateUrl: './update-section-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UpdateSectionDetailsComponent
  extends UkFormComponent
  implements AfterViewInit
{
  @Input() sectionDetails: Section;
  @Output() readonly emitter = new EventEmitter<Section>();

  displayTypeOptions = getOptionsFromMap(DisplayTypeMap);

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  ngAfterViewInit(): void {
    this.formGroup.get('summary').updateValueAndValidity();
  }

  protected doSubmit() {
    this.emitter.emit({
      ...this.sectionDetails,
      title: this.formGroup.get('title').value,
      summary: this.formGroup.get('summary').value,
    });
  }

  protected getFormModel(): any {
    return {
      reportType: [this.sectionDetails.reportType],
      title: [this.sectionDetails.title, Validators.required],
      summary: [this.sectionDetails.summary, { updateOn: 'change' }],
      displayType: [this.sectionDetails.displayType],
    };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      title: {
        required: 'Enter a title',
      },
    };
  }

  get reportTypeOptions(): Option[] {
    const options = Object.entries(reportTypeMap).map(
      ([value, label]: [ReportType, ReportTypeValue]) => ({
        label: label.label,
        value: value,
      })
    );
    options.unshift({ label: '-', value: null });
    return options;
  }
}
