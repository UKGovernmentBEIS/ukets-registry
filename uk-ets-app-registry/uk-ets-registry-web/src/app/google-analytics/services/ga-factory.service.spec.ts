import { TestBed } from '@angular/core/testing';

import { GaFactoryService } from './ga-factory.service';

describe('GaFactoryService', () => {
  let service: GaFactoryService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GaFactoryService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
