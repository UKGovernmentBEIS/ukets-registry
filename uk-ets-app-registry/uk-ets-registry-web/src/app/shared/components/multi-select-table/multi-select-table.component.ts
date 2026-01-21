import { NgTemplateOutlet } from '@angular/common';
import {
  ChangeDetectionStrategy,
  Component,
  computed,
  contentChild,
  input,
  output,
  TemplateRef,
} from '@angular/core';

import {
  SortEvent,
  TableColumn,
} from '@shared/components/multi-select-table/multi-selected-item.model';

type TableRow<T> = Pick<T, TableColumn<T>['field']>;

@Component({
  selector: 'app-multi-select-table',
  standalone: true,
  imports: [NgTemplateOutlet],
  templateUrl: './multi-select-table.component.html',
  styleUrl: './multi-select-table.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MultiSelectTableComponent<T extends { isSelected?: boolean }> {
  readonly caption = input<string>();
  readonly description = input<string>();
  readonly emptyTableText = input<string>();
  readonly columns = input<TableColumn<Omit<T, 'isSelected'>>[]>();
  readonly data = input<TableRow<T>[]>();

  readonly sort = output<SortEvent>();
  readonly selectionChanged = output<TableRow<T>[]>();

  readonly template = contentChild<
    TemplateRef<{
      column: TableColumn<T>;
      row: TableRow<T>;
      index: number;
    }>
  >(TemplateRef);

  sortedField: TableColumn<T>['field'];
  sortedColumn: TableColumn<T>['header'];
  sortingDirection: 'ascending' | 'descending';

  readonly isAllSelected = computed<boolean>(() =>
    this.data().every((item) => item?.isSelected)
  );

  /**
   * Checks whether the status of master checkbox is in indeterminate state (checkboxes partially checked)
   */
  readonly isIndeterminate = computed<boolean>(() => {
    const selectedCount = this.data().filter(
      (item) => item?.isSelected
    )?.length;
    return selectedCount > 0 && selectedCount !== this.data().length;
  });

  /**
   * If all are selected - unselects everything, otherwise selects everything
   */
  masterToggle() {
    const isAllSelected = this.isAllSelected();
    this.selectionChanged.emit(
      this.data().map((item) => ({ ...item, isSelected: !isAllSelected }))
    );
  }

  /**
   * Toggles a checkbox
   */
  toggle(rowIndex: number) {
    this.selectionChanged.emit(
      this.data().map((item, index) =>
        rowIndex === index ? { ...item, isSelected: !item?.isSelected } : item
      )
    );
  }

  sortBy(columnField: TableColumn<T>['field']): void {
    this.sortingDirection =
      this.sortedField === columnField && this.sortingDirection === 'ascending'
        ? 'descending'
        : 'ascending';
    this.sortedField = columnField;
    this.sortedColumn = this.columns().find(
      (column) => column.field === columnField
    ).header;

    this.sort.emit({
      column: this.sortedField,
      direction: this.sortingDirection,
    });
  }
}
