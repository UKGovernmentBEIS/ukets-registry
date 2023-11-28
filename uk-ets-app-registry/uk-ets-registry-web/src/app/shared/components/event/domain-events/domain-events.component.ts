import { Component, EventEmitter, Input, Output } from '@angular/core';
import { DomainEvent } from '@shared/model/event';

@Component({
  selector: 'app-domain-events',
  templateUrl: './domain-events.component.html',
  styles: [],
})
export class DomainEventsComponent {
  @Input()
  domainEvents: DomainEvent[];

  @Input()
  isAdmin: boolean;

  @Output() navigate = new EventEmitter<any>();

  navigateTo(urid: string) {
    this.navigate.emit(urid);
  }
}
