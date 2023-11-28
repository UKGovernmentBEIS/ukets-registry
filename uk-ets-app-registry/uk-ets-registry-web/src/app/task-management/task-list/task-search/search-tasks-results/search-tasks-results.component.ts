import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Location } from '@angular/common';
import {
  AccountType,
  SelectionChange,
  Task,
  taskStatusMap,
  requestStatusMap,
  TaskType,
  TaskOutcome,
} from '@task-management/model';
import { indexOfTask } from '@task-management/task-list/util/task.comparator';
import { getLabel } from '@shared/shared.util';
import { SortParameters } from '@shared/search/sort/SortParameters';
import { Router, RouterStateSnapshot } from '@angular/router';
import { SearchMode } from '@shared/resolvers/search.resolver';
import { Store } from '@ngrx/store';
import { MenuItemEnum } from '@account-management/account/account-details/model';
import { canGoBackToList } from '@shared/shared.action';

@Component({
  selector: 'app-search-tasks-results',
  styleUrls: ['./search-tasks-results.component.scss'],
  templateUrl: './search-tasks-results.component.html',
})
export class SearchTasksResultsComponent {
  @Input() results: Task[];
  @Input() selectedTasks: Task[];
  @Input() taskTypeOptionsAll: TaskType[];
  @Input() accountTypeOptions: AccountType[];
  @Input() sortParameters: SortParameters;

  @Output() readonly selectedTasksChanged = new EventEmitter<
    SelectionChange<Task>
  >();
  @Output() readonly sort = new EventEmitter<SortParameters>();
  @Output() readonly openDetail = new EventEmitter<string>();

  taskStatusMap = taskStatusMap;
  requestStatusMap = requestStatusMap;
  TaskOutcome = TaskOutcome;

  url: string;

  constructor(
    private router: Router,
    private location: Location,
    private store: Store
  ) {
    const snapshot: RouterStateSnapshot = router.routerState.snapshot;
    this.url =
      snapshot.url.indexOf('?') > -1 ? snapshot.url.split('?')[0] : null;
  }

  isAllSelected() {
    for (const task of this.results) {
      if (indexOfTask(this.selectedTasks, task) < 0) {
        return false;
      }
    }
    return true;
  }

  isIndeterminate() {
    if (this.isAllSelected()) {
      return false;
    }
    for (const task of this.results) {
      if (indexOfTask(this.selectedTasks, task) > -1) {
        return true;
      }
    }
    return false;
  }

  isSelected(task: Task) {
    return indexOfTask(this.selectedTasks, task) > -1;
  }

  masterToggle() {
    const selectionChangeEvent = this.isAllSelected()
      ? new SelectionChange<Task>([], this.results)
      : new SelectionChange<Task>(this.results, []);
    this.selectedTasksChanged.emit(selectionChangeEvent);
  }

  toggle(checked: boolean, task: Task) {
    const selectionChangeEvent = checked
      ? new SelectionChange<Task>([task], [])
      : new SelectionChange<Task>([], [task]);
    this.selectedTasksChanged.emit(selectionChangeEvent);
  }

  getTaskTypeLabel(key: string): string {
    return getLabel(key, this.taskTypeOptionsAll);
  }

  getAccountTypeLabel(key: string): string {
    return getLabel(key, this.accountTypeOptions);
  }

  navigateToTask(taskId: string, event): void {
    event.preventDefault();
    // We need to replace the url with the appropriate param mode when entering
    // a detailed page so to load the stored criteria when returning back to results by clicking on the browser's back button
    this.changeLocationState();
    if (this.url) {
      this.router.navigate([this.url], {
        queryParams: { mode: SearchMode.LOAD },
        replaceUrl: true,
        skipLocationChange: true,
      });
    }
    this.store.dispatch(
      canGoBackToList({
        goBackToListRoute: `/task-list`,
        extras: {
          skipLocationChange: false,
          queryParams: {
            mode: SearchMode.LOAD,
          },
        },
      })
    );
    this.openDetail.emit(taskId);
  }

  changeLocationState(): void {
    // We need to replace the url with the appropriate param mode when entering
    // a detailed page so to load the stored criteria when returning back to results by clicking on the browser's back button
    if (this.url) {
      this.location.go(this.url, `mode=${SearchMode.LOAD}`);
    }
  }

  goToAccountDetailsAndChangeLocationState(
    ukRegistryIdentifier: number,
    event
  ) {
    event.preventDefault();
    this.changeLocationState();
    this.store.dispatch(
      canGoBackToList({
        goBackToListRoute: `/task-list`,
        extras: {
          skipLocationChange: false,
          queryParams: {
            mode: SearchMode.LOAD,
          },
        },
      })
    );

    this.router.navigate(['/account', ukRegistryIdentifier], {
      queryParams: { selectedSideMenu: MenuItemEnum.OVERVIEW },
    });
  }

  goToUserDetailsAndChangeLocationState(
    authorizedRepresentativeUserId: string,
    event
  ) {
    event.preventDefault();
    this.changeLocationState();
    this.store.dispatch(
      canGoBackToList({
        goBackToListRoute: `/task-list`,
        extras: {
          skipLocationChange: false,
          queryParams: {
            mode: SearchMode.LOAD,
          },
        },
      })
    );

    this.router.navigate(['/user-details', authorizedRepresentativeUserId]);
  }

  hideLabelForAllocateAllowancesProposal(taskType: string, label: string) {
    return this.getTaskTypeLabel(taskType) === 'Allocate allowances proposal'
      ? null
      : label;
  }
}
