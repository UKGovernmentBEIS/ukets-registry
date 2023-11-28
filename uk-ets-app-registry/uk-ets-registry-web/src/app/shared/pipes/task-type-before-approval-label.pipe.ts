import { Pipe, PipeTransform } from '@angular/core';
import { REQUEST_TYPE_VALUES, RequestType } from '@task-management/model';

@Pipe({
  name: 'taskTypeBeforeApprovalLabel'
})
export class TaskTypeBeforeApprovalLabelPipe implements PipeTransform {
  transform(value: RequestType): string {
    return REQUEST_TYPE_VALUES[value].confirmationText;
  }
}
