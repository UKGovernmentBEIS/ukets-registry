import { UserDetailsEffect } from './user-details.effect';
import { Action } from '@ngrx/store';
import { Observable } from 'rxjs';
import { TestBed } from '@angular/core/testing';
import { provideMockStore } from '@ngrx/store/testing';
import { UserDetailService } from '@user-management/service';
import { ExportFileService } from '@shared/export-file/export-file.service';
import { ApiErrorHandlingService } from '@shared/services';
import { provideMockActions } from '@ngrx/effects/testing';
import { RouterTestingModule } from '@angular/router/testing';

const userDetailServiceSpy = jest.fn().mockImplementation();
const exportFileServiceSpy = jest.fn().mockImplementation();
const apiErrorHandlingServiceSpy = jest.fn().mockImplementation();

describe('UserDetailsEffect', () => {
  let effect: UserDetailsEffect;
  const actions$ = new Observable<Action>();

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [
        UserDetailsEffect,
        provideMockStore(),
        provideMockActions(() => actions$),
        { provide: UserDetailService, useValue: userDetailServiceSpy },
        { provide: ExportFileService, useValue: exportFileServiceSpy },
        {
          provide: ApiErrorHandlingService,
          useValue: apiErrorHandlingServiceSpy,
        },
      ],
    }).compileComponents();
    effect = TestBed.inject(UserDetailsEffect);
  });

  test('api errors are converted to business errors', () => {
    const apiErrorBody = { errorDetails: [{ message: 'test' }] };
    expect(effect.apiErrorToBusinessError(apiErrorBody).error).toBe('test');
  });
});
