import { Pipe, PipeTransform } from '@angular/core';
import { EVENT_TYPE_LABELS, EventType } from '@shared/user';

@Pipe({
  name: 'eventType'
})
export class EventTypePipe implements PipeTransform {
  transform(value: EventType): string {
    return EVENT_TYPE_LABELS[value].label;
  }
}
