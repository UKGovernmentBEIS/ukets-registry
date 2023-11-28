import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { APP_BASE_HREF } from '@angular/common';
import { SelectAccountHolderUpdateTypeComponent } from '@account-management/account/account-holder-details-wizard/components/select-type';
import { UkRadioInputComponent } from '@shared/form-controls/uk-radio-input/uk-radio-input.component';
import { AccountHolderDetailsType } from '@account-management/account/account-holder-details-wizard/model';

describe('SelectAccountHolderUpdateTypeComponent', () => {
  let component: SelectAccountHolderUpdateTypeComponent;
  let fixture: ComponentFixture<SelectAccountHolderUpdateTypeComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [ReactiveFormsModule, FormsModule, RouterModule.forRoot([])],
        declarations: [
          SelectAccountHolderUpdateTypeComponent,
          UkRadioInputComponent,
        ],
        providers: [{ provide: APP_BASE_HREF, useValue: '/' }],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(SelectAccountHolderUpdateTypeComponent);
    component = fixture.componentInstance;
    component.updateType =
      AccountHolderDetailsType.ACCOUNT_HOLDER_UPDATE_DETAILS;
    component.updateTypes = [];
    component.routeData = {
      updateTypes: [
        {
          label: '',
          hint: '',
          value: '',
          enabled: true,
        },
      ],
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
