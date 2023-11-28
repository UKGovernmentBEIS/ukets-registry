import { TestBed } from '@angular/core/testing';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import { UploadStatus } from '@registry-web/shared/model/file';
import { EmissionsTableState } from '@emissions-table/store/reducers';
import { ClearEmissionsTableUploadGuard } from './clear-emissions-table-upload.guard';
import { RouterTestingModule } from '@angular/router/testing';

describe('ClearEmissionsTableUploadGuard', () => {
  let guard: ClearEmissionsTableUploadGuard;
  let store: MockStore;
  const initialState: EmissionsTableState = {
    fileHeader: {
      id: 24423,
      fileName: 'UK_Emissions_28062021_depra_1872689VXVXDWDW.xlsx',
      fileSize: '3534455',
    },
    status: UploadStatus.Completed,
    progress: null,
    requestId: null,
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [],
      imports: [RouterTestingModule],
      providers: [
        ClearEmissionsTableUploadGuard,
        provideMockStore({ initialState }),
      ],
    }).compileComponents();
    guard = TestBed.inject(ClearEmissionsTableUploadGuard);
    store = TestBed.inject(MockStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should return true', () => {
    expect(guard.canDeactivate(null, null, null)).toBeTruthy();
  });
});
