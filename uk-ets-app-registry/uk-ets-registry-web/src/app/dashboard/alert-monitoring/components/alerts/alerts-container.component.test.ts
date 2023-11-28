import { ComponentFixture, TestBed } from '@angular/core/testing';
import { State, Store } from '@ngrx/store';
import { of } from 'rxjs';
import { AlertsContainerComponent } from './alerts-container.component';
import { selectAlerts } from '@registry-web/dashboard/alert-monitoring/reducers/alerts.selectors';
import { isAdmin } from '@registry-web/auth/auth.selector';
import { getAlerts } from '@registry-web/dashboard/alert-monitoring/actions';

describe('AlertsContainerComponent', () => {
  let component: AlertsContainerComponent;
  let fixture: ComponentFixture<AlertsContainerComponent>;
  let storeMock: jest.Mocked<Store>;

  beforeEach(async () => {
    // Create a mock instance for the Store
    storeMock = {
      select: jest.fn(),
      dispatch: jest.fn(),
    } as unknown as jest.Mocked<Store>;

    await TestBed.configureTestingModule({
      declarations: [AlertsContainerComponent],
      providers: [{ provide: Store, useValue: storeMock }],
    }).compileComponents();

    fixture = TestBed.createComponent(AlertsContainerComponent);
    component = fixture.componentInstance;
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize alerts$ with correct values when isAdmin is true and alerts exist', () => {
    // Mock the selectAlerts and isAdmin observables
    const alertsMock = {};
    const isAdminMock = true;
    storeMock.select.mockReturnValueOnce(of(alertsMock));
    storeMock.select.mockReturnValueOnce(of(isAdminMock));

    fixture.detectChanges(); // Trigger ngOnInit

    expect(component.alerts$).toBeDefined();
    component.alerts$.subscribe(([alerts, isAdminValue]) => {
      expect(alerts).toBe(alertsMock);
      expect(isAdminValue).toBe(isAdminMock);
    });
  });

  it('should dispatch getAlerts action when isAdmin is true and alerts do not exist', () => {
    // Mock the selectAlerts and isAdmin observables
    const alertsMock = null;
    const isAdminMock = true;
    storeMock.select.mockReturnValueOnce(of(alertsMock));
    storeMock.select.mockReturnValueOnce(of(isAdminMock));

    fixture.detectChanges(); // Trigger ngOnInit

    expect(component.alerts$).toBeDefined();
    component.alerts$.subscribe();

    expect(storeMock.dispatch).toHaveBeenCalledWith(getAlerts());
  });
});
