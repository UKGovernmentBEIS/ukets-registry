import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideMockStore } from '@ngrx/store/testing';

import { PaymentSelectMethodContainerComponent } from '@task-details/components';
import { routes } from '@task-details/task-details-routing.module';
import { ActivatedRoute, provideRouter } from '@angular/router';

xdescribe('PaymentSelectMethodContainerComponent', () => {
  let component: PaymentSelectMethodContainerComponent;
  let fixture: ComponentFixture<PaymentSelectMethodContainerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PaymentSelectMethodContainerComponent],
      providers: [
        provideMockStore(),
        provideRouter(routes),
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              _routerState: {
                url: '/task-details/:requestId/payment-bacs-details',
              },
              paramMap: {
                requestId: 8484,
              },
            },
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(PaymentSelectMethodContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  test('should create', () => {
    expect(component).toBeTruthy();
  });
});
