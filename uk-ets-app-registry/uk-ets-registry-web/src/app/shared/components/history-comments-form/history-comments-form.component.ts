import { Component, computed, input, output } from '@angular/core';
import { TaskDetails } from '@shared/task-and-regulator-notice-management/model';
import { ReactiveFormsModule, UntypedFormBuilder } from '@angular/forms';
import { SharedModule } from '@registry-web/shared/shared.module';

@Component({
  selector: 'app-history-comments-form',
  templateUrl: './history-comments-form.component.html',
  standalone: true,
  imports: [SharedModule, ReactiveFormsModule],
})
export class HistoryCommentsFormComponent {
  readonly formModel = this.formBuilder.group({ comment: [] });

  readonly task = input.required<TaskDetails>();
  readonly addComment = output<{ comment: string; requestId: string }>();

  readonly isCompleted = computed<boolean>(
    () => this.task().taskStatus === 'COMPLETED'
  );

  constructor(private formBuilder: UntypedFormBuilder) {}

  submit() {
    const comment: string = this.formModel.value['comment'];
    this.addComment.emit({ comment, requestId: this.task().requestId });
    this.formModel.reset();
  }
}
