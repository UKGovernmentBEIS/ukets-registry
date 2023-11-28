import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';

@Component({
  selector: 'app-message-form',
  templateUrl: './message-form.component.html',
  styles: [],
})
export class MessageFormComponent extends UkFormComponent implements OnInit {
  @Output() readonly sendMessageRequest = new EventEmitter<{
    content: string;
  }>();

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  ngOnInit(): void {
    super.ngOnInit();
  }

  protected getFormModel(): any {
    return {
      content: ['', { validators: [Validators.required], updateOn: 'change' }],
    };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      content: {
        required: 'Message content is required.',
      },
    };
  }

  doSubmit() {
    this.sendMessageRequest.emit({
      content: this.formGroup.get('content').value,
    });
  }
}
