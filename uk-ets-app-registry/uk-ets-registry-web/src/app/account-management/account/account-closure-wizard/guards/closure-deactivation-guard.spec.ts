import { TestBed } from '@angular/core/testing';

import { ClosureDeactivationGuard } from './closure-deactivation-guard';
import { ActivatedRoute, Router } from '@angular/router';
import { provideMockStore } from '@ngrx/store/testing';

const routerSpy = jest.fn().mockImplementation((): Partial<Router> => ({}));
const activatedRouteSpy = jest
  .fn()
  .mockImplementation((): Partial<ActivatedRoute> => ({}));

describe('ClosureDeactivationGuardGuard', () => {
  let guard: ClosureDeactivationGuard;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideMockStore(),
        { provide: Router, useValue: routerSpy },
        { provide: ActivatedRoute, useValue: activatedRouteSpy },
      ],
    });
    guard = TestBed.inject(ClosureDeactivationGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });
});
