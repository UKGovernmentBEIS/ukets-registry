import { waitForAsync, TestBed, ComponentFixture } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { SortableTableDirective } from '@shared/search/sort/sortable-table.directive';
import { SortableColumnDirective } from '@shared/search/sort/sortable-column.directive';
import { SortService } from '@shared/search/sort/sort.service';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { RouterLinkDirectiveStub } from '@shared/test/router-link-directive-stub';
import { PaginatorComponent } from '@shared/search/paginator';
import { GdsDateTimeShortPipe } from '@shared/pipes';
import { SearchMessagesResultsComponent } from '@kp-administration/itl-messages/components';
import { MessageSearchResult } from '@kp-administration/itl-messages/model';

describe('SearchMessagesResultsComponent', () => {
  let component: SearchMessagesResultsComponent;
  let fixture: ComponentFixture<SearchMessagesResultsComponent>;

  const messages: MessageSearchResult[] = [
    {
      messageId: 5,
      from: 'ITL',
      to: 'GB',
      messageDate: '2020-11-06T16:39:03.34',
      content: 'A Test message from ITL',
    },
  ];

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [ReactiveFormsModule, FormsModule],
        declarations: [
          SortableColumnDirective,
          SortableTableDirective,
          SearchMessagesResultsComponent,
          PaginatorComponent,
          RouterLinkDirectiveStub,
          GdsDateTimeShortPipe,
        ],
        providers: [SortService],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(SearchMessagesResultsComponent);
    component = fixture.componentInstance;
  });

  test('the component is created', () => {
    expect(component).toBeTruthy();
  });

  test('the results are rendered in the corresponded columns', () => {
    component.results = messages;
    fixture.detectChanges();
    const columns = fixture.debugElement.queryAll(By.css('td'));
    expect(parseInt(columns[0].nativeElement.textContent.trim())).toBe(
      messages[0].messageId
    );
    expect(columns[1].nativeElement.textContent.trim()).toBe(messages[0].from);
    expect(columns[2].nativeElement.textContent.trim()).toBe(messages[0].to);
    expect(columns[3].nativeElement.textContent.trim()).toBe(
      '6 Nov 2020, 4:39pm'
    );
    expect(columns[4].nativeElement.textContent.trim()).toBe(
      messages[0].content
    );
  });

  // TODO: change logic and re-enable this unit test.
  // routerlink is no longer user when navigating to taskdetails
  // it('can navigate and pass params to the task detail view', () => {
  //   component.results = tasks;
  //   component.selectedTasks = [];
  //   fixture.detectChanges(); // trigger initial data binding
  //
  //   // find DebugElements with an attached RouterLinkStubDirective
  //   const linkDes = fixture.debugElement.queryAll(
  //     By.directive(RouterLinkDirectiveStub)
  //   );
  //   // get attached link directive instances
  //   // using each DebugElement's injector
  //   const routerLinks = linkDes.map(de =>
  //     de.injector.get(RouterLinkDirectiveStub)
  //   );
  //
  //   const taskDetailsLinkDe = linkDes[0]; // First Task link DebugElement
  //   const taskDetailsLink = routerLinks[0]; // First Task link directive
  //
  //   expect(taskDetailsLink.navigatedTo).toBeNull();
  //
  //   taskDetailsLinkDe.triggerEventHandler('click', null);
  //   fixture.detectChanges();
  //
  //   expect(taskDetailsLink.navigatedTo).toEqual([
  //     '/task-details',
  //     'test-requestId'
  //   ]);
  // });
});
