import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PaymentConfirmationContainerComponent } from '@task-details/components';
import { provideMockStore } from '@ngrx/store/testing';

describe('PaymentConfirmationContainerComponent', () => {
  let component: PaymentConfirmationContainerComponent;
  let fixture: ComponentFixture<PaymentConfirmationContainerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PaymentConfirmationContainerComponent],
      providers: [provideMockStore()],
    }).compileComponents();

    fixture = TestBed.createComponent(PaymentConfirmationContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  test('should create', () => {
    expect(component).toBeTruthy();
  });
});
