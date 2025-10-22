import { Component, EventEmitter, Input, Output } from '@angular/core';
import {
  Mode,
  REQUEST_TYPE_VALUES,
  requestStatusMap,
  RequestType,
  TaskDetails,
  TaskFileDownloadInfo,
  TaskOutcome,
  TaskType,
} from '@task-management/model';
import { BannerType } from '@shared/banner/banner-type.enum';
import { TaskListRoutes } from '@task-management/task-list/task-list.properties';
import { GoBackNavigationExtras } from '@shared/back-button';
import { NavigationExtras, RouterModule } from '@angular/router';
import { navigateTo } from '@registry-web/shared/shared.action';
import { Store } from '@ngrx/store';
import {
  navigateToChangeTaskDeadline,
  navigateToSelectPaymentMethod,
} from '@task-details/actions/task-details-navigation.actions';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@registry-web/shared/shared.module';

@Component({
  standalone: true,
  imports: [CommonModule, RouterModule, SharedModule],
  selector: 'app-task-header',
  templateUrl: './task-header.component.html',
  styleUrls: [
    './task-header.component.scss',
    '../../../../shared/sub-headers/styles/sub-header.scss',
  ],
})
export class TaskHeaderComponent {
  @Input()
  taskDetails: TaskDetails;

  @Input()
  showBackToList: boolean;

  @Input()
  taskHeaderActionsVisibility: boolean;

  @Input()
  showExportToPDF = false;

  @Input()
  taskTypeOptions: TaskType[];

  @Input()
  goBackToListRoute: string;

  @Input()
  goBackToListNavigationExtras: GoBackNavigationExtras;

  @Input()
  isAdmin: boolean;

  TaskOutcome = TaskOutcome;

  @Output() readonly userDecision = new EventEmitter<{
    taskOutcome: TaskOutcome;
    taskType: RequestType;
  }>();

  @Output() readonly handleExportPDF = new EventEmitter<TaskFileDownloadInfo>();

  bannerTypes = BannerType;

  moreInfo = false;

  taskListRoutes = TaskListRoutes;

  taskListModes = Mode;

  requestStatusMap = requestStatusMap;

  constructor(private store: Store) {}

  goBackToList(event) {
    event.preventDefault();
    const extras: NavigationExtras = {
      skipLocationChange: this.goBackToListNavigationExtras?.skipLocationChange,
      queryParams: this.goBackToListNavigationExtras?.queryParams,
    };
    this.store.dispatch(
      navigateTo({
        route: this.goBackToListRoute,
        extras,
        queryParams: this.goBackToListNavigationExtras?.queryParams,
      })
    );
  }

  getTaskTypeLabel(): string {
    return REQUEST_TYPE_VALUES[this.taskDetails.taskType]?.label;
  }

  // to be removed
  checkIfCanApprove(type: RequestType) {
    return (
      !this.checkIfCanOnlyComplete(type) &&
      this.checkIfCanApproveRejectPaymentRequest()
    );
  }

  // to be removed
  checkIfCanReject(type: RequestType) {
    return (
      !this.checkIfCanOnlyComplete(type) &&
      this.checkIfCanApproveRejectPaymentRequest()
    );
  }

  checkIfCanOnlyComplete(type: RequestType) {
    return REQUEST_TYPE_VALUES[this.taskDetails.taskType]?.completeOnly;
  }

  completeOnly() {
    return REQUEST_TYPE_VALUES[this.taskDetails.taskType]?.completeOnly;
  }

  checkIfCanApproveRejectPaymentRequest() {
    if ('PAYMENT_REQUEST' === this.taskDetails.taskType) {
      return this.isAdmin;
    }
    return true;
  }

  checkIfCanMakePayment() {
    if ('PAYMENT_REQUEST' === this.taskDetails.taskType) {
      return !this.isAdmin;
    }
    return false;
  }

  proceedWith(taskOutcome: TaskOutcome) {
    this.userDecision.emit({
      taskOutcome,
      taskType: this.taskDetails.taskType,
    });
  }

  exportPDF() {
    this.handleExportPDF.emit({
      taskType: this.taskDetails.taskType,
      taskRequestId: this.taskDetails.requestId,
    });
  }

  proceedWithPayment() {
    this.store.dispatch(navigateToSelectPaymentMethod());
  }

  changeDeadline() {
    this.store.dispatch(navigateToChangeTaskDeadline());
  }
}
