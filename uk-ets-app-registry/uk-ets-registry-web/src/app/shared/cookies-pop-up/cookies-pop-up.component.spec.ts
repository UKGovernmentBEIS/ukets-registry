import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import { CookiesPopUpComponent } from './cookies-pop-up.component';
import { CookieService } from '@shared/services';
import { RouterTestingModule } from '@angular/router/testing';

const cookieService = new CookieService();

describe('CookiesPopUpComponent', () => {
  let component: CookiesPopUpComponent;
  let fixture: ComponentFixture<CookiesPopUpComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [CookiesPopUpComponent],
        providers: [{ provide: CookieService, useValue: cookieService }],
        imports: [RouterTestingModule],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(CookiesPopUpComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
