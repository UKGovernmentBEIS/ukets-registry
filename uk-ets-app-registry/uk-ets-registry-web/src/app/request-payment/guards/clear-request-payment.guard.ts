import { CanDeactivateFn } from '@angular/router';
import { clearRequestPayment } from '../store/actions';
import { inject } from '@angular/core';
import { Store } from '@ngrx/store';

export const clearRequestPaymentGuard: CanDeactivateFn<boolean> = () => {
  const store = inject(Store);
  store.dispatch(clearRequestPayment());
  return true;
};
