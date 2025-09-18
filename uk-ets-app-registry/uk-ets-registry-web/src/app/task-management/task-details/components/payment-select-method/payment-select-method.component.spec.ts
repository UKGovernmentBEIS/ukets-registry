import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PaymentSelectMethodComponent } from '@task-details/components';

describe('PaymentSelectMethodComponent', () => {
  let component: PaymentSelectMethodComponent;
  let fixture: ComponentFixture<PaymentSelectMethodComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PaymentSelectMethodComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(PaymentSelectMethodComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  test('should create', () => {
    expect(component).toBeTruthy();
  });
});
