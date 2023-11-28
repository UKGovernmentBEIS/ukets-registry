import { TestBed } from '@angular/core/testing';
import { provideMockActions } from '@ngrx/effects/testing';
import { Observable } from 'rxjs';

import { ReportsEffects } from './reports.effects';
import {
  UK_ETS_REGISTRY_API_BASE_URL,
  UK_ETS_REPORTS_API_BASE_URL,
} from '@registry-web/app.tokens';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ApiErrorHandlingService } from '@shared/services';
import { ExportFileService } from '@shared/export-file/export-file.service';
import { provideMockStore } from '@ngrx/store/testing';
import { KeycloakService } from 'keycloak-angular';
import { KeycloakServiceStub } from 'src/testing/mock-keycloak-service';

describe('ReportsEffects', () => {
  let actions$: Observable<any>;
  let effects: ReportsEffects;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        ReportsEffects,
        provideMockActions(() => actions$),
        {
          provide: UK_ETS_REPORTS_API_BASE_URL,
          useValue: 'ukEtsReportsApiBaseUrl',
        },
        {
          provide: UK_ETS_REGISTRY_API_BASE_URL,
          useValue: 'ukEtsRegistryApiBaseUrl',
        },
        ApiErrorHandlingService,
        ExportFileService,
        { provide: KeycloakService, useClass: KeycloakServiceStub },
        provideMockStore(),
      ],
      imports: [HttpClientTestingModule],
    });

    effects = TestBed.inject(ReportsEffects);
  });

  it('should be created', () => {
    expect(effects).toBeTruthy();
  });
});
