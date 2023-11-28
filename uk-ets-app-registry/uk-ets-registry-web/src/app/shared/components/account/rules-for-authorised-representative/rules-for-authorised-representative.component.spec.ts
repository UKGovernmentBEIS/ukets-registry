import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import { RulesForAuthorisedRepresentativeComponent } from '@shared/components/account/rules-for-authorised-representative';
import { RouterModule } from '@angular/router';
import { APP_BASE_HREF } from '@angular/common';
import { ProtectPipe } from '@shared/pipes';
import { AuthApiService } from '../../../../auth/auth-api.service';
import { MockAuthApiService } from '../../../../../testing/mock-auth-api-service';
import { provideMockStore } from '@ngrx/store/testing';

describe('RulesForAuthorisedRepresentativeComponent', () => {
  let component: RulesForAuthorisedRepresentativeComponent;
  let fixture: ComponentFixture<RulesForAuthorisedRepresentativeComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [RouterModule.forRoot([])],
        declarations: [RulesForAuthorisedRepresentativeComponent, ProtectPipe],
        providers: [
          provideMockStore(),
          { provide: APP_BASE_HREF, useValue: '/' },
          { provide: AuthApiService, useValue: MockAuthApiService },
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(
      RulesForAuthorisedRepresentativeComponent
    );
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
