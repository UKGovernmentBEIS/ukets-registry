import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import { FormsModule } from '@angular/forms';
import { Store } from '@ngrx/store';
import { By } from '@angular/platform-browser';
import { Option } from '@shared/form-controls/uk-select-input/uk-select.model';
import { PageParameters } from '@shared/search/paginator/paginator.model';
import { DebugElement } from '@angular/core';
import { PaginatorComponent } from './paginator.component';
import { RouterTestingModule } from '@angular/router/testing';

describe('PaginatorComponent', () => {
  const pageSizeOptions: Option[] = [
    { label: '10', value: 10 },
    { label: '30', value: 30 },
  ];

  function initializePaginator(
    currentPage: number,
    totalResults: number,
    pageSize: number
  ): ComponentFixture<PaginatorComponent> {
    const fixture = TestBed.createComponent(PaginatorComponent);
    const storedPagination = {
      currentPage,
      totalResults,
      pageSize,
    };
    const component = fixture.componentInstance;
    component.pageSizeOptions = pageSizeOptions;
    component.pagination = storedPagination;
    fixture.detectChanges();
    return fixture;
  }

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        providers: [provideMockStore()],
        declarations: [PaginatorComponent],
        imports: [FormsModule, RouterTestingModule],
      }).compileComponents();
    })
  );

  test('the component is created', () => {
    const fixture = TestBed.createComponent(PaginatorComponent);
    const component = fixture.componentInstance;
    expect(component).toBeTruthy();
  });

  test('The paginator should not be rendered when the Pagination object returned from the store selector is null', () => {
    const fixture = TestBed.createComponent(PaginatorComponent);
    const mockStore = TestBed.inject(Store) as MockStore<any>;
    const component = fixture.componentInstance;

    fixture.detectChanges();
    const paginator = fixture.debugElement.query(By.css('nav'));

    expect(paginator).toBeFalsy();
  });

  test('The paginator should not be rendered when the totalResults are zero', () => {
    const fixture = TestBed.createComponent(PaginatorComponent);
    const mockStore = TestBed.inject(Store) as MockStore<any>;

    const component = fixture.componentInstance;

    component.pagination = {
      currentPage: 1,
      totalResults: 0, // total results are zero and we expect to not render the paginator
      pageSize: 10,
    };

    fixture.detectChanges();
    const paginator = fixture.debugElement.query(By.css('nav'));

    expect(paginator).toBeFalsy();
  });

  test('Paginator displays the current/selected/active page index as it comes from the store', () => {
    const activePage = 2;
    const fixture = initializePaginator(activePage, 199, 10);
    fixture.detectChanges();

    const activePageIndexLink = fixture.debugElement.query(
      By.css('li.hmcts-pagination__item--active')
    );
    expect(activePageIndexLink).toBeTruthy();
    const activePageIndex = +activePageIndexLink.nativeElement.textContent.trim();
    expect(activePageIndex === activePage).toBeTruthy();
  });

  test('Paginator displays the total results number as it comes from the store', () => {
    const totalResults = 199;
    const fixture = initializePaginator(2, totalResults, 10);

    fixture.detectChanges();
    const totalResultsLabel = fixture.debugElement.query(
      By.css('b.totalResults')
    );
    expect(
      +totalResultsLabel.nativeElement.textContent.trim() === totalResults
    ).toBeTruthy();
  });

  test('Paginator displays the page size as it comes from the store', () => {
    const pageSize = 20;
    const fixture = initializePaginator(1, 199, pageSize);

    fixture.detectChanges();
    const showingToIndexLabel = fixture.debugElement.query(
      By.css('b.showingToIndex')
    );
    const showingToIndex = +showingToIndexLabel.nativeElement.textContent.trim();
    expect(showingToIndex === pageSize).toBeTruthy();
  });

  test(`Paginator displays a link to exactly previous page if the current page is the third one or after the third page`, () => {
    const fixture = initializePaginator(3, 199, 10);
    fixture.detectChanges();
    const previousPageLink = fixture.debugElement.query(
      By.css('a.previousPage')
    );
    expect(previousPageLink).toBeTruthy();
    expect(previousPageLink.nativeElement.textContent.trim() === '2');
  });

  test(`Paginator does not display the previous page index link if the current page is not the third or after the third page`, () => {
    const fixture = initializePaginator(2, 199, 10);
    fixture.detectChanges();

    const previousPageLink = fixture.debugElement.query(
      By.css('a.previousPage')
    );
    expect(previousPageLink).toBeFalsy();
  });

  test(`Paginator displays a link to the exactly next page of current page if the current page is not next to last page`, () => {
    const fixture = initializePaginator(6, 199, 10);
    fixture.detectChanges();
    const nextPageLink = fixture.debugElement.query(By.css('a.nextPage'));
    expect(nextPageLink).toBeTruthy();
    expect(nextPageLink.nativeElement.textContent.trim()).toBe('7');
  });

  test(`Paginator should not display a link to the exactly next page of current page if the current page is the next to last page`, () => {
    const fixture = initializePaginator(19, 199, 10);
    fixture.detectChanges();

    const nextPageLink = fixture.debugElement.query(By.css('a.nextPage'));
    expect(nextPageLink).toBeFalsy();
  });

  test(`Paginator does not display the first page link if the current page is the first page`, () => {
    const fixture = initializePaginator(1, 199, 10);
    fixture.detectChanges();

    const firstPageLink = fixture.debugElement.query(By.css('a.goToFirstPage'));
    expect(firstPageLink).toBeFalsy();
  });

  test(`Paginator displays the first page link if the current page is not the first page`, () => {
    const fixture = initializePaginator(2, 199, 10);
    fixture.detectChanges();

    const firstPageLink = fixture.debugElement.query(By.css('a.goToFirstPage'));
    expect(firstPageLink).toBeTruthy();
    expect(firstPageLink.nativeElement.textContent.trim() === '1');
  });

  test(`Paginator does not display the last page link if the current page is the last page`, () => {
    const fixture = initializePaginator(20, 199, 10);
    fixture.detectChanges();

    const lastPageLink = fixture.debugElement.query(By.css('a.goToLastPage'));
    expect(lastPageLink).toBeFalsy();
  });

  test(`Paginator displays the last page link if the current page is not the last page`, () => {
    const fixture = initializePaginator(5, 199, 10);
    fixture.detectChanges();

    const lastPageLink = fixture.debugElement.query(By.css('a.goToLastPage'));
    expect(lastPageLink).toBeTruthy();
    expect(lastPageLink.nativeElement.textContent.trim() === '20');
  });

  test('Paginator always displays the "next" and "previous" icon buttons', () => {
    const fixture = initializePaginator(5, 199, 10);
    fixture.detectChanges();
    expect(fixture.debugElement.query(By.css('a.goToNextPage'))).toBeTruthy();
    expect(
      fixture.debugElement.query(By.css('a.goToPreviousPage'))
    ).toBeTruthy();
  });

  test(`The next page (>>) link emits an event
    which contains the pageSize of page and
    the next page index (the index starts with 0)`, () => {
    const pageSize = 10;
    const currentPage = 5;
    const fixture = initializePaginator(currentPage, 199, pageSize);
    const component = fixture.componentInstance;
    let pagedParameters: PageParameters;
    component.selectNextPage.subscribe((pagedParams) => {
      pagedParameters = pagedParams;
    });
    fixture.detectChanges();
    const nextPageElem = fixture.debugElement.query(By.css('a.goToNextPage'))
      .nativeElement;
    nextPageElem.click();
    const currentPageIndex = currentPage - 1;
    const nextPageIndex = currentPageIndex + 1;
    expect(pagedParameters).toBeTruthy();
    expect(
      pagedParameters.pageSize === pageSize &&
        pagedParameters.page === nextPageIndex
    );
  });

  test(`The next page (>>) link does not emit any event
        when
        the current page is the last page`, () => {
    const pageSize = 10;
    const currentPage = 20;
    const fixture = initializePaginator(currentPage, 199, pageSize);
    const component = fixture.componentInstance;
    let pagedParameters: PageParameters;
    component.selectNextPage.subscribe((pagedParams) => {
      pagedParameters = pagedParams;
    });
    fixture.detectChanges();
    const nextPageElem = fixture.debugElement.query(By.css('a.goToNextPage'))
      .nativeElement;
    nextPageElem.click();
    expect(pagedParameters).toBeFalsy();
  });

  test(`The previous page (<<) link emits an event
    which contains the pageSize of page and
    the previous page index (the index starts with 0)`, () => {
    const pageSize = 10;
    const currentPage = 5;
    const fixture = initializePaginator(currentPage, 199, pageSize);
    const component = fixture.componentInstance;
    let pagedParameters: PageParameters;
    component.selectPreviousPage.subscribe((pagedParams) => {
      pagedParameters = pagedParams;
    });
    fixture.detectChanges();
    const nextPageElem = fixture.debugElement.query(
      By.css('a.goToPreviousPage')
    ).nativeElement;
    nextPageElem.click();
    const currentPageIndex = currentPage - 1;
    const previousPageIndex = currentPageIndex - 1;
    expect(pagedParameters).toBeTruthy();
    expect(
      pagedParameters.pageSize === pageSize &&
        pagedParameters.page === previousPageIndex
    );
  });

  test(`The previous page (<<) link does not emit any event
        when
        the current page is the first page`, () => {
    const pageSize = 10;
    const currentPage = 1;
    const fixture = initializePaginator(currentPage, 199, pageSize);
    const component = fixture.componentInstance;
    let pagedParameters: PageParameters;
    component.selectNextPage.subscribe((pagedParams) => {
      pagedParameters = pagedParams;
    });
    fixture.detectChanges();
    const nextPageElem = fixture.debugElement.query(
      By.css('a.goToPreviousPage')
    ).nativeElement;
    nextPageElem.click();
    expect(pagedParameters).toBeFalsy();
  });

  test(`When there are more than two pages behind the current/active/selected page,
          dots are displayed behind the previous page of the current page`, () => {
    const pageSize = 10;
    const currentPage = 6;
    const fixture = initializePaginator(currentPage, 199, pageSize);
    fixture.detectChanges();
    const dotsElem = fixture.debugElement.query(By.css('li.dots-before'));
    expect(dotsElem).toBeTruthy();
    expect(dotsElem.nativeElement.textContent.trim() === '...');
  });

  test(`When there are more than two pages behind the current/active/selected page,
           dots are displayed behind the previous page of the current page`, () => {
    const pageSize = 10;
    const currentPage = 3;
    const fixture = initializePaginator(currentPage, 199, pageSize);
    fixture.detectChanges();
    const dotsElem = fixture.debugElement.query(By.css('li.dots-after'));
    expect(dotsElem).toBeTruthy();
    expect(dotsElem.nativeElement.textContent.trim() === '...');
  });

  test(`When a page number link is clicked, Paginator emits an event
    which contains the pageSize of the page and
    the page index of the clicked page (the index starts with 0)`, () => {
    const pageSize = 10;
    const currentPage = 4;
    const fixture = initializePaginator(currentPage, 199, pageSize);
    const component = fixture.componentInstance;
    let pagedParameters: PageParameters;
    component.selectNextPage.subscribe((pagedParams) => {
      pagedParameters = pagedParams;
    });
    component.selectPreviousPage.subscribe((pagedParams) => {
      pagedParameters = pagedParams;
    });
    component.selectFirstPage.subscribe((pagedParams) => {
      pagedParameters = pagedParams;
    });
    component.selectLastPage.subscribe((pagedParams) => {
      pagedParameters = pagedParams;
    });

    const elements: DebugElement[] = fixture.debugElement.queryAll(
      By.css('a.hmcts-pagination__item > hmcts-pagination__link')
    );
    expect(elements.length);
    elements.forEach((element) => {
      const pageIndex = +element.nativeElement.textContent.trim() - 1;
      element.nativeElement.click();
      expect(pagedParameters.pageSize === pageSize);
      expect(pagedParameters.page === pageIndex);
    });
  });

  test(`The "rows per page" text should be in a label element with a
    "for" attribute that matches the "id" attribute of the combobox.`, () => {
    const pageSize = 10;
    const currentPage = 4;
    const fixture = initializePaginator(currentPage, 199, pageSize);
    const label = fixture.debugElement.query(By.css('label[for=pageSize]'))
      .nativeElement;
    expect(label).toBeTruthy();
    expect(label.innerHTML).toBeTruthy();
    expect(label.innerHTML.trim()).toBe('rows per page');
  });

  test(`When page size select changes value
        then the paginator emits the first page index (0) and the new selected page size`, () => {
    const pageSize = 10;
    const currentPage = 4;
    const fixture = initializePaginator(currentPage, 199, pageSize);
    const component = fixture.componentInstance;
    let emittedParameters: PageParameters;
    component.changePageSize.subscribe((pageParameters) => {
      emittedParameters = pageParameters;
    });
    fixture.detectChanges();
    const select: HTMLSelectElement = fixture.debugElement.query(
      By.css('select[name="pageSize"]')
    ).nativeElement;
    select.value = select.options[1].value;
    select.dispatchEvent(new Event('change'));
    fixture.detectChanges();

    expect(emittedParameters.pageSize === component.pageSizeOptions[1].value);
    expect(emittedParameters.page === 0);
  });
});
