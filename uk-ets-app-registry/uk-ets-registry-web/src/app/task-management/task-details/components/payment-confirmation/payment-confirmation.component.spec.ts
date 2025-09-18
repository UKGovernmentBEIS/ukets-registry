import { ComponentFixture, TestBed } from '@angular/core/testing';
import { GdsDateTimePipe } from '@registry-web/shared/pipes';
import { PaymentConfirmationComponent } from '@task-details/components';
import { RequestType, TaskOutcome } from '@task-management/model';

describe('PaymentConfirmationComponent', () => {
  let component: PaymentConfirmationComponent;
  let fixture: ComponentFixture<PaymentConfirmationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PaymentConfirmationComponent, GdsDateTimePipe],
    }).compileComponents();

    fixture = TestBed.createComponent(PaymentConfirmationComponent);
    component = fixture.componentInstance;
    component.paymentCompletionDetails = {
      requestIdentifier: 'RequestId',
      taskDetailsDTO: {
        amountRequested: 401.6,
        amountPaid: 400,
        description: 'Payment fee for chmahe permission rights',
        taskType: RequestType.PAYMENT_REQUEST,
        paymentLink: '',
        paymentMethod: 'CARD_OR_DIGITAL_WALLET',
        recipient: '',
        userUrid: '',
        reasonForAssignment: '',
        comment: '',
        completedDate: undefined,
        requestId: '',
        initiatorName: '',
        initiatorUrid: '',
        claimantName: '',
        claimantURID: '',
        taskStatus: '',
        requestStatus: TaskOutcome.APPROVED,
        initiatedDate: '',
        claimedDate: '',
        currentUserClaimant: false,
        completedByName: '',
        accountNumber: '',
        accountFullIdentifier: '',
        accountName: '',
        referredUserFirstName: '',
        referredUserLastName: '',
        referredUserURID: '',
        history: [],
        subTasks: [],
        parentTask: undefined,
      },
      referenceNumber: 'ReferenceNo',
      status: 'SUCCESS',
      paidOn: null,
      paidBy: '',
      method: 'CARD_OR_DIGITAL_WALLET',
    };
    fixture.detectChanges();
  });

  test('should create', () => {
    expect(component).toBeTruthy();
  });
});
