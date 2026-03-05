import { Component, computed } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from '@shared/shared.module';
import { BulkActionBaseDirective } from '@shared/task-and-regulator-notice-management/bulk-actions/components/bulk-action-base.directive';
import { BulkActions } from '@shared/task-and-regulator-notice-management/bulk-actions/store';

@Component({
  selector: 'app-bulk-claim',
  templateUrl: './bulk-claim.component.html',
  standalone: true,
  imports: [SharedModule, ReactiveFormsModule],
})
export class BulkClaimComponent extends BulkActionBaseDirective {
  readonly someRequestsClaimed = computed<boolean>(() =>
    this.selectedTasks().some((task) => task.taskStatus === 'CLAIMED')
  );

  validate(): boolean {
    this.validateSelectedRequestItems();

    return this.errorDetails.length === 0;
  }

  doSubmit() {
    this.store.dispatch(
      BulkActions.BULK_CLAIM({
        requestIds: this.requestIds(),
        comment: this.form.value['comment'],
        potentialErrors: this.potentialErrors,
      })
    );
  }
}
