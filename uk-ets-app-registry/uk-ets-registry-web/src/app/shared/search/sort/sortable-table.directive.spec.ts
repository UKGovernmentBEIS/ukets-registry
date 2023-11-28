import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import { SortableColumnDirective } from '@shared/search/sort/sortable-column.directive';
import { Component, DebugElement } from '@angular/core';
import { SortService } from '@shared/search/sort/sort.service';
import { By } from '@angular/platform-browser';

@Component({
  selector: 'app-sortable-column-wrapper',
  template: `
    <table class="govuk-table">
      <thead class="govuk-table__head">
        <tr class="govuk-table__row">
          <th appSortableColumn [sortField]="'testField'">Test column</th>
        </tr>
      </thead>
    </table>
  `,
})
class SortableColumnWrapperComponent {}

describe('Directive: SortableColumnDirective', () => {
  let component: SortableColumnWrapperComponent;
  let fixture: ComponentFixture<SortableColumnWrapperComponent>;
  let thElem: DebugElement;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [SortableColumnDirective, SortableColumnWrapperComponent],
        providers: [SortService],
      });
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(SortableColumnWrapperComponent);
    component = fixture.componentInstance;
    thElem = fixture.debugElement.query(By.css('th'));
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  test(`aria-sort th element's attribute value should be toggled between ascending and descending values on th element click.`, () => {
    // given
    const getAriaSortValue = function () {
      return thElem.nativeElement.children[0].getAttribute('aria-sort');
    };
    // then
    expect(getAriaSortValue()).toBe('');

    // when
    thElem.nativeElement.click();
    fixture.detectChanges();
    // then
    expect(getAriaSortValue()).toBe('ascending');

    // when
    thElem.nativeElement.click();
    fixture.detectChanges();
    // then
    expect(getAriaSortValue()).toBe('descending');
  });
});
