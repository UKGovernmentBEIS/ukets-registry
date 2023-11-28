import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';
import { AuthApiService } from '@registry-web/auth/auth-api.service';
import { KeycloakService } from 'keycloak-angular';
import { KeycloakServiceStub } from 'src/testing/mock-keycloak-service';

describe('AuthApiService', () => {
  let service: AuthApiService;
  let keycloakService: KeycloakService;
  let httpMock: HttpTestingController;
  let baseApiUrl: string;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        {
          provide: UK_ETS_REGISTRY_API_BASE_URL,
          useValue: 'ukEtsRegistryApiBaseUrl',
        },
        AuthApiService,
        { provide: KeycloakService, useClass: KeycloakServiceStub },
      ],
    });
    service = TestBed.inject(AuthApiService);
    httpMock = TestBed.inject(HttpTestingController);
    baseApiUrl = TestBed.inject(UK_ETS_REGISTRY_API_BASE_URL);
    keycloakService = TestBed.inject(KeycloakService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
