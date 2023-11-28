import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { PageParameters, Pagination } from '@shared/search/paginator';
import { Option } from '@shared/form-controls/uk-select-input/uk-select.model';

@Component({
  selector: 'app-paginator',
  templateUrl: './paginator.component.html',
  styleUrls: ['./paginator.component.scss'],
})
export class PaginatorComponent implements OnInit {
  @Input() pageSizeOptions: Option[];

  @Output() readonly selectFirstPage = new EventEmitter<PageParameters>();
  @Output() readonly selectLastPage = new EventEmitter<PageParameters>();
  @Output() readonly selectNextPage = new EventEmitter<PageParameters>();
  @Output() readonly selectPreviousPage = new EventEmitter<PageParameters>();
  @Output() readonly changePageSize = new EventEmitter<PageParameters>();

  displayable: boolean;
  currentPage: number;
  totalResults: number;
  pageSize: number;
  previousPage: number;
  nextPage: number;
  firstPage = 1;
  lastPage: number;
  showingFromIndex: number;
  showingToIndex: number;
  dashBefore: boolean;
  dashAfter: boolean;
  selectedPageSize: number;

  @Input()
  set pagination(pagination: Pagination) {
    if (!pagination || !pagination.totalResults) {
      this.displayable = false;
      return;
    }
    this.currentPage = pagination.currentPage;
    this.totalResults = pagination.totalResults;
    this.pageSize = pagination.pageSize;

    this.lastPage = Math.ceil(this.totalResults / this.pageSize);
    this.nextPage =
      this.currentPage < this.lastPage - 1 ? this.currentPage + 1 : null;
    this.previousPage = this.currentPage > 2 ? this.currentPage - 1 : null;
    const offset = (this.currentPage - 1) * this.pageSize;
    this.showingFromIndex = offset + 1;
    this.showingToIndex = Math.min(this.totalResults, offset + this.pageSize);
    this.dashBefore = this.currentPage - this.firstPage > 2;
    this.dashAfter = this.lastPage - this.currentPage > 2;

    this.initSelectedPageSizeOption();

    this.displayable = true;
  }

  ngOnInit(): void {
    this.initSelectedPageSizeOption();
  }

  private initSelectedPageSizeOption() {
    if (this.pageSizeOptions) {
      const filteredOptions = this.pageSizeOptions.filter(
        (o) => o.value === this.pageSize
      );

      this.selectedPageSize = filteredOptions.length
        ? filteredOptions[0].value
        : null;
    }
  }

  goToFirstPage() {
    this.selectFirstPage.emit({
      page: 0,
      pageSize: this.pageSize,
    });
  }

  goToLastPage() {
    this.selectLastPage.emit({
      page: this.lastPage - 1,
      pageSize: this.pageSize,
    });
  }

  goToPreviousPage() {
    if (this.currentPage > this.firstPage) {
      this.selectPreviousPage.emit({
        page: this.currentPage - 2,
        pageSize: this.pageSize,
      });
    }
  }

  goToNextPage() {
    if (this.currentPage < this.lastPage) {
      this.selectNextPage.emit({
        page: this.currentPage,
        pageSize: this.pageSize,
      });
    }
  }

  onChangePageSize($event) {
    this.changePageSize.emit({
      page: 0,
      pageSize: $event,
    });
  }
}
