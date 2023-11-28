import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import {
  SelectAccessRightsComponent,
  SelectAccessRightsContainerComponent,
} from '@authorised-representatives/components';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { UkRadioInputComponent } from '@shared/form-controls/uk-radio-input/uk-radio-input.component';
import { provideMockStore } from '@ngrx/store/testing';
import { RouterModule } from '@angular/router';
import { APP_BASE_HREF } from '@angular/common';
import { CancelRequestLinkComponent } from '@shared/components/account/cancel-request-link';
import { AuthorisedRepresentativeUpdateTypePipe } from '@shared/pipes';

describe('SelectAccessRightsContainerComponent', () => {
  let component: SelectAccessRightsContainerComponent;
  let fixture: ComponentFixture<SelectAccessRightsContainerComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [FormsModule, ReactiveFormsModule, RouterModule.forRoot([])],
        declarations: [
          SelectAccessRightsContainerComponent,
          SelectAccessRightsComponent,
          AuthorisedRepresentativeUpdateTypePipe,
          UkRadioInputComponent,
          CancelRequestLinkComponent,
        ],
        providers: [
          provideMockStore(),
          { provide: APP_BASE_HREF, useValue: '/' },
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(SelectAccessRightsContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
