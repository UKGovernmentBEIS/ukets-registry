import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SearchReportButtonComponent } from './search-report-button.component';
import { ProtectPipe } from '@shared/pipes';
import { AuthApiService } from '@registry-web/auth/auth-api.service';
import { MockAuthApiService } from '../../../../../testing/mock-auth-api-service';
import { provideMockStore } from '@ngrx/store/testing';

describe('TaskReportComponent', () => {
  let component: SearchReportButtonComponent;
  let fixture: ComponentFixture<SearchReportButtonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SearchReportButtonComponent, ProtectPipe],
      providers: [
        { provide: AuthApiService, useValue: MockAuthApiService },
        provideMockStore(),
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SearchReportButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
