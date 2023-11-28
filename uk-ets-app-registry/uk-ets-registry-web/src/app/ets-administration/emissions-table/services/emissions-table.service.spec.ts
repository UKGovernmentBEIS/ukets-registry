import { HttpResponse } from '@angular/common/http';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';

import { EmissionsTableService } from './emissions-table.service';

describe('EmissionsTableService', () => {
  let service: EmissionsTableService;
  let httpMock: HttpTestingController;
  let baseApiUrl: string;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        { provide: UK_ETS_REGISTRY_API_BASE_URL, useValue: 'apiBaseUrl' },
      ],
    });
    service = TestBed.inject(EmissionsTableService);
    httpMock = TestBed.inject(HttpTestingController);
    baseApiUrl = TestBed.inject(UK_ETS_REGISTRY_API_BASE_URL);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should HTTP POST for submission of SelectedEmissionsTable', () => {
    // given
    const mockedResponse = '10002';
    let response: string;
    const emissionsTableRequest = {
      otp: '123456',
      fileHeader: {
        id: 1,
        fileName: 'UK_Emissions_280629021_BEIS_jhfds8h329ry49.xlsx',
        fileSize: '109876',
      },
    };
    // when
    service
      .submitSelectedEmissionsTable(emissionsTableRequest)
      .subscribe((taskIdentifier) => (response = taskIdentifier));
    //then
    const req = httpMock.expectOne((r) =>
      r.url.startsWith(`${baseApiUrl}/emissions-table.submit`)
    );
    expect(req.request.url).toBe(`${baseApiUrl}/emissions-table.submit`);
    expect(req.request.method).toEqual('POST');
    expect(req.request.body).toEqual(emissionsTableRequest);

    req.flush(mockedResponse);
    httpMock.verify();
  });

  // it('should HTTP GET for download of EmissionsTable errors', () => {
  //   // given
  //   let response: HttpResponse<Blob>;
  //   const fileId = 50005;
  //   // when
  //   service
  //     .downloadErrorsCSV(fileId)
  //     .subscribe((errors) => (response = errors));
  //   //then
  //   const req = httpMock.expectOne((r) =>
  //     r.url.startsWith(`${baseApiUrl}/emissions-table-errors.download`)
  //   );
  //   expect(req.request.url).toBe(`${baseApiUrl}/emissions-table-errors.download`);
  //   expect(req.request.method).toEqual('GET');
  //   expect(req.request.params.get('fileId')).toEqual(fileId.toString());

  //   httpMock.verify();
  // });

  afterEach(() => httpMock.verify());
});
