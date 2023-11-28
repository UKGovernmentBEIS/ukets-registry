import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { CancelTransactionProposalComponent } from './cancel-transaction-proposal.component';

describe('CancelTransactionProposalComponent', () => {
  let component: CancelTransactionProposalComponent;
  let fixture: ComponentFixture<CancelTransactionProposalComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [CancelTransactionProposalComponent],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(CancelTransactionProposalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
