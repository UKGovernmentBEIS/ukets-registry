import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CheckUpdateAndSubmitComponent } from './check-update-and-submit.component';
import { SharedModule } from '@registry-web/shared/shared.module';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { ChangeDetectionStrategy } from '@angular/core';
import { DocumentUpdateType } from '@registry-web/documents/models/document-update-type.model';
import { of } from 'rxjs';

describe('CheckUpdateAndSubmitComponent', () => {
  let component: CheckUpdateAndSubmitComponent;
  let fixture: ComponentFixture<CheckUpdateAndSubmitComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CheckUpdateAndSubmitComponent, RouterModule, SharedModule],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            paramMap: of({}),
          },
        },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CheckUpdateAndSubmitComponent);
    component = fixture.componentInstance;
    component.categories = [
      {
        id: 1,
        name: 'category',
        order: 1,
        createdOn: new Date(),
        documents: [
          {
            id: 1,
            name: 'docname',
            title: 'title',
            order: 1,
            createdOn: new Date(),
          },
        ],
      },
    ];
    component.storedDocument = {
      id: 1,
      categoryId: 1,
      order: 1,
      title: 'title',
      file: null,
    };
    component.storedUpdateType = DocumentUpdateType.ADD_DOCUMENT;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should emit navigateTo event when navigateTo method is called', () => {
    spyOn(component.navigateToEmitter, 'emit');
    const value = 'somePath';
    component.navigateTo(value);
    expect(component.navigateToEmitter.emit).toHaveBeenCalledWith(value);
  });

  it('should emit submitRequest event when submitChanges method is called', () => {
    spyOn(component.submitRequest, 'emit');
    component.storedUpdateType = DocumentUpdateType.ADD_DOCUMENT;
    component.submitChanges();
    expect(component.submitRequest.emit).toHaveBeenCalledWith(
      DocumentUpdateType.ADD_DOCUMENT
    );
  });
});
