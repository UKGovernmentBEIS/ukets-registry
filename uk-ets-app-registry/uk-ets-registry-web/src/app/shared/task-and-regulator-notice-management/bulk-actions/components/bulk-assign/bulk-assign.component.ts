import { Component, computed, inject } from '@angular/core';
import { HttpParams } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';
import { NgbHighlight } from '@ng-bootstrap/ng-bootstrap';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';
import { SharedModule } from '@registry-web/shared/shared.module';
import { BulkActionBaseDirective } from '@shared/task-and-regulator-notice-management/bulk-actions/components/bulk-action-base.directive';
import { BulkActions } from '@shared/task-and-regulator-notice-management/bulk-actions/store';

interface UserDTO {
  fullName: string;
  knownAs: string;
  displayName: string;
  urid: string;
}

@Component({
  selector: 'app-bulk-assign',
  templateUrl: './bulk-assign.component.html',
  standalone: true,
  imports: [SharedModule, NgbHighlight, ReactiveFormsModule],
})
export class BulkAssignComponent extends BulkActionBaseDirective {
  private readonly ukEtsRegistryApiBaseUrl = inject(
    UK_ETS_REGISTRY_API_BASE_URL
  );

  readonly searchByNameRequestUrl =
    this.ukEtsRegistryApiBaseUrl + '/tasks.get.candidate-assignees';

  readonly accountIdentifiers = computed<string[]>(() =>
    Array.from(
      new Set(
        this.selectedTasks()
          .filter((task) => task.accountNumber)
          .map((task) => task.accountNumber)
      )
    )
  );
  readonly taskTypes = computed<string[]>(() =>
    this.selectedTasks()
      .filter((task) => task.taskType)
      .map((task) => task.taskType)
  );
  readonly requestParams = computed<HttpParams>(() => {
    let requestParams = new HttpParams().set(
      'requestIds',
      this.requestIds().join(',')
    );

    if (this.accountIdentifiers().length) {
      requestParams = requestParams.set(
        'accountIdentifiers',
        this.accountIdentifiers().join(',')
      );
    }
    if (this.taskTypes().length) {
      requestParams = requestParams.set(
        'taskTypes',
        this.taskTypes().join(',')
      );
    }

    return requestParams;
  });

  selectedUrid: string;

  doSubmit() {
    this.store.dispatch(
      BulkActions.BULK_ASSIGN({
        requestIds: this.requestIds(),
        comment: this.form.value['comment'],
        urid: this.selectedUrid,
        potentialErrors: this.potentialErrors,
      })
    );
  }

  validate(): boolean {
    this.validateSelectedRequestItems();

    if (this.errorDetails.length) {
      return false;
    }

    this.validateSelectedUser();
    this.validateComment();

    return this.errorDetails.length === 0;
  }

  userResultFormatter(item: UserDTO): string {
    return item.displayName;
  }

  onSelectFromSearch(event: UserDTO) {
    this.selectedUrid = event.urid;
  }

  private validateSelectedUser() {
    if (!this.selectedUrid) {
      this.errorDetails.push({
        componentId: 'user-label',
        errorMessage: 'Select a user',
      });
    }
  }

  private validateComment() {
    if (!this.form.value['comment']) {
      this.errorDetails.push({
        componentId: 'comment-label',
        errorMessage: 'Enter an explanation',
      });
    }
  }
}
