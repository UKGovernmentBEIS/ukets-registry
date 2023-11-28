import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { CancelRequestLinkComponent } from './cancel-request-link.component';
import { RouterTestingModule } from '@angular/router/testing';

describe('CancelRequestLinkComponent', () => {
  let component: CancelRequestLinkComponent;
  let fixture: ComponentFixture<CancelRequestLinkComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [CancelRequestLinkComponent],
        imports: [RouterTestingModule],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(CancelRequestLinkComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
