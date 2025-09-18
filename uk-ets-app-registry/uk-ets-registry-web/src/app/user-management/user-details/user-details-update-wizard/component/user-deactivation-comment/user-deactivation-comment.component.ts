import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input,
  Output,
} from '@angular/core';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { empty } from '@shared/shared.util';
import { KeycloakUser } from '@shared/user';
import { Store } from '@ngrx/store';
import { selectLoggedInUser } from '@registry-web/auth/auth.selector';
import { takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';

@Component({
  selector: 'app-user-deactivation-comment',
  templateUrl: './user-deactivation-comment.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UserDeactivationCommentComponent extends UkFormComponent {
  @Input()
  _fullName: KeycloakUser;
  @Input()
  comment: string;
  @Output()
  readonly justificationComment = new EventEmitter<string>();

  isSignedInUser: boolean;

  private readonly unsubscribe$: Subject<void> = new Subject();

  constructor(protected formBuilder: UntypedFormBuilder, private store: Store) {
    super();
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.checkIfSignedInUser();
  }

  protected getFormModel(): any {
    return {
      justification: [
        this.comment,
        { validators: Validators.required, updateOn: 'change' },
      ],
    };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      justification: {
        required: 'Enter a reason for deactivating this user',
      },
    };
  }

  get fullName() {
    return empty(this._fullName) || this.isSignedInUser
      ? ''
      : this._fullName.attributes.alsoKnownAs?.length > 0 &&
        this._fullName.attributes.alsoKnownAs[0]?.length > 0
      ? 'Name ' + this._fullName.attributes.alsoKnownAs[0]
      : 'Name ' + this._fullName.firstName + ' ' + this._fullName.lastName;
  }

  protected doSubmit() {
    this.justificationComment.emit(this.formGroup.value['justification']);
  }

  checkIfSignedInUser() {
    this.store
      .select(selectLoggedInUser)
      .pipe(takeUntil(this.unsubscribe$))
      .subscribe((authModel) => {
        this.isSignedInUser =
          authModel.urid === this._fullName.attributes['urid'][0];
      });
  }

  ngOnDestroy(): void {
    this.unsubscribe$.next();
    this.unsubscribe$.complete();
  }
}
