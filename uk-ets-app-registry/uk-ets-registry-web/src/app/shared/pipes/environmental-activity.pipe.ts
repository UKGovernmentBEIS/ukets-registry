import { Pipe, PipeTransform } from '@angular/core';
import {
  EnvironmentalActivity,
  EnvironmentalActivityLabelMap
} from '@shared/model/transaction';

@Pipe({
  name: 'environmentalActivity'
})
export class EnvironmentalActivityPipe implements PipeTransform {
  transform(value: EnvironmentalActivity): string {
    return EnvironmentalActivityLabelMap.get(value);
  }
}
