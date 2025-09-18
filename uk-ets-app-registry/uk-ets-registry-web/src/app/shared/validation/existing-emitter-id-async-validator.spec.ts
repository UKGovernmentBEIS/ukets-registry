import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { FormControl } from '@angular/forms';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';
import { ExistingEmitterIdAsyncValidator } from './existing-emitter-id-async-validator';

describe('ExistingEmitterIdAsyncValidator', () => {
  let validator: ExistingEmitterIdAsyncValidator;
  let httpMock: HttpTestingController;
  const mockBaseUrl = 'https://baseApi';

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        ExistingEmitterIdAsyncValidator,
        { provide: UK_ETS_REGISTRY_API_BASE_URL, useValue: mockBaseUrl },
      ],
    });

    validator = TestBed.inject(ExistingEmitterIdAsyncValidator);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(validator).toBeTruthy();
  });


  it('should return null the EmitterID does not exist on another account', (done) => {
    const control = new FormControl('5653357886HT5');

    validator.validateEmitterId()(control).subscribe((result) => {
      expect(result).toBeNull();
      done();
    });

    const req = httpMock.expectOne(`${mockBaseUrl}/accounts.get.emitter-id?emitterId=5653357886HT5`);
    expect(req.request.method).toBe('GET');
    req.flush(false);
  });

  it('should return { exists: true } if the EmitterID exists on another account', (done) => {
    const control = new FormControl('5653357886HT5-exists');

    validator.validateEmitterId()(control).subscribe((result) => {
      expect(result).toEqual({ exists: true });
      done();
    });

    const req = httpMock.expectOne(`${mockBaseUrl}/accounts.get.emitter-id?emitterId=5653357886HT5-exists`);
    expect(req.request.method).toBe('GET');
    req.flush(true);
  });

  it('should return { exists: false } if the EmitterID does not exist on another account except the one specified by the operatorId', (done) => {
    const control = new FormControl('5653357886HT5-exists');
    const operatorId = 1000026;

    validator.validateEmitterId(operatorId)(control).subscribe((result) => {
      expect(result).toBeNull();
      done();
    });

    const req = httpMock.expectOne(`${mockBaseUrl}/accounts.get.emitter-id?emitterId=5653357886HT5-exists&operatorIdentifier=1000026`);
    expect(req.request.method).toBe('GET');
    req.flush(false);
  });

  it('should return { exists: true } if the EmitterID exists on another account (except the one specified by the operatorId)', (done) => {
    const control = new FormControl('5653357886HT5');
    const operatorId = 1000026;

    validator.validateEmitterId(operatorId)(control).subscribe((result) => {
      expect(result).toEqual({ exists: true });
      done();
    });

    const req = httpMock.expectOne(`${mockBaseUrl}/accounts.get.emitter-id?emitterId=5653357886HT5&operatorIdentifier=1000026`);
    expect(req.request.method).toBe('GET');
    req.flush(true);
  });

  it('should return { serverError: true } if Api request fails', (done) => {
    const control = new FormControl('5653357886HT5-err');

    validator.validateEmitterId()(control).subscribe((result) => {
      expect(result).toEqual({ serverError: true });
      done();
    });

    const req = httpMock.expectOne(`${mockBaseUrl}/accounts.get.emitter-id?emitterId=5653357886HT5-err`);
    expect(req.request.method).toBe('GET');
    req.error(new ErrorEvent('Network error'));
  });
});
