import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { DocumentsRequestType } from '@shared/model/request-documents/documents-request-type';

@Component({
  selector: 'app-user-document-assigning-comment',
  templateUrl: './user-document-assigning-comment.component.html',
})
export class UserDocumentAssigningCommentComponent
  extends UkFormComponent
  implements OnInit
{
  @Input()
  recipientName: string;
  @Input()
  documentRequestType: DocumentsRequestType;
  @Input()
  comment: string;
  @Output() readonly setComment = new EventEmitter<string>();

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  ngOnInit() {
    super.ngOnInit();
  }

  getTitleText(): string {
    return this.documentRequestType === DocumentsRequestType.ACCOUNT_HOLDER
      ? 'Request account holder documents'
      : 'Request user documents';
  }

  protected doSubmit() {
    this.setComment.emit(this.formGroup.get('comment').value);
  }

  protected getFormModel(): any {
    return { comment: [this.comment, Validators.required] };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      comment: {
        required: 'Please enter a comment.',
      },
    };
  }
}
