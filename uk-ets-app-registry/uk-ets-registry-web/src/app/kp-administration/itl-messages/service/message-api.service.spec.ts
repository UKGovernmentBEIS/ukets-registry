import { waitForAsync, TestBed } from '@angular/core/testing';

import { UK_ETS_REGISTRY_API_BASE_URL } from 'src/app/app.tokens';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { MessageApiService } from '@kp-administration/itl-messages/service';
import {
  MessageDetails,
  MessageSearchResult,
  SendMessageResponse,
} from '@kp-administration/itl-messages/model';
import { PagedResults } from '@registry-web/shared/search/util/search-service.util';

describe('MessageApiService', () => {
  let service: MessageApiService;
  let httpMock: HttpTestingController;
  let baseApiUrl: string;
  let messagesListGetUrl;
  let messageGetUrl;
  let sendMessagePostUrl;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        { provide: UK_ETS_REGISTRY_API_BASE_URL, useValue: 'apiBaseUrl' },
        MessageApiService,
      ],
    });
    service = TestBed.inject(MessageApiService);
    httpMock = TestBed.inject(HttpTestingController);
    baseApiUrl = TestBed.inject(UK_ETS_REGISTRY_API_BASE_URL);
    messagesListGetUrl = `${baseApiUrl}/itl.messages.list`;
    messageGetUrl = `${baseApiUrl}/itl.messages.get`;
    sendMessagePostUrl = `${baseApiUrl}/itl.messages.send`;
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it(
    'should succesfully request message list',
    waitForAsync(() => {
      const mockedResponse: PagedResults<MessageSearchResult> = {
        totalResults: 1,
        items: [
          {
            messageId: 12,
            from: 'GB',
            to: 'ITL',
            messageDate: '2020-11-04T16:39:09.00',
            content: 'Type your content here.',
          },
        ],
      };
      service
        .search(
          {
            messageId: 12,
            messageDateFrom: '2020-09-04T16:39:09.00',
            messageDateTo: '2020-11-22T12:19:23.09',
          },
          { page: 0, pageSize: 10 },
          {
            sortField: 'messageId',
            sortDirection: 'ASC',
          }
        )
        .subscribe((res) => expect(res).toStrictEqual(mockedResponse));

      const req = httpMock.expectOne((r) =>
        r.url.startsWith(messagesListGetUrl)
      );
      expect(req.request.url).toBe(messagesListGetUrl);
      expect(req.request.method).toEqual('GET');
      expect(req.request.params.get('messageId')).toEqual('12');
      expect(req.request.params.get('messageDateFrom')).toEqual(
        '2020-09-04T16:39:09.00'
      );
      expect(req.request.params.get('messageDateTo')).toEqual(
        '2020-11-22T12:19:23.09'
      );
      req.flush(mockedResponse);
    })
  );

  it(
    'should succesfully request a message by id',
    waitForAsync(() => {
      const mockedResponse: MessageDetails = {
        messageId: 12,
        messageDate: '2020-09-04T16:39:09.00',
        content: 'A content',
      };
      service
        .fetchITLMessage('12')
        .subscribe((res) => expect(res).toStrictEqual(mockedResponse));

      const req = httpMock.expectOne((r) => r.url.startsWith(messageGetUrl));
      expect(req.request.url).toBe(messageGetUrl);
      expect(req.request.method).toEqual('GET');
      expect(req.request.params.get('messageId')).toEqual('12');
      req.flush(mockedResponse);
    })
  );

  it(
    'should succesfully send a message',
    waitForAsync(() => {
      const mockedResponse: SendMessageResponse = {
        messageId: 12,
        success: true,
      };
      const messageContent = 'A test message.';
      service
        .sendMessage(messageContent)
        .subscribe((res) => expect(res).toStrictEqual(mockedResponse));

      const req = httpMock.expectOne((r) =>
        r.url.startsWith(sendMessagePostUrl)
      );
      expect(req.request.url).toBe(sendMessagePostUrl);
      expect(req.request.method).toEqual('POST');
      expect(req.request.body).toEqual({ content: messageContent });
      req.flush(mockedResponse);
    })
  );

  afterEach(() => httpMock.verify());
});
