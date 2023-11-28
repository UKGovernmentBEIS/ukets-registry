import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { CancelUpdateRequestComponent } from './cancel-update-request.component';

describe('CancelUpdateRequestComponent', () => {
  let component: CancelUpdateRequestComponent;
  let fixture: ComponentFixture<CancelUpdateRequestComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [CancelUpdateRequestComponent],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(CancelUpdateRequestComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
