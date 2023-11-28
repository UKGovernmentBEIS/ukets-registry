import { Pipe, PipeTransform } from '@angular/core';
import {
  AllocationCategory,
  AllocationCategoryLabel,
} from '../model/allocation';

@Pipe({
  name: 'allocationCategory',
})
export class AllocationCategoryPipe implements PipeTransform {
  transform(value: AllocationCategory): string {
    return value ? AllocationCategoryLabel[value] : value;
  }
}
