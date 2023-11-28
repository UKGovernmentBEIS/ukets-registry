import { Component, EventEmitter, Input, Output } from '@angular/core';
import { KeyValue } from '@angular/common';
import { SummaryListItem } from '@shared/summary-list/summary-list.info';
import { v4 as uuidv4 } from 'uuid';

@Component({
  selector: 'app-summary-list',
  styleUrls: ['summary-list.component.scss'],
  templateUrl: './summary-list.component.html',
})
export class SummaryListComponent {
  @Input()
  title: string;

  @Input()
  summaryListItems: SummaryListItem[];

  @Input()
  class: string;

  @Input()
  lines: { [key: string]: string };

  @Input()
  links: { [key: string]: string };

  @Input()
  skipLocationChange = true;

  @Output() readonly navigateToEmitter = new EventEmitter<string>();

  privateKey = uuidv4();
  originalOrder = (
    a: KeyValue<string, string>,
    b: KeyValue<string, string>
  ): number => {
    return 0;
  };

  hasLink(key: string) {
    return !!this.links && !!this.links[key];
  }

  getLink(key: string) {
    return this.links[key];
  }

  navigateTo(value: string) {
    this.navigateToEmitter.emit(value);
  }

  isArray(value) {
    return Array.isArray(value);
  }

  keytoId(key: string): string {
    if (key.length > 0) {
      const id = key.replace(/ +/g, '_').toLowerCase();
      return id;
    }
  }
}
