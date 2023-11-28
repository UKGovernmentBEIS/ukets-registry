import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { AllocationTableUploadTaskDetailsComponent } from '@task-details/components';
import { RouterTestingModule } from '@angular/router/testing';

describe('AllocationTableUploadTaskDetailsComponent', () => {
  let component: AllocationTableUploadTaskDetailsComponent;
  let fixture: ComponentFixture<AllocationTableUploadTaskDetailsComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [AllocationTableUploadTaskDetailsComponent],
        imports: [RouterTestingModule],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(
      AllocationTableUploadTaskDetailsComponent
    );
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
