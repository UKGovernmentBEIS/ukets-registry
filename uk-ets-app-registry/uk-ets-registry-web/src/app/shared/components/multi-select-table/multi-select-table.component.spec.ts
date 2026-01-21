import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';

import { MultiSelectTableComponent } from '@shared/components/multi-select-table';
import {
  TableColumn,
  SortEvent,
} from '@shared/components/multi-select-table/multi-selected-item.model';

describe('MultiSelectTableComponent', () => {
  @Component({
    standalone: true,
    imports: [MultiSelectTableComponent],
    template: `
      <app-multi-select-table
        [caption]="caption"
        [columns]="columns"
        [data]="data"
        (sort)="onSort($event)"
        (selectionChanged)="onSelectionChanged($event)"
      />
    `,
  })
  class TestComponent {
    columns: TableColumn[] = [
      {
        header: 'Name',
        field: 'name',
        widthClass: 'govuk-!-width-one-quarter',
        isHeader: true,
      },
      { header: 'Surname', field: 'surname' },
      { header: 'Age', field: 'age', isNumeric: true },
    ];
    data: any[] = [];
    caption: string;
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    onSort = jest.fn((_: SortEvent) => null);

    onSelectionChanged(data: any[]) {
      this.data = data;
    }
  }

  @Component({
    standalone: true,
    imports: [MultiSelectTableComponent],
    template: `
      <app-multi-select-table
        [caption]="caption"
        [columns]="columns"
        [data]="data"
        (sort)="onSort($event)"
        (selectionChanged)="onSelectionChanged($event)"
      >
        <ng-template let-column="column" let-row="row">
          @if (column.field === 'link') {
            <a>{{ row[column.field] }}</a>
          } @else {
            {{ row[column.field] }}
          }
        </ng-template>
      </app-multi-select-table>
    `,
  })
  class TestTemplateComponent {
    columns: TableColumn[] = [
      { header: 'Link', field: 'link' },
      { header: 'Text', field: 'text' },
    ];
    data: any[] = [];
    caption: string;
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    onSort = jest.fn((_: SortEvent) => null);

    onSelectionChanged(data: any[]) {
      this.data = data;
    }
  }

  let component: MultiSelectTableComponent<any>;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        MultiSelectTableComponent,
        TestComponent,
        TestTemplateComponent,
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(
      By.directive(MultiSelectTableComponent)
    ).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the caption', () => {
    const hostElement: HTMLElement = fixture.nativeElement;
    let caption = hostElement.querySelector<HTMLTableCaptionElement>('caption');

    expect(caption).toBeNull();

    hostComponent.caption = 'Test Caption';
    fixture.detectChanges();

    caption = hostElement.querySelector<HTMLTableCaptionElement>('caption');

    expect(caption).not.toBeNull();
    expect(caption.textContent).toContain('Test Caption');
  });

  it('should render the HTML elements', () => {
    const hostElement: HTMLElement = fixture.nativeElement;
    const headers =
      hostElement.querySelectorAll<HTMLTableCellElement>('thead th');
    const checkBoxes = hostElement.querySelectorAll<HTMLElement>(
      '.govuk-checkboxes__input'
    );

    expect(headers).toHaveLength(3);
    expect(checkBoxes).toHaveLength(0);
    expect(headers[0].innerHTML).toContain('Name');
    expect(headers[1].innerHTML).toContain('Surname');
    expect(headers[2].innerHTML).toContain('Age');
  });

  it('should render the data', () => {
    hostComponent.data = [
      { name: 'Name 1', surname: 'Surname 1', age: 23 },
      { name: 'Name 2', surname: 'Surname 2', age: 48 },
      { name: 'Name 3', surname: 'Surname 3', age: 32 },
    ];
    fixture.detectChanges();

    const hostElement: HTMLElement = fixture.nativeElement;
    const rows = hostElement.querySelectorAll<HTMLTableCellElement>('tbody tr');
    const checkBoxes = hostElement.querySelectorAll<HTMLElement>(
      '.govuk-checkboxes__input'
    );

    expect(rows).toHaveLength(3);
    expect(checkBoxes).toHaveLength(4);

    expect(
      rows[0].querySelectorAll<HTMLTableCellElement>('td')[0].textContent.trim()
    ).toContain('Select item');
    expect(
      rows[0].querySelectorAll<HTMLTableCellElement>('td')[1].textContent.trim()
    ).toContain('Name 1');
    expect(
      rows[0].querySelectorAll<HTMLTableCellElement>('td')[2].textContent.trim()
    ).toContain('Surname 1');
    expect(
      rows[0].querySelectorAll<HTMLTableCellElement>('td')[3].textContent.trim()
    ).toContain('23');
  });

  it('should assign width classes', () => {
    hostComponent.data = [{ name: 'Name 1', surname: 'Surname 1', age: 23 }];
    fixture.detectChanges();
    const hostElement: HTMLElement = fixture.nativeElement;
    const headers =
      hostElement.querySelectorAll<HTMLTableCellElement>('thead th');

    expect(headers[1].classList).toContain('govuk-!-width-one-quarter');
  });

  it('should assign numeric class', () => {
    hostComponent.data = [
      { name: 'Name 1', surname: 'Surname 1', age: 23 },
      { name: 'Name 2', surname: 'Surname 2', age: 48 },
      { name: 'Name 3', surname: 'Surname 3', age: 32 },
    ];
    fixture.detectChanges();

    const hostElement: HTMLElement = fixture.nativeElement;
    const tds = hostElement.querySelectorAll<HTMLTableCellElement>('td');

    expect(tds[0].classList).not.toContain('govuk-table__cell--numeric');
    expect(tds[1].classList).not.toContain('govuk-table__cell--numeric');
    expect(tds[2].classList).not.toContain('govuk-table__cell--numeric');
    expect(tds[3].classList).toContain('govuk-table__cell--numeric');
  });

  it('should correctly check items', () => {
    hostComponent.data = [
      { name: 'Name 1', surname: 'Surname 1', age: 23 },
      { name: 'Name 2', surname: 'Surname 2', age: 48 },
      { name: 'Name 3', surname: 'Surname 3', age: 32 },
    ];
    fixture.detectChanges();

    const hostElement: HTMLElement = fixture.nativeElement;
    const checkBoxes = hostElement.querySelectorAll<HTMLElement>(
      '.govuk-checkboxes__input'
    );

    expect(checkBoxes).toHaveLength(4);

    // Click 3rd checkbox(since there is global-select) for 2nd row of data
    checkBoxes[2].click();
    fixture.detectChanges();
    expect(hostComponent.data[1]).toEqual({
      name: 'Name 2',
      surname: 'Surname 2',
      age: 48,
      isSelected: true,
    });

    // Click global-select checkbox
    checkBoxes[0].click();
    fixture.detectChanges();
    expect(hostComponent.data).toEqual([
      { name: 'Name 1', surname: 'Surname 1', age: 23, isSelected: true },
      { name: 'Name 2', surname: 'Surname 2', age: 48, isSelected: true },
      { name: 'Name 3', surname: 'Surname 3', age: 32, isSelected: true },
    ]);
  });

  it('should display sort buttons and emit event on click', () => {
    hostComponent.columns = [
      { header: 'One', field: 'first', isSortable: true },
      { header: 'Second', field: 'second', isSortable: true },
      { header: 'Third', field: 'third', isSortable: false },
    ];
    hostComponent.data = [
      { first: 1, second: new Date('2020-07-23T10:00:00Z'), third: 'abc' },
      { first: 2, second: new Date('2020-07-23T11:00:00Z'), third: 'cda' },
    ];

    fixture.detectChanges();
    const sortButtons = fixture.debugElement.queryAll(
      By.css('[aria-sort] button')
    );

    expect(sortButtons.length).toEqual(2);

    sortButtons[0].nativeElement.click();
    fixture.detectChanges();

    expect(hostComponent.onSort).toHaveBeenCalledWith({
      column: 'first',
      direction: 'ascending',
    });
    expect(sortButtons[0].parent.attributes['aria-sort']).toBe('ascending');
    expect(sortButtons[1].parent.attributes['aria-sort']).toBe('none');

    sortButtons[0].nativeElement.click();
    fixture.detectChanges();

    expect(hostComponent.onSort).toHaveBeenCalledWith({
      column: 'first',
      direction: 'descending',
    });
    expect(sortButtons[0].parent.attributes['aria-sort']).toBe('descending');
    expect(sortButtons[1].parent.attributes['aria-sort']).toBe('none');

    sortButtons[1].nativeElement.click();
    fixture.detectChanges();

    expect(hostComponent.onSort).toHaveBeenCalledWith({
      column: 'second',
      direction: 'ascending',
    });
    expect(sortButtons[0].parent.attributes['aria-sort']).toBe('none');
    expect(sortButtons[1].parent.attributes['aria-sort']).toBe('ascending');
  });

  it('should display custom template', () => {
    const templateFixture = TestBed.createComponent(TestTemplateComponent);
    templateFixture.componentInstance.data = [
      { link: 'Go to', text: 'Something to watch' },
    ];
    templateFixture.detectChanges();

    const element: HTMLElement = templateFixture.nativeElement;
    const anchors = element.querySelectorAll('a');

    expect(anchors.length).toEqual(1);
    expect(anchors[0].textContent).toEqual('Go to');
  });
});
