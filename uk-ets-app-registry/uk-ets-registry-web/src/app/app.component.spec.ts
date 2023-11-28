import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import { AppComponent } from './app.component';
import { Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { State } from './reducers';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { GOVUKFrontendService } from './app.govukfrontend';
import { RouterTestingModule } from '@angular/router/testing';
import { KeycloakService } from 'keycloak-angular';
import { provideMockStore } from '@ngrx/store/testing';

const routerSpy = jest.fn().mockImplementation((): Partial<Router> => ({}));

const storeSpy = jest
  .fn()
  .mockImplementation((): Partial<Store<State>> => ({}));

describe('AppComponent', () => {
  let fixture: ComponentFixture<AppComponent>;
  let app: AppComponent;
  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        schemas: [CUSTOM_ELEMENTS_SCHEMA],
        imports: [RouterTestingModule],
        declarations: [AppComponent],
        providers: [
          { provide: Store, useValue: storeSpy },
          { provide: GOVUKFrontendService, useValue: null },
          { provide: KeycloakService, useValue: null },
          provideMockStore(),
        ],
      }).compileComponents();
      fixture = TestBed.createComponent(AppComponent);
      app = fixture.debugElement.componentInstance;
    })
  );
  it('should create the app', () => {
    expect(app).toBeTruthy();
  });
});
