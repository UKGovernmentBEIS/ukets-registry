import { Pipe, PipeTransform } from '@angular/core';
import { REQUEST_TYPE_VALUES } from '@task-management/model';

@Pipe({
  name: 'taskTypeLabel',
})
export class TaskTypeLabelPipe implements PipeTransform {
  transform(key: string): any {
    return REQUEST_TYPE_VALUES[key]?.label;
  }
}
