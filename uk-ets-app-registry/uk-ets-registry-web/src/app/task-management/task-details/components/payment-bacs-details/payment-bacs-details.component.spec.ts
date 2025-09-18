import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PaymentBacsDetailsComponent } from './payment-bacs-details.component';

describe('PaymentBacsDetailsComponent', () => {
  let component: PaymentBacsDetailsComponent;
  let fixture: ComponentFixture<PaymentBacsDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PaymentBacsDetailsComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(PaymentBacsDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
