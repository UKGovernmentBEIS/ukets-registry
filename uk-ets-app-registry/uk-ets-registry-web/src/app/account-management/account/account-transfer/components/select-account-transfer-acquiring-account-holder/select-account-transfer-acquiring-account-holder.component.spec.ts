import { APP_BASE_HREF } from '@angular/common';
import {
  ComponentFixture,
  ComponentFixtureAutoDetect,
  TestBed,
} from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { SelectAccountTransferAcquiringAccountHolderComponent } from '@account-transfer/components/select-account-transfer-acquiring-account-holder/select-account-transfer-acquiring-account-holder.component';
import { TypeAheadComponent } from '@shared/form-controls/type-ahead/type-ahead.component';
import { SharedModule } from '@shared/shared.module';
import { NgbTypeaheadModule } from '@ng-bootstrap/ng-bootstrap';
import { provideMockStore } from '@ngrx/store/testing';

describe('SelectAccountTransferAcquiringAccountHolderComponent', () => {
  let component: SelectAccountTransferAcquiringAccountHolderComponent;
  let fixture: ComponentFixture<SelectAccountTransferAcquiringAccountHolderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule,
        NgbTypeaheadModule,
        SharedModule,
        FormsModule,
        RouterModule.forRoot([]),
      ],
      declarations: [
        SelectAccountTransferAcquiringAccountHolderComponent,
        TypeAheadComponent,
      ],
      providers: [
        { provide: APP_BASE_HREF, useValue: '/' },
        { provide: ComponentFixtureAutoDetect, useValue: true },
        provideMockStore(),
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(
      SelectAccountTransferAcquiringAccountHolderComponent
    );
    component = fixture.componentInstance;
  });

  it.skip('should create', () => {
    expect(component).toBeTruthy();
  });

  it.skip('should display the title', () => {
    const h1: HTMLElement = fixture.nativeElement.querySelector('h1');
    expect(h1.textContent).toContain('Choose the new account holder');
  });
});
