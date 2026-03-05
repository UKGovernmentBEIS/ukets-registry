import { Pipe, PipeTransform } from '@angular/core';
import {
  REQUEST_TYPE_VALUES,
  RequestType,
} from '@shared/task-and-regulator-notice-management/model';

@Pipe({
  name: 'taskTypeBeforeApprovalLabel',
})
export class TaskTypeBeforeApprovalLabelPipe implements PipeTransform {
  transform(value: RequestType): string {
    return REQUEST_TYPE_VALUES[value].confirmationText;
  }
}
