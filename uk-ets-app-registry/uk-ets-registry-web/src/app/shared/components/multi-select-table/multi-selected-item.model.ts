import { GovukWidthClass } from '@registry-web/shared/govuk-components';

export interface TableColumn<T = any> {
  header: string;
  field: keyof T;
  isSortable?: boolean;
  isHeader?: boolean;
  isNumeric?: boolean;
  widthClass?: GovukWidthClass | string;
}

export interface SortEvent {
  column: TableColumn['field'];
  direction: 'ascending' | 'descending';
}

export type MultiSelectedItem<T> = T & {
  isSelected?: boolean;
};
