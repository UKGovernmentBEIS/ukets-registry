import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import { DocumentsRequestSubmittedComponent } from './documents-request-submitted.component';
import { APP_BASE_HREF } from '@angular/common';
import { RouterModule } from '@angular/router';

describe('DocumentsRequestSubmittedComponent', () => {
  let component: DocumentsRequestSubmittedComponent;
  let fixture: ComponentFixture<DocumentsRequestSubmittedComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [RouterModule.forRoot([])],
        declarations: [DocumentsRequestSubmittedComponent],
        providers: [{ provide: APP_BASE_HREF, useValue: '/' }],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(DocumentsRequestSubmittedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
