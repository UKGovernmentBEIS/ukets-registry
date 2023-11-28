import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import { AllocationTableHistoryContainerComponent } from './allocation-table-history-container.component';
import { DomainEventsComponent } from '@shared/components/event/domain-events/domain-events.component';
import { GdsDateTimeShortPipe } from '@shared/pipes';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import { Store } from '@ngrx/store';
import { RouterTestingModule } from '@angular/router/testing';

describe('AllocationTableHistoryContainerComponent', () => {
  let component: AllocationTableHistoryContainerComponent;
  let fixture: ComponentFixture<AllocationTableHistoryContainerComponent>;
  let store: MockStore<any>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [
          AllocationTableHistoryContainerComponent,
          DomainEventsComponent,
          GdsDateTimeShortPipe,
        ],
        providers: [provideMockStore()],
        imports: [RouterTestingModule],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(AllocationTableHistoryContainerComponent);
    component = fixture.componentInstance;
    store = TestBed.inject(Store) as MockStore<any>;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
