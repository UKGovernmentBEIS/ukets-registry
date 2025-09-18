import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SetPaymentDetailsComponent } from '@request-payment/components';
import { ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from '@shared/shared.module';
import { By } from '@angular/platform-browser';
import { PaymentDetails } from '@request-payment/model';
import { async } from 'rxjs';

describe('SetPaymentDetailsComponent', () => {
  let component: SetPaymentDetailsComponent;
  let fixture: ComponentFixture<SetPaymentDetailsComponent>;

  const candidateRecipients = [
    {
      firstName: 'Rep1',
      lastName: 'Auth1',
      alsoKnownAs: 'Jimmy',
      urid: 'UK791916189158',
      state: 'REGISTERED',
    },
    {
      firstName: 'Rep2',
      lastName: 'Auth2',
      alsoKnownAs: 'Tiger',
      urid: 'UK611629127282',
      state: 'ENROLLED',
    },
    {
      firstName: 'Rep3',
      lastName: 'Auth3',
      alsoKnownAs: null,
      urid: 'UK367902749814',
      state: 'VALIDATED',
    },
  ];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SetPaymentDetailsComponent],
      imports: [ReactiveFormsModule, SharedModule],
    }).compileComponents();

    fixture = TestBed.createComponent(SetPaymentDetailsComponent);
    component = fixture.componentInstance;
    component.candidateRecipients = candidateRecipients;
    fixture.detectChanges();
  });

  test('should create', () => {
    expect(component).toBeTruthy();
  });

  test('should have as caption `Change the account status`', () => {
    const caption = fixture.debugElement.query(By.css('.govuk-caption-xl'));
    expect(caption.nativeElement.textContent).toContain('Request payment');
  });

  test('should render title in h1 tag', () => {
    const compiled = fixture.debugElement.nativeElement;
    expect(compiled.querySelector('h1').textContent).toContain(
      'Enter payment details'
    );
  });

  test('candidate representatives are rendered correctly', () => {
    candidateRecipients.forEach((representative, index) => {
      const labelName = fixture.debugElement.nativeElement.querySelector(
        `option[value="${index}: ${representative.urid}"]`
      ).textContent;
      expect(
        representative.alsoKnownAs
          ? representative.alsoKnownAs
          : representative.firstName + ' ' + representative.lastName
      ).toBe(labelName.trim());
    });
  });

  test('all fields required', () => {
    const continueButton = fixture.debugElement.query(By.css('button'));
    continueButton.nativeElement.click();
    fixture.detectChanges();

    expect(component.getAllFormGroupValidationErrors()).toBeDefined();
    // expect(
    //   component.genericValidator.processMessages(component.formGroup).recipient
    // ).toBe('Please select a recipient.');
    expect(
      component.genericValidator.processMessages(component.formGroup).amount
    ).toBe('Please enter an amount.');
    expect(
      component.genericValidator.processMessages(component.formGroup)
        .description
    ).toBe('Please enter a description.');

    // error message
    const debugElements = fixture.debugElement.queryAll(
      By.css('.govuk-error-message')
    );

    // expect(debugElements[0].nativeElement.textContent.trim()).toBe(
    //   'Error: Please select a recipient.'
    // );
    expect(debugElements[0].nativeElement.textContent.trim()).toBe(
      'Error: Please enter an amount.'
    );
    expect(debugElements[1].nativeElement.textContent.trim()).toBe(
      'Error: Please enter a description.'
    );
  });

  test('should raise selected event when click continue', () => {
    const userProvidedDetails = {
      recipientUrid: 'UK367902749814',
      recipientName: 'Rep3 Auth3',
      amount: '1090',
      description: 'A test payment',
    };
    let submittedDetails: PaymentDetails | undefined;
    //Subscribe to the output
    component.setPaymentDetails.subscribe(
      (details: PaymentDetails) => (submittedDetails = details)
    );

    //Simulate user entry for recipient
    const recipientSelect =
      fixture.debugElement.nativeElement.querySelector('select');
    recipientSelect.options[2].selected = true;
    // Dispatch a DOM event so that Angular learns of input value change.
    recipientSelect.dispatchEvent(new Event('change'));

    //Simulate user entry for amount
    const amountInput =
      fixture.debugElement.nativeElement.querySelector('input');
    amountInput.value = userProvidedDetails.amount;
    // Dispatch a DOM event so that Angular learns of input value change.
    amountInput.dispatchEvent(new Event('input'));

    //Simulate user entry for description
    const descriptionInput = fixture.debugElement.query(By.css('textarea'));
    descriptionInput.nativeElement.value = userProvidedDetails.description;
    descriptionInput.nativeElement.dispatchEvent(new Event('input'));

    const continueButton = fixture.debugElement.query(By.css('button'));
    continueButton.nativeElement.click();
    fixture.detectChanges();

    const recipient =
      fixture.componentInstance.formGroup.get('recipient').value;
    expect(recipient).toEqual('UK367902749814');
    const amount = fixture.componentInstance.formGroup.get('amount').value;
    expect(amount).toEqual('1090');
    const description =
      fixture.componentInstance.formGroup.get('description').value;
    expect(description).toEqual('A test payment');

    expect(submittedDetails).toEqual(userProvidedDetails);
  });
});
