import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import { routes } from '@request-payment/request-payment-routing.module';
import { CheckPaymentRequestContainerComponent } from '@request-payment/components';
import {
  cancelRequestPayment,
  downloadInvoice,
  submitPaymentRequest,
} from '@request-payment/store/actions';
import { canGoBack } from '@shared/shared.action';

describe('CheckPaymentRequestContainerComponent', () => {
  let component: CheckPaymentRequestContainerComponent;
  let fixture: ComponentFixture<CheckPaymentRequestContainerComponent>;
  let mockStore: MockStore;
  let dispatchSpy: jasmine.Spy;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CheckPaymentRequestContainerComponent],
      providers: [
        provideMockStore(),
        provideRouter(routes),
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              _routerState: { url: '/request-payment/check-payment-request' },
            },
          },
        },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    mockStore = TestBed.inject(MockStore);
    dispatchSpy = spyOn(mockStore, 'dispatch'); // Spy on dispatch calls
    fixture = TestBed.createComponent(CheckPaymentRequestContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  test('should create', () => {
    expect(component).toBeTruthy();
    const action = canGoBack({
      goBackRoute: '/request-payment/set-payment-details',
    });
    expect(dispatchSpy).toHaveBeenLastCalledWith(action);
  });

  test('should dispatch submitPaymentRequest action', () => {
    //trigger the dispatch
    component.onSubmit();

    const action = submitPaymentRequest();
    expect(dispatchSpy).toHaveBeenLastCalledWith(action);
  });

  test('should dispatch downloadInvoice action', () => {
    //trigger the dispatch
    component.onInvoiceDownload();

    const action = downloadInvoice();
    expect(dispatchSpy).toHaveBeenLastCalledWith(action);
  });

  test('should dispatch cancelRequestPayment action', () => {
    //trigger the dispatch
    component.onCancel();

    const action = cancelRequestPayment({
      route: '/request-payment/check-payment-request',
    });
    expect(dispatchSpy).toHaveBeenLastCalledWith(action);
  });
});
