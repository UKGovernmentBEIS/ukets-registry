import { TestBed } from '@angular/core/testing';

import { GlobalErrorHandlingService } from './global-error-handling.service';

describe('GlobalErrorHandlingService', () => {
  let service: GlobalErrorHandlingService;
  let location: Location;
  let consoleError;

  beforeAll(() => {
    location = window.location;
    delete window.location;
    window.location = {
      ...location,
      reload: jest.fn()
    };

    consoleError = console.error;
    console.error = jest.fn();
  });

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [GlobalErrorHandlingService]
    });
    service = TestBed.inject(GlobalErrorHandlingService);
  });

  afterEach(() => jest.resetAllMocks());

  afterAll(() => {
    jest.restoreAllMocks();
    window.location = location;
    console.error = consoleError;
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should reload if chunk load failed', () => {
    const error = new Error('Loading chunk 0 failed');
    service.handleError(error);
    expect(window.location.reload).toHaveBeenCalledTimes(1);
    expect(console.error).toHaveBeenCalledTimes(1);
    expect(console.error).toHaveBeenCalledWith(error);
  });

  it('should log error on any other error', () => {
    const error = new Error('Something');
    service.handleError(error);
    expect(window.location.reload).not.toHaveBeenCalled();
    expect(console.error).toHaveBeenCalledTimes(1);
    expect(console.error).toHaveBeenCalledWith(error);
  });
});
