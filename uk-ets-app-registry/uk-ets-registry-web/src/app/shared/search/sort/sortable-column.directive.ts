import {
  Directive,
  Input,
  ElementRef,
  Renderer2,
  OnInit,
  HostListener,
  OnDestroy,
} from '@angular/core';
import { SortService } from './sort.service';
import { Subscription } from 'rxjs';
import { SortParameters } from './SortParameters';

const ASC = 'ASC';
const DESC = 'DESC';
@Directive({
  selector: '[appSortableColumn]',
})
export class SortableColumnDirective implements OnInit, OnDestroy {
  @Input() sortDisable: boolean;
  @Input() sortField: string;
  @Input() sortDirection = '';

  ariaSortMap = {
    ASC: 'ascending',
    DESC: 'descending',
  };

  subscription = new Subscription();

  constructor(
    private sortService: SortService,
    private elRef: ElementRef,
    private renderer: Renderer2
  ) {}

  @HostListener('click')
  sort() {
    if (this.sortDisable) return;
    this.sortService.sortByColumn({
      sortField: this.sortField,
      sortDirection: this.sortDirection === ASC ? DESC : ASC,
    });
  }

  ngOnInit() {
    const thElem: HTMLElement = this.elRef.nativeElement;
    this.renderer.setAttribute(thElem, 'scope', 'col');
    this.renderer.addClass(thElem, 'govuk-table__header');

    if (!this.sortDisable) {
      const buttonElem = document.createElement('BUTTON');
      buttonElem.setAttribute('aria-sort', this.getAriaSort());
      const headerLabel = document.createTextNode(thElem.innerText);
      buttonElem.appendChild(headerLabel);
      thElem.innerHTML = '';
      this.renderer.appendChild(thElem, buttonElem);
    }

    this.subscription = this.sortService
      .getColumnSorted()
      .subscribe((event: SortParameters) => {
        if (event.sortField === this.sortField) {
          this.sortDirection = event.sortDirection;
        } else {
          this.sortDirection = '';
        }
        this.renderer.setAttribute(
          this.elRef.nativeElement.children[0],
          'aria-sort',
          this.getAriaSort()
        );
      });
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  private getAriaSort(): string {
    return this.ariaSortMap[this.sortDirection]
      ? this.ariaSortMap[this.sortDirection]
      : this.sortDirection;
  }
}
