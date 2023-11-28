import { APP_BASE_HREF } from '@angular/common';
import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterModule } from '@angular/router';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import { MemoizedSelector } from '@ngrx/store';
import { By } from '@angular/platform-browser';
import { SendMessageConfirmationComponent } from '@kp-administration/itl-messages/components';
import * as fromSendMessage from '@kp-administration/store';

describe('SendMessageConfirmationComponent', () => {
  let component: SendMessageConfirmationComponent;
  let fixture: ComponentFixture<SendMessageConfirmationComponent>;
  let mockStore: MockStore;
  let mockMessageIdSelector: MemoizedSelector<
    fromSendMessage.SendMessageState,
    number
  >;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [SendMessageConfirmationComponent],
        imports: [RouterModule.forRoot([])],
        providers: [
          provideMockStore(),
          { provide: APP_BASE_HREF, useValue: '/' },
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(SendMessageConfirmationComponent);
    mockStore = TestBed.inject(MockStore);
    mockMessageIdSelector = mockStore.overrideSelector(
      fromSendMessage.selectMessageId,
      1024
    );
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  test('should render title in h1 tag', () => {
    const compiled = fixture.debugElement.nativeElement;
    expect(compiled.querySelector('h1').textContent).toContain(
      'You have sent a message to the ITL'
    );
  });

  test('should render info text in govuk-panel__body', () => {
    const infoText = fixture.debugElement.query(By.css('.govuk-panel__body'));
    expect(infoText.nativeElement.textContent).toContain(
      'The message ID is 1024'
    );
  });
});
