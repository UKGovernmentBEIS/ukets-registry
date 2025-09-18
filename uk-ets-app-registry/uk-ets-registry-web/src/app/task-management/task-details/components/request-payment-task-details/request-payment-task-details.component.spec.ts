import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RequestPaymentTaskDetailsComponent } from './request-payment-task-details.component';
import { taskDetailsBase } from '@task-management/model/task-details.model.spec';
import { RequestType } from '@task-management/model';
import { provideRouter } from '@angular/router';
import { routes } from '@task-details/task-details-routing.module';

describe('RequestPaymentTaskDetailsComponent', () => {
  let component: RequestPaymentTaskDetailsComponent;
  let fixture: ComponentFixture<RequestPaymentTaskDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RequestPaymentTaskDetailsComponent],
      providers: [provideRouter(routes)],
    }).compileComponents();

    fixture = TestBed.createComponent(RequestPaymentTaskDetailsComponent);
    component = fixture.componentInstance;
    component.taskDetails = {
      ...taskDetailsBase,
      taskType: RequestType.PAYMENT_REQUEST,
      recipient: 'UK12345',
      userUrid: 'UK12345',
      //TODO add map if needed
      amountRequested: 456.88,
      amountPaid: null,
      description: 'A test payment',
      paymentLink: 'Not yet generated',
      comment: null,
      reasonForAssignment: null,
      completedDate: new Date(),
    };
    fixture.detectChanges();
  });

  test('should create', () => {
    expect(component).toBeTruthy();
  });
});
