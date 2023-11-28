import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Store } from '@ngrx/store';
import { ActivatedRoute } from '@angular/router';
import { ErrorDetail } from 'src/app/shared/error-summary/error-detail';
import { claimTasks } from '../task-list.actions';
import { errors } from 'src/app/shared/shared.action';
import { ErrorSummary } from 'src/app/shared/error-summary/error-summary';
import { BulkActionForm } from '../util/bulk-action';
import * as fromAuth from '@registry-web/auth/auth.selector';
import { Observable } from 'rxjs';
import * as fromTaskList from '../task-list.selector';

@Component({
  selector: 'app-bulk-claim',
  templateUrl: './bulk-claim.component.html',
})
export class BulkClaimComponent extends BulkActionForm implements OnInit {
  isAuthorityUser$: Observable<boolean>;
  areTasksClaimed$: Observable<boolean>;

  constructor(
    protected route: ActivatedRoute,
    private store: Store,
    private formBuilder: UntypedFormBuilder
  ) {
    super(route);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.isAuthorityUser$ = this.store.select(fromAuth.isAuthorityUser);
    this.areTasksClaimed$ = this.store.select(
      fromTaskList.selectAreTasksClaimed(this.requestIds)
    );
    this.form = this.formBuilder.group({
      comment: [],
    });
  }

  validate(): boolean {
    const validAction: boolean = this.requestIds && this.requestIds.length > 0;
    if (!validAction) {
      this.store.dispatch(
        errors({
          errorSummary: new ErrorSummary([
            new ErrorDetail(
              null,
              'No task has been selected from task list to be claimed.'
            ),
          ]),
        })
      );
    }
    return validAction;
  }

  doSubmit() {
    const comment: string = this.form.value['comment'];
    console.log(comment);
    this.store.dispatch(
      claimTasks({
        requestIds: this.requestIds,
        comment,
        potentialErrors: this.potentialErrors,
      })
    );
  }
}
