import { Component, EventEmitter, Input, Output } from '@angular/core';
import { RouterLink } from '@angular/router';
import { SortParameters } from '@shared/search/sort/SortParameters';
import { SharedModule } from '@registry-web/shared/shared.module';
import { REGULATOR_NOTICE_DETAILS_PATH } from '@regulator-notice-management/details';
import { RegulatorNoticeTask } from '@shared/task-and-regulator-notice-management/model';
import {
  SelectionChange,
  taskStatusMap,
} from '@shared/task-and-regulator-notice-management/model';
import { indexOfItem } from '@shared/task-and-regulator-notice-management/util';

@Component({
  selector: 'app-regulator-notice-list-results',
  templateUrl: './regulator-notice-list-results.component.html',
  styleUrl: './regulator-notice-list-results.component.scss',
  standalone: true,
  imports: [SharedModule, RouterLink],
})
export class RegulatorNoticeListResultsComponent {
  @Input() results: RegulatorNoticeTask[];
  @Input() selectedTasks: RegulatorNoticeTask[];
  @Input() sortParameters: SortParameters;

  @Output() readonly selectedTasksChanged = new EventEmitter<
    SelectionChange<RegulatorNoticeTask>
  >();
  @Output() readonly sort = new EventEmitter<SortParameters>();

  readonly detailsPath = REGULATOR_NOTICE_DETAILS_PATH;
  readonly taskStatusMap = taskStatusMap;

  isAllSelected(): boolean {
    for (const task of this.results) {
      if (indexOfItem(this.selectedTasks, task) < 0) {
        return false;
      }
    }
    return true;
  }

  isIndeterminate(): boolean {
    if (this.isAllSelected()) {
      return false;
    }
    for (const task of this.results) {
      if (indexOfItem(this.selectedTasks, task) > -1) {
        return true;
      }
    }
    return false;
  }

  isSelected(task: RegulatorNoticeTask): boolean {
    return indexOfItem(this.selectedTasks, task) > -1;
  }

  masterToggle(): void {
    const selectionChangeEvent = this.isAllSelected()
      ? new SelectionChange<RegulatorNoticeTask>([], this.results)
      : new SelectionChange<RegulatorNoticeTask>(this.results, []);
    this.selectedTasksChanged.emit(selectionChangeEvent);
  }

  toggle(checked: boolean, task: RegulatorNoticeTask): void {
    const selectionChangeEvent = checked
      ? new SelectionChange<RegulatorNoticeTask>([task], [])
      : new SelectionChange<RegulatorNoticeTask>([], [task]);
    this.selectedTasksChanged.emit(selectionChangeEvent);
  }
}
