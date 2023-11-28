import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { TimedOutComponent } from './timed-out.component';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import { ActivatedRoute, convertToParamMap, Router } from '@angular/router';

const routerSpy = jest.fn().mockImplementation((): Partial<Router> => ({}));

describe('TimedOutComponent', () => {
  let component: TimedOutComponent;
  let fixture: ComponentFixture<TimedOutComponent>;
  let store: MockStore;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [TimedOutComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: {
              snapshot: {
                queryParamMap: convertToParamMap({ idle: '30' }),
              },
            },
          },
          { provide: Router, useValue: routerSpy },
          provideMockStore(),
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(TimedOutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    store = TestBed.inject(MockStore);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should sign in again on button click', () => {
    const storeSpy = jest.spyOn(store, 'dispatch');
    const button = fixture.nativeElement.querySelector('button');

    button.click();

    expect(storeSpy).toHaveBeenCalledTimes(1);
  });
});
