import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { provideMockStore } from '@ngrx/store/testing';
import { CancelUpdateRequestComponent } from '@shared/components/cancel-update-request';

import { CancelEmissionsTableUploadContainerComponent } from '.';

describe('CancelEmissionsTableUploadContainerComponent', () => {
  let component: CancelEmissionsTableUploadContainerComponent;
  let fixture: ComponentFixture<CancelEmissionsTableUploadContainerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        CancelEmissionsTableUploadContainerComponent,
        CancelUpdateRequestComponent,
      ],
      imports: [RouterTestingModule],
      providers: [provideMockStore()],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(
      CancelEmissionsTableUploadContainerComponent
    );
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
