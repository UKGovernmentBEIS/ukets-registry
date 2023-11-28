import { Component, Input, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { ActivatedRoute, Router } from '@angular/router';
import { TaskDetailsActions } from '@task-details/actions';
import { TaskDetails } from '@task-management/model';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';

@Component({
  selector: 'app-history-comments',
  templateUrl: './history-comments.component.html',
  styles: [],
})
export class HistoryCommentsComponent implements OnInit {
  formModel: UntypedFormGroup;

  requestId: string;

  @Input()
  task: TaskDetails;
  isCompleted: boolean;

  constructor(
    private router: Router,
    route: ActivatedRoute,
    private store: Store,
    private formBuilder: UntypedFormBuilder
  ) {
    route.paramMap.subscribe((paramMap) => {
      this.requestId = paramMap.get('requestId');
    });
    this.formModel = this.formBuilder.group({
      comment: [],
    });
  }

  ngOnInit() {
    this.isCompleted = this.task.taskStatus !== 'COMPLETED';
    this.store.dispatch(
      TaskDetailsActions.fetchTaskHistory({ requestId: this.requestId })
    );
  }

  submit() {
    const comment: string = this.formModel.value['comment'];
    this.store.dispatch(
      TaskDetailsActions.taskHistoryAddComment({
        requestId: this.requestId,
        comment,
      })
    );
    this.formModel.reset();
  }
}
