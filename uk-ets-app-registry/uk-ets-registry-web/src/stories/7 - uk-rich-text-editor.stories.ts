import {
  FormBuilder,
  FormControl,
  FormGroup,
  FormGroupDirective,
  NgControl,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { moduleMetadata } from '@storybook/angular';
import { UkRichTextEditorComponent } from '@shared/form-controls/uk-rich-text-editor/uk-rich-text-editor.component';
import { QuillModule } from 'ngx-quill';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { action } from '@storybook/addon-actions';

@Component({
  selector: 'app-wrapper',
  template: `
    <form [formGroup]="formGroup" (ngSubmit)="onContinue()">
      <app-uk-rich-text-editor
        label="Content"
        [controlName]="'richText'"
        validationErrorMessage="This field is required."
      ></app-uk-rich-text-editor>
      <button
        class="govuk-button"
        data-module="govuk-button"
        type="submit"
        id="continue"
      >
        Continue
      </button>
    </form>
    <pre>fg value: {{ formGroup.value | json }}</pre>
    <pre>fg status:  {{ formGroup.status | json }}</pre>
    <pre>control touched: {{ formGroup.touched | json }}</pre>
    <pre>control errors: {{ formGroup.get('richText')?.errors | json }}</pre>
  `,
})
export class WrapperComponent extends UkFormComponent implements OnInit {
  @Input() validators = [Validators.required];
  @Input() initialValue = '';

  @Output() continue = new EventEmitter<any>();

  constructor(protected formBuilder: FormBuilder) {
    super();
  }

  ngOnInit() {
    super.ngOnInit();
  }

  protected doSubmit() {}

  protected getFormModel(): any {
    return {
      richText: [this.initialValue, this.validators],
    };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      richText: {
        required: 'This field is required.',
      },
    };
  }

  onContinue() {
    this.continue.emit(this.formGroup.value);
    this.onSubmit();
  }
}

export default {
  title: 'UkRichTextEditorComponent',
  component: UkRichTextEditorComponent,
  decorators: [
    moduleMetadata({
      declarations: [UkRichTextEditorComponent, WrapperComponent],
      imports: [ReactiveFormsModule, QuillModule],
      providers: [FormGroupDirective, NgControl],
    }),
  ],
};

const formGroup = new FormGroup(
  {
    richText: new FormControl('', Validators.required),
  },
  { updateOn: 'submit' }
);

const formGroupNotRequired = new FormGroup(
  {
    richText: new FormControl(''),
  },
  { updateOn: 'submit' }
);

export const Default = () => ({
  // language=Angular2Html
  template: `
    <app-wrapper (continue)="onContinue($event)"></app-wrapper>`,
  props: {
    onContinue: action('Continue button clicked'),
    validators: [Validators.required],
  },
});

export const NotRequiredField = () => ({
  // language=Angular2Html
  template: `
    <app-wrapper [validators]="validators" (continue)="onContinue($event)"></app-wrapper>`,
  props: {
    onContinue: action('Continue button clicked'),
    validators: [],
  },
});

export const WithInitialValue = () => ({
  // language=Angular2Html
  template: `
    <app-wrapper [initialValue]="init" (continue)="onContinue($event)"></app-wrapper>`,
  props: {
    onContinue: action('Continue button clicked'),
    validators: [],
    init:
      '<p><strong>bold</strong></p><p><br></p><p><a href=\\"www.google.com\\" rel=\\"noopener noreferrer\\" target=\\"_blank\\">link</a></p>',
  },
});
