import { DomainEventsComponent } from '@shared/components/event/domain-events/domain-events.component';
import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import { GdsDateTimeShortPipe } from '@shared/pipes';
import { IdentificationDocumentationListComponent } from '@shared/components/identification-documentation-list';
import { RouterTestingModule } from '@angular/router/testing';

describe('DomainEventsComponent', () => {
  let component: IdentificationDocumentationListComponent;
  let fixture: ComponentFixture<IdentificationDocumentationListComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [
          IdentificationDocumentationListComponent,
          GdsDateTimeShortPipe,
        ],
        imports: [RouterTestingModule],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(IdentificationDocumentationListComponent);
    component = fixture.componentInstance;
    component.documents = [
      {
        id: 1,
        name: 'File 1',
        type: 'Proof of identity',
        uploadedDate: new Date(),
      },
      {
        id: 2,
        name: 'File 2',
        type: 'Proof of residence',
        uploadedDate: new Date(),
      },
    ];
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should instantiate', () => {
    component = new IdentificationDocumentationListComponent();
    expect(component).toBeDefined();
  });

  it('should be equal to File 1', () => {
    expect(component.documents.length).toEqual(2);
    expect(component.documents[0].name).toEqual('File 1');
  });

  it('should be equal to File 2', () => {
    expect(component.documents.length).toEqual(2);
    expect(component.documents[1].name).toEqual('File 2');
  });
});
