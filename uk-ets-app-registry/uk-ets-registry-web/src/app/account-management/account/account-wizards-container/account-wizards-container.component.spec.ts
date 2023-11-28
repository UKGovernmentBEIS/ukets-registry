import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AccountWizardsContainerComponent } from './account-wizards-container.component';
import { ActivatedRoute } from '@angular/router';
import { provideMockStore } from '@ngrx/store/testing';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

const activatedRouteSpy = jest
  .fn()
  .mockImplementation((): Partial<ActivatedRoute> => ({}));

describe('AccountWizardsContainerComponent', () => {
  let component: AccountWizardsContainerComponent;
  let fixture: ComponentFixture<AccountWizardsContainerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      declarations: [AccountWizardsContainerComponent],
      providers: [
        provideMockStore(),
        { provide: ActivatedRoute, useValue: activatedRouteSpy },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AccountWizardsContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
