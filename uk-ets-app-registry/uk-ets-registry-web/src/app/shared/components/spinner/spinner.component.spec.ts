import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AuthApiService } from '@registry-web/auth/auth-api.service';
import { MockAuthApiService } from '../../../../testing/mock-auth-api-service';
import { provideMockStore } from '@ngrx/store/testing';
import { SpinnerComponent } from '@shared/components/spinner/spinner.component';

describe('SpinnerComponent', () => {
  let component: SpinnerComponent;
  let fixture: ComponentFixture<SpinnerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SpinnerComponent],
      providers: [
        { provide: AuthApiService, useValue: MockAuthApiService },
        provideMockStore(),
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SpinnerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
