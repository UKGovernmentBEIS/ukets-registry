import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AllocationJobStatusResultsComponent } from './allocation-job-status-results.component';
import { Store } from '@ngrx/store';
import { of } from 'rxjs';

describe.skip('AllocationJobStatusResultsComponent', () => {
  let component: AllocationJobStatusResultsComponent;
  let fixture: ComponentFixture<AllocationJobStatusResultsComponent>;
  let storeMock: Partial<Store>;

  beforeEach(async () => {
    storeMock = {
      dispatch: jest.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [AllocationJobStatusResultsComponent],
      providers: [{ provide: Store, useValue: storeMock }],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AllocationJobStatusResultsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should dispatch navigateToTask action when goToTask is called', () => {
    const requestIdentifier = '123';
    component.goToTask(requestIdentifier);
    expect(storeMock.dispatch).toHaveBeenCalledWith({
      type: '[Allocation Job Status] Navigate to task',
      requestIdentifier,
    });
  });
});
