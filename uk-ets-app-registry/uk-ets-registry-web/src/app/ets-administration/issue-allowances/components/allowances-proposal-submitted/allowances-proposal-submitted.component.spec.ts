import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { AllowancesProposalSubmittedComponent } from './allowances-proposal-submitted.component';

describe('AllowancesProposalSubmittedComponent', () => {
  let component: AllowancesProposalSubmittedComponent;
  let fixture: ComponentFixture<AllowancesProposalSubmittedComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [AllowancesProposalSubmittedComponent],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(AllowancesProposalSubmittedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
