import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { CancelUpdateAllocationStatusComponent } from './cancel-update-allocation-status.component';
import { CancelUpdateRequestComponent } from '@shared/components/cancel-update-request';
import { provideMockStore } from '@ngrx/store/testing';

describe('CancelUpdateRequestComponent', () => {
  let component: CancelUpdateAllocationStatusComponent;
  let fixture: ComponentFixture<CancelUpdateAllocationStatusComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [
          CancelUpdateRequestComponent,
          CancelUpdateAllocationStatusComponent
        ],
        providers: [provideMockStore()]
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(CancelUpdateAllocationStatusComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
