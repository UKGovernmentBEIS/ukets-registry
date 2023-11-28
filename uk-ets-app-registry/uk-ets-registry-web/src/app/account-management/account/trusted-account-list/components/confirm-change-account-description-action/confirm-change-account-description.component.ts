import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { DescriptionUpdateActionState } from '@account-management/account/trusted-account-list/reducers/trusted-account-list.reducer';
import { SummaryListItem } from '@shared/summary-list/summary-list.info';

@Component({
  selector: 'app-confirm-change-description',
  templateUrl: './confirm-change-account-description.component.html',
})
export class ConfirmChangeAccountDescriptionComponent
  extends UkFormComponent
  implements OnInit
{
  @Input() accountIdentifier: string;
  @Input() accountFullIdentifier: string;
  @Input() descriptionUpdateAction: DescriptionUpdateActionState;
  @Output()
  readonly description = new EventEmitter<DescriptionUpdateActionState>();
  @Output() readonly cancelDescriptionUpdateAction = new EventEmitter();
  @Output() readonly navigateToEmitter = new EventEmitter<string>();

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  ngOnInit(): void {
    super.ngOnInit();
  }

  protected getFormModel(): any {
    return {
      description: ['', { updateOn: 'change' }],
    };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      description: {},
    };
  }

  doSubmit() {
    this.description.emit({
      description: this.descriptionUpdateAction?.description,
      accountFullIdentifier: this.accountFullIdentifier,
    });
  }

  onContinue() {
    this.onSubmit();
  }

  onCancel() {
    this.cancelDescriptionUpdateAction.emit();
  }

  getSummaryListItems(): SummaryListItem[] {
    return [
      {
        key: { label: 'Account ID' },
        value: [
          {
            label: this.accountFullIdentifier,
            class:
              'govuk-summary-list__value govuk-summary-list__value_change_description',
          },
        ],
      },
      {
        key: { label: 'Description' },
        value: [
          {
            label: this.descriptionUpdateAction?.description,
            class:
              'govuk-summary-list__value govuk-summary-list__value_change_description',
          },
        ],
        action: {
          label: 'Change',
          visible: true,
          visuallyHidden: '',
          url: '',
          clickEvent: 'trusted-account-list/change-description',
        },
      },
    ];
  }

  navigateTo(value: string) {
    this.navigateToEmitter.emit(value);
  }
}
