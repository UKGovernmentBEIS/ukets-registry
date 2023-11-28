import { Component, Input, OnInit } from '@angular/core';
import { ControlContainer, FormGroupDirective } from '@angular/forms';
import { UkProtoFormComponent } from '../../uk-proto-form.component';
import { Observable } from 'rxjs';
import { debounceTime, map, startWith } from 'rxjs/operators';
import { empty } from '@shared/shared.util';

@Component({
  selector: 'app-form-comment-area',
  templateUrl: './uk-proto-form-comment-area.component.html',
  viewProviders: [
    { provide: ControlContainer, useExisting: FormGroupDirective },
  ],
})
export class UkProtoFormCommentAreaComponent
  extends UkProtoFormComponent
  implements OnInit
{
  @Input() maxlength = 1024;
  @Input() rows = 5;
  @Input() srOnlyLabel = false; // if true, the label will be read by a screen reader but not by the user

  _infoId: string;
  remainingChars$: Observable<number>;

  ngOnInit(): void {
    // since ngOnInit is implemented in parent, it must be called here first.
    super.ngOnInit();
    this.remainingChars$ = this.formControl.valueChanges.pipe(
      startWith(''),
      debounceTime(500),
      map((val) => {
        if (!val) {
          return this.maxlength;
        }
        const remainingChars = this.maxlength - val.length;
        return remainingChars > 0 ? remainingChars : 0;
      })
    );
  }

  get infoId(): string {
    return `${this._id}-info`;
  }

  describedBy() {
    let describedBy = this.labelId;

    if (!empty(this.hint)) {
      describedBy = this.hintId;
    }

    return describedBy;
  }
}
