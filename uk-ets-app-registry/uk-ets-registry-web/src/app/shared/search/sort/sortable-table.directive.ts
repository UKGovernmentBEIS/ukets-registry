import {
  Directive,
  Output,
  EventEmitter,
  OnInit,
  OnDestroy,
} from '@angular/core';
import { SortService } from './sort.service';
import { Subscription } from 'rxjs';
import { SortParameters } from './SortParameters';

@Directive({
  selector: '[appSortableTable]',
})
export class SortableTableDirective implements OnInit, OnDestroy {
  constructor(private sortService: SortService) {}

  @Output() readonly sorting = new EventEmitter<SortParameters>();

  private columnSortedSubscription = new Subscription();

  ngOnInit() {
    this.columnSortedSubscription = this.sortService
      .getSortParameters()
      .subscribe((event) => {
        this.sorting.emit(event);
      });
  }

  ngOnDestroy() {
    this.columnSortedSubscription.unsubscribe();
  }
}
