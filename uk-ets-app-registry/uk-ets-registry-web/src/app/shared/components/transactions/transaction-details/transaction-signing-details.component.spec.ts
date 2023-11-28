import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { TransactionSigningDetailsComponent } from '@shared/components/transactions';
import { GdsDateShortPipe } from '@shared/pipes';

describe('TransactionSigningDetailsComponent', () => {
  let component: TransactionSigningDetailsComponent;
  let fixture: ComponentFixture<TransactionSigningDetailsComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [TransactionSigningDetailsComponent, GdsDateShortPipe],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(TransactionSigningDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
