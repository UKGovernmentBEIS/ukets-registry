import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CheckPaymentRequestComponent } from '@request-payment/components';

describe('CheckPaymentRequestComponent', () => {
  let component: CheckPaymentRequestComponent;
  let fixture: ComponentFixture<CheckPaymentRequestComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CheckPaymentRequestComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(CheckPaymentRequestComponent);
    component = fixture.componentInstance;
    // simulate the parent setting the input properties
    fixture.componentRef.setInput('recipientName', 'John Smith');
    fixture.componentRef.setInput('amount', '550.30');
    fixture.componentRef.setInput('description', 'A test payment');
    fixture.detectChanges();
  });

  test('should create', () => {
    expect(component).toBeTruthy();
  });
});
