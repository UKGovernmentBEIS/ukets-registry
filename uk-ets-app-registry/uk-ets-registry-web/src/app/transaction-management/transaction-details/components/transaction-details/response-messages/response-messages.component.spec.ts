import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { ResponseMessagesComponent } from './response-messages.component';

describe('ResponseMessagesComponent', () => {
  let component: ResponseMessagesComponent;
  let fixture: ComponentFixture<ResponseMessagesComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [ResponseMessagesComponent],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(ResponseMessagesComponent);
    component = fixture.componentInstance;
    fixture.componentInstance.transactionResponses = [
      {
        errorCode: 404,
        details: 'Unknown',
        dateOccurred: '2020-03-11 16:23:27',
      },
    ];
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should instantiate', () => {
    const component: ResponseMessagesComponent = new ResponseMessagesComponent();
    expect(component).toBeDefined();
  });

  it('should be equal to errorCode 404', () => {
    expect(fixture.componentInstance.transactionResponses[0].errorCode).toEqual(
      404
    );
  });
});
