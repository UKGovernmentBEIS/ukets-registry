import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { UntypedFormBuilder, UntypedFormControl } from '@angular/forms';
import { Observable } from 'rxjs';
import { Store } from '@ngrx/store';
import * as fromAuth from '@registry-web/auth/auth.selector';

@Component({
  selector: 'app-transaction-comment-form',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-full">
        <form [formGroup]="formGroup">
          <app-form-comment-area
            controlName="comment"
            [label]="
              (isAuthorityUser$ | async)
                ? 'Enter comment'
                : 'Enter comment (optional)'
            "
            [hint]="commentAreaHint()"
          >
          </app-form-comment-area>
        </form>
      </div>
    </div>
  `,
})
export class TransactionCommentFormComponent
  extends UkFormComponent
  implements OnInit
{
  @Input()
  fullIdentifier: string;

  @Output() readonly comment = new EventEmitter<string>();

  isAuthorityUser$: Observable<boolean>;

  constructor(protected formBuilder: UntypedFormBuilder, private store: Store) {
    super();
  }

  ngOnInit() {
    super.ngOnInit();
    this.isAuthorityUser$ = this.store.select(fromAuth.isAuthorityUser);
    this.commentControl().valueChanges.subscribe(() => {
      this.comment.emit(this.commentControl().value);
    });
  }

  protected getFormModel() {
    return {
      comment: ['', { updateOn: 'change' }],
    };
  }

  protected getValidationMessages(): {
    [key: string]: { [key: string]: string };
  } {
    return {};
  }

  commentControl(): UntypedFormControl {
    return this.formGroup.get('comment') as UntypedFormControl;
  }

  protected doSubmit() {
    this.comment.emit(this.commentControl().value);
  }

  commentAreaHint() {
    if (
      this.fullIdentifier?.startsWith('GB') ||
      this.fullIdentifier?.startsWith('UK')
    ) {
      return 'Visible to authorised representatives of both acquiring and transferring accounts';
    } else {
      return 'Visible to authorised representatives of transferring account';
    }
  }
}
