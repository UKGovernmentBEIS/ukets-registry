import { Pipe, PipeTransform } from '@angular/core';
import { REQUEST_TYPE_VALUES } from '@shared/task-and-regulator-notice-management/model';

@Pipe({
  name: 'taskTypeLabel',
})
export class TaskTypeLabelPipe implements PipeTransform {
  transform(key: string): any {
    return REQUEST_TYPE_VALUES[key]?.label;
  }
}
