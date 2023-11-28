import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PublicationWizardsContainerComponent } from './publication-wizards-container.component';
import { ActivatedRoute } from '@angular/router';
import { provideMockStore } from '@ngrx/store/testing';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

const activatedRouteSpy = jest
  .fn()
  .mockImplementation((): Partial<ActivatedRoute> => ({}));

describe('PublicationWizardsContainerComponent', () => {
  let component: PublicationWizardsContainerComponent;
  let fixture: ComponentFixture<PublicationWizardsContainerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      declarations: [PublicationWizardsContainerComponent],
      providers: [
        provideMockStore(),
        { provide: ActivatedRoute, useValue: activatedRouteSpy },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PublicationWizardsContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
