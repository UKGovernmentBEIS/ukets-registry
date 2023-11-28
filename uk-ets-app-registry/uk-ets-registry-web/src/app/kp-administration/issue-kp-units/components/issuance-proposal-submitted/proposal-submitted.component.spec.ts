import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProposalSubmittedComponent } from './proposal-submitted.component';

describe('ProposalSubmittedComponent', () => {
  let component: ProposalSubmittedComponent;
  let fixture: ComponentFixture<ProposalSubmittedComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [ProposalSubmittedComponent],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(ProposalSubmittedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
